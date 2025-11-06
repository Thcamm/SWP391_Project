package dao.carservice;

import common.DbContext;
import model.dto.ServiceHistoryDTO;
import model.dto.ServiceRequestViewDTO;
import model.workorder.ServiceRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAO extends DbContext {

    // Tạo Request + danh sách Service (ghi vào ServiceRequestDetail)
    public int createServiceRequestWithDetails(ServiceRequest request, List<Integer> serviceIds) throws SQLException {
        int requestId;
        String sqlRequest = "INSERT INTO ServiceRequest (CustomerID, VehicleID, AppointmentID, Note) VALUES (?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO ServiceRequestDetail (RequestID, ServiceID) VALUES (?, ?)";

        try (Connection conn = DbContext.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                ps1.setInt(1, request.getCustomerID());
                ps1.setInt(2, request.getVehicleID());
                if (request.getAppointmentID() != null)
                    ps1.setInt(3, request.getAppointmentID());
                else
                    ps1.setNull(3, Types.INTEGER);
                if (request.getNote() != null)
                    ps1.setString(4, request.getNote());
                else
                    ps1.setNull(4, Types.VARCHAR);
                ps1.executeUpdate();
                try (ResultSet rs = ps1.getGeneratedKeys()) {
                    rs.next();
                    requestId = rs.getInt(1);
                }
            }

            if (serviceIds != null && !serviceIds.isEmpty()) {
                try (PreparedStatement ps2 = conn.prepareStatement(sqlDetail)) {
                    for (Integer sId : serviceIds) {
                        ps2.setInt(1, requestId);
                        ps2.setInt(2, sId);
                        ps2.addBatch();
                    }
                    ps2.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            // đảm bảo rollback nếu có lỗi
            // (nếu muốn chặt chẽ hơn: bắt riêng, rollback, rethrow)
            throw e;
        }
        return requestId;
    }

    // Lịch sử dịch vụ của khách: 1 dòng / chi tiết dịch vụ
    public List<ServiceHistoryDTO> getServiceHistoryByCustomerId(int customerId) throws SQLException {
        List<ServiceHistoryDTO> history = new ArrayList<>();
        String sql = "SELECT sr.RequestID, st.ServiceName, sr.RequestDate, sr.Status, st.UnitPrice " +
                "FROM ServiceRequest sr " +
                "JOIN ServiceRequestDetail srd ON srd.RequestID = sr.RequestID " +
                "JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                "WHERE sr.CustomerID = ? " +
                "ORDER BY sr.RequestDate DESC, sr.RequestID DESC";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ServiceHistoryDTO item = new ServiceHistoryDTO();
                    item.setRequestId(rs.getInt("RequestID"));
                    item.setServiceName(rs.getString("ServiceName"));
                    item.setRequestDate(rs.getTimestamp("RequestDate"));
                    item.setStatus(rs.getString("Status"));
                    item.setPrice(rs.getDouble("UnitPrice"));
                    history.add(item);
                }
            }
        }
        return history;
    }

    // Danh sách request cho màn hình View: gom tên dịch vụ và tổng tiền -> 1 dòng /
    // Request
    public List<ServiceRequestViewDTO> getAllServiceRequestsForView() throws SQLException {
        List<ServiceRequestViewDTO> requestList = new ArrayList<>();
        String sql = "SELECT sr.RequestID, sr.RequestDate, sr.Status, " +
                "       u.FullName AS CustomerName, " +
                "       CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "       GROUP_CONCAT(DISTINCT st.ServiceName ORDER BY st.ServiceName SEPARATOR ', ') AS ServiceNames, "
                +
                "       COALESCE(SUM(st.UnitPrice), 0) AS TotalPrice " +
                "FROM ServiceRequest sr " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "LEFT JOIN ServiceRequestDetail srd ON srd.RequestID = sr.RequestID " +
                "LEFT JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                "GROUP BY sr.RequestID, sr.RequestDate, sr.Status, u.FullName, VehicleInfo " +
                "ORDER BY sr.RequestDate DESC";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ServiceRequestViewDTO dto = new ServiceRequestViewDTO();
                dto.setRequestId(rs.getInt("RequestID"));
                dto.setRequestDate(rs.getTimestamp("RequestDate"));
                dto.setStatus(rs.getString("Status"));
                dto.setCustomerName(rs.getString("CustomerName"));
                dto.setVehicleInfo(rs.getString("VehicleInfo"));
                // map vào các field sẵn có
                dto.setServiceName(rs.getString("ServiceNames")); // chuỗi tên dịch vụ
                dto.setServicePrice(rs.getDouble("TotalPrice")); // tổng tiền
                requestList.add(dto);
            }
        }
        return requestList;
    }

    public boolean updateServiceRequestStatus(int requestId, String newStatus) throws SQLException {
        String sql = "UPDATE ServiceRequest SET Status = ? WHERE RequestID = ?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        }
    }

    // ========== TRANSACTIONAL METHODS (for TechManager approval flow) ==========

    /**
     * Lấy ServiceRequest FOR UPDATE trong transaction.
     * LƯU Ý: ServiceID đã bị loại bỏ, không set vào model nữa.
     */
    public ServiceRequest getServiceRequestForUpdate(Connection conn, int requestId) throws SQLException {
        String sql = "SELECT * FROM ServiceRequest WHERE RequestID = ? FOR UPDATE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ServiceRequest sr = new ServiceRequest();
                    sr.setRequestID(rs.getInt("RequestID"));
                    sr.setCustomerID(rs.getInt("CustomerID"));
                    sr.setVehicleID(rs.getInt("VehicleID"));
                    // sr.setServiceID(...) // BỎ: cột đã xóa
                    sr.setAppointmentID(rs.getObject("AppointmentID", Integer.class));
                    sr.setRequestDate(rs.getTimestamp("RequestDate"));
                    sr.setStatus(rs.getString("Status"));
                    // (nếu model có field Note) -> set Note
                    try {
                        sr.setNote(rs.getString("Note"));
                    } catch (Exception ignore) {
                        /* nếu model chưa có Note thì bỏ qua */ }
                    return sr;
                }
            }
        }
        return null;
    }

    /**
     * Update status trong transaction (giữ nguyên)
     */
    public boolean updateServiceRequestStatus(Connection conn, int requestId, String newStatus) throws SQLException {
        String sql = "UPDATE ServiceRequest SET Status = ? WHERE RequestID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Danh sách các request PENDING cho TechManager: gom dịch vụ + tổng tiền
     */
    public List<ServiceRequestViewDTO> getPendingServiceRequests() throws SQLException {
        List<ServiceRequestViewDTO> requestList = new ArrayList<>();
        String sql = "SELECT sr.RequestID, sr.RequestDate, sr.Status, " +
                "       u.FullName AS CustomerName, u.PhoneNumber, " +
                "       CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "       GROUP_CONCAT(DISTINCT st.ServiceName ORDER BY st.ServiceName SEPARATOR ', ') AS ServiceNames, "
                +
                "       COALESCE(SUM(st.UnitPrice), 0) AS TotalPrice, " +
                "       sr.CustomerID, sr.VehicleID " +
                "FROM ServiceRequest sr " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "LEFT JOIN ServiceRequestDetail srd ON srd.RequestID = sr.RequestID " +
                "LEFT JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                "WHERE sr.Status = 'PENDING' " +
                "GROUP BY sr.RequestID, sr.RequestDate, sr.Status, u.FullName, u.PhoneNumber, VehicleInfo, sr.CustomerID, sr.VehicleID "
                +
                "ORDER BY sr.RequestDate ASC";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ServiceRequestViewDTO dto = new ServiceRequestViewDTO();
                dto.setRequestId(rs.getInt("RequestID"));
                dto.setRequestDate(rs.getTimestamp("RequestDate"));
                dto.setStatus(rs.getString("Status"));
                dto.setCustomerName(rs.getString("CustomerName"));
                dto.setVehicleInfo(rs.getString("VehicleInfo"));
                dto.setServiceName(rs.getString("ServiceNames"));
                dto.setServicePrice(rs.getDouble("TotalPrice"));
                requestList.add(dto);
            }
        }
        return requestList;
    }
}

package dao.carservice;

import common.DbContext;
import model.dto.ServiceHistoryDTO;
import model.dto.ServiceRequestViewDTO;
import model.workorder.ServiceRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAO extends DbContext {

    public int createServiceRequestWithDetails(ServiceRequest request, List<Integer> serviceIds) throws SQLException {
        int requestId;
        String sqlRequest = "INSERT INTO ServiceRequest (CustomerID, VehicleID, AppointmentID,Note) VALUES (?, ?, ?,?)";
        String sqlDetail = "INSERT INTO ServiceRequestDetail (RequestID, ServiceID) VALUES (?, ?)";

        try (Connection conn = DbContext.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(sqlRequest, Statement.RETURN_GENERATED_KEYS)) {
                ps1.setInt(1, request.getCustomerID());
                ps1.setInt(2, request.getVehicleID());
                if (request.getAppointmentID() != null)
                    ps1.setInt(3, request.getAppointmentID());
                else
                    ps1.setNull(3, java.sql.Types.INTEGER);
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

            if (serviceIds != null) {
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
            // Cân nhắc rollback ở đây nếu conn chưa bị đóng
            throw e;
        }
        return requestId;
    }

    public List<ServiceHistoryDTO> getServiceHistoryByCustomerId(int customerId) throws SQLException {
        List<ServiceHistoryDTO> history = new ArrayList<>();
        // Đã JOIN qua ServiceRequestDetail và dùng GROUP_CONCAT
        String sql = "SELECT sr.RequestID, sr.RequestDate, sr.Status, " +
                "GROUP_CONCAT(st.ServiceName SEPARATOR ', ') AS ServiceName, " +
                "SUM(st.UnitPrice) AS UnitPrice " +
                "FROM ServiceRequest sr " +
                "JOIN ServiceRequestDetail srd ON sr.RequestID = srd.RequestID " +
                "JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                "WHERE sr.CustomerID = ? " +
                "GROUP BY sr.RequestID, sr.RequestDate, sr.Status " +
                "ORDER BY sr.RequestDate DESC";

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

    public List<ServiceRequestViewDTO> getAllServiceRequestsForView() throws SQLException {
        List<ServiceRequestViewDTO> requestList = new ArrayList<>();
        // Đã JOIN qua ServiceRequestDetail và dùng GROUP_CONCAT
        String sql = "SELECT sr.RequestID, sr.RequestDate, sr.Status, " +
                "u.FullName AS CustomerName, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "GROUP_CONCAT(st.ServiceName SEPARATOR ', ') AS ServiceName, " +
                "SUM(st.UnitPrice) AS UnitPrice " +
                "FROM ServiceRequest sr " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN ServiceRequestDetail srd ON sr.RequestID = srd.RequestID " +
                "JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
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
                dto.setServiceName(rs.getString("ServiceName"));
                dto.setServicePrice(rs.getDouble("UnitPrice"));
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

    /**
     * Get ServiceRequest by ID with FOR UPDATE lock (use within transaction)
     * Returns the current status of the request, or null if not found
     */
    // *** ĐÃ SỬA ***
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
                    sr.setAppointmentID(rs.getObject("AppointmentID", Integer.class));
                    sr.setRequestDate(rs.getTimestamp("RequestDate"));
                    sr.setStatus(rs.getString("Status"));
                    // Bạn có thể thêm sr.setNote(rs.getString("Note")); nếu model ServiceRequest có
                    return sr;
                }
            }
        }
        return null;
    }

    /**
     * Update status using provided connection (for transactions)
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
     * Get all PENDING service requests for TechManager to review
     */
    public List<ServiceRequestViewDTO> getPendingServiceRequests() throws SQLException {
        List<ServiceRequestViewDTO> requestList = new ArrayList<>();
        String sql = "SELECT sr.RequestID, sr.RequestDate, sr.Status, " +
                "u.FullName AS CustomerName, u.PhoneNumber, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "GROUP_CONCAT(st.ServiceName SEPARATOR ', ') AS ServiceName, " +
                "SUM(st.UnitPrice) AS UnitPrice, " +
                "sr.CustomerID, sr.VehicleID, sr.Note " + // Thêm sr.Note nếu cần hiển thị
                "FROM ServiceRequest sr " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN ServiceRequestDetail srd ON sr.RequestID = srd.RequestID " +
                "JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                "WHERE sr.Status = 'PENDING' " +
                "GROUP BY sr.RequestID, sr.RequestDate, sr.Status, u.FullName, u.PhoneNumber, VehicleInfo, sr.CustomerID, sr.VehicleID, sr.Note "
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
                dto.setServiceName(rs.getString("ServiceName"));
                dto.setServicePrice(rs.getDouble("UnitPrice"));

                // dto.setNote(rs.getString("Note"));
                requestList.add(dto);
            }
        }
        return requestList;
    }
}
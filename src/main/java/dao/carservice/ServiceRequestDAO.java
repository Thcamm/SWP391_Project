package dao.carservice;

import common.DbContext;
import model.servicetype.ServiceHistoryDTO;
import model.servicetype.ServiceRequestViewDTO;
import model.workorder.ServiceRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAO extends DbContext {

    public int createServiceRequest(ServiceRequest request) throws SQLException {

        String sql = "INSERT INTO servicerequest (CustomerID, VehicleID, ServiceID, AppointmentID) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, request.getCustomerID());
            ps.setInt(2, request.getVehicleID());
            ps.setInt(3, request.getServiceID());

            if (request.getAppointmentID() != null) {
                ps.setInt(4, request.getAppointmentID());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public List<ServiceHistoryDTO> getServiceHistoryByCustomerId(int customerId) throws SQLException {
        List<ServiceHistoryDTO> history = new ArrayList<>();
        String sql = "SELECT sr.RequestID, st.ServiceName, sr.RequestDate, sr.Status, st.UnitPrice " +
                "FROM ServiceRequest sr " +
                "JOIN Service_Type st ON sr.ServiceID = st.ServiceID " +
                "WHERE sr.CustomerID = ? " +
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
        String sql = "SELECT sr.RequestID, sr.RequestDate, sr.Status, " +
                "u.FullName AS CustomerName, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "st.ServiceName, st.UnitPrice " +
                "FROM ServiceRequest sr " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Service_Type st ON sr.ServiceID = st.ServiceID " +
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
}

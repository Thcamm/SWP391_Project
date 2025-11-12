package dao.employee.techmanager;

import common.DbContext;
import model.employee.techmanager.PendingServiceRequestDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for TechManager to view and manage pending service requests
 */
public class ServiceRequestDAO {

    /**
     * Get all pending service requests with full customer and vehicle information
     * 
     * LUỒNG 4.0: ServiceRequest no longer has ServiceID column
     * Services are now in ServiceRequestDetail table (1 request → N services)
     * 
     * @return list of pending service requests
     * @throws SQLException if database error occurs
     */
    public List<PendingServiceRequestDTO> getPendingServiceRequests() throws SQLException {
        List<PendingServiceRequestDTO> requests = new ArrayList<>();

        String sql = "SELECT " +
                "sr.RequestID, sr.CustomerID, sr.VehicleID, " +
                "sr.AppointmentID, sr.RequestDate, sr.Status, " +
                "u.FullName as CustomerName, u.PhoneNumber, " +
                "v.LicensePlate, v.Brand, v.Model, v.YearManufacture " +
                "FROM ServiceRequest sr " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "WHERE sr.Status = 'PENDING' " +
                "ORDER BY sr.RequestDate ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PendingServiceRequestDTO dto = new PendingServiceRequestDTO();
                dto.setRequestId(rs.getInt("RequestID"));
                dto.setCustomerId(rs.getInt("CustomerID"));
                dto.setCustomerName(rs.getString("CustomerName"));
                dto.setPhoneNumber(rs.getString("PhoneNumber"));
                dto.setVehicleId(rs.getInt("VehicleID"));
                dto.setLicensePlate(rs.getString("LicensePlate"));
                dto.setVehicleBrand(rs.getString("Brand"));
                dto.setVehicleModel(rs.getString("Model"));
                dto.setYearManufacture(rs.getInt("YearManufacture"));
                dto.setRequestDate(rs.getTimestamp("RequestDate"));
                dto.setStatus(rs.getString("Status"));

                // AppointmentID can be null
                int apptId = rs.getInt("AppointmentID");
                dto.setAppointmentId(rs.wasNull() ? null : apptId);

                // Service info will be loaded separately via AJAX when user clicks "Approve &
                // Classify"
                // No need to set serviceId, serviceName, etc. here

                requests.add(dto);
            }
        }

        return requests;
    }

    /**
     * Get a single service request by ID with full details
     * 
     * @param requestId the service request ID
     * @return service request DTO or null if not found
     * @throws SQLException if database error occurs
     */
    public PendingServiceRequestDTO getServiceRequestById(int requestId) throws SQLException {
        String sql = "SELECT " +
                "sr.RequestID, sr.CustomerID, sr.VehicleID, sr.ServiceID, " +
                "sr.AppointmentID, sr.RequestDate, sr.Status, " +
                "u.FullName as CustomerName, u.PhoneNumber, " +
                "v.LicensePlate, v.Brand, v.Model, v.YearManufacture, " +
                "st.ServiceName, st.Category, st.UnitPrice " +
                "FROM ServiceRequest sr " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Service_Type st ON sr.ServiceID = st.ServiceID " +
                "WHERE sr.RequestID = ?";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PendingServiceRequestDTO dto = new PendingServiceRequestDTO();
                    dto.setRequestId(rs.getInt("RequestID"));
                    dto.setCustomerId(rs.getInt("CustomerID"));
                    dto.setCustomerName(rs.getString("CustomerName"));
                    dto.setPhoneNumber(rs.getString("PhoneNumber"));
                    dto.setVehicleId(rs.getInt("VehicleID"));
                    dto.setLicensePlate(rs.getString("LicensePlate"));
                    dto.setVehicleBrand(rs.getString("Brand"));
                    dto.setVehicleModel(rs.getString("Model"));
                    dto.setYearManufacture(rs.getInt("YearManufacture"));
                    dto.setServiceId(rs.getInt("ServiceID"));
                    dto.setServiceName(rs.getString("ServiceName"));
                    dto.setServiceCategory(rs.getString("Category"));
                    dto.setServiceUnitPrice(rs.getDouble("UnitPrice"));
                    dto.setRequestDate(rs.getTimestamp("RequestDate"));
                    dto.setStatus(rs.getString("Status"));

                    int apptId = rs.getInt("AppointmentID");
                    dto.setAppointmentId(rs.wasNull() ? null : apptId);

                    return dto;
                }
            }
        }

        return null;
    }

    /**
     * Update service request status - Transaction version
     * 
     * @param conn      Database connection for transaction
     * @param requestId Service request ID
     * @param newStatus New status (APPROVE, DECLINE, etc.)
     * @throws SQLException if update fails
     */
    public void updateServiceRequestStatus(Connection conn, int requestId, String newStatus) throws SQLException {
        String sql = "UPDATE ServiceRequest SET Status = ? WHERE RequestID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Failed to update service request status, no rows affected");
            }
        }
    }
}

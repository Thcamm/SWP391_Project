package dao.carservice;

import common.DbContext;
import model.servicerequest.ServiceRequest;

import java.sql.*;

public class ServiceRequestDAO extends DbContext {

    public int createServiceRequest(ServiceRequest request) throws SQLException {
        String sql = "INSERT INTO ServiceRequest (CustomerID, VehicleID, ServiceID, AppointmentID, Status) VALUES (?, ?, ?, ?, 'PENDING')";

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

            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Trả về RequestID mới
                    }
                }
            }
        }
        return -1;
    }
}
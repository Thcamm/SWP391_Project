package dao.appointment;

import common.DbContext;
import model.appointment.Appointment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentDAO extends DbContext {

    public List<Map<String, Object>> getAllAppointments() {
        String sql = "SELECT a.AppointmentID, a.CustomerID, a.VehicleID, a.Date, a.Status, a.Description, " +
                "u.FullName AS customerName " +
                "FROM Appointment a " +
                "JOIN Customer c ON a.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "ORDER BY a.Date DESC";

        List<Map<String, Object>> resultList = new ArrayList<>();

        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();

                Appointment appointment = new Appointment();
                appointment.setAppointmentID(rs.getInt("AppointmentID"));
                appointment.setCustomerID(rs.getInt("CustomerID"));
                appointment.setVehicleID(rs.getInt("VehicleID"));
                appointment.setAppointmentDate(rs.getTimestamp("Date").toLocalDateTime());
                appointment.setStatus(rs.getString("Status"));
                appointment.setDescription(rs.getString("Description"));

                row.put("appointment", appointment);
                row.put("customerName", rs.getString("customerName"));

                resultList.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách cuộc hẹn với tên khách hàng", e);
        }

        return resultList;
    }

    public Appointment getAppointmentById(int appointmentID) {
        String sql = "SELECT * FROM Appointment WHERE AppointmentID = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, appointmentID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentID(rs.getInt("AppointmentID"));
                appointment.setCustomerID(rs.getInt("CustomerID"));
                appointment.setVehicleID(rs.getInt("VehicleID"));
                appointment.setAppointmentDate(rs.getTimestamp("Date").toLocalDateTime());
                appointment.setStatus(rs.getString("Status"));
                appointment.setDescription(rs.getString("Description"));
                return appointment;
            } else {
                // Xử lý trường hợp không tìm thấy cuộc hẹn
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy cuộc hẹn theo ID", e);
        }
        return null;
    }

    public void insertAppointment(Appointment appointment) {
        String sql = "INSERT INTO Appointment (CustomerID, VehicleID, Date, Status, Description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, appointment.getCustomerID());
            st.setInt(2, appointment.getVehicleID());
            // Kiểm tra null và set date an toàn
            if (appointment.getAppointmentDate() != null) {
                st.setTimestamp(3, java.sql.Timestamp.valueOf(appointment.getAppointmentDate()));
            } else {
                throw new IllegalArgumentException("AppointmentService date cannot be null");
            }
            st.setString(4, appointment.getStatus());
            st.setString(5, appointment.getDescription());
            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Hiển thị chi tiết lỗi SQL
            throw new RuntimeException("Lỗi khi thêm cuộc hẹn: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Ngày hẹn không hợp lệ", e);
        }
    }

    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE Appointment SET CustomerID = ?, VehicleID = ?, Date = ?, Status = ?, Description = ? WHERE AppointmentID = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, appointment.getCustomerID());
            st.setInt(2, appointment.getVehicleID());
            st.setTimestamp(3, java.sql.Timestamp.valueOf(appointment.getAppointmentDate()));
            st.setString(4, appointment.getStatus());
            st.setString(5, appointment.getDescription());
            st.setInt(6, appointment.getAppointmentID());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật cuộc hẹn", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAppointment(int appointmentID) {
        String sql = "DELETE FROM Appointment WHERE AppointmentID = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, appointmentID);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa cuộc hẹn", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void getAppointmentsByCustomerId(int customerID) {
        String sql = "SELECT * FROM Appointment WHERE CustomerID = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, customerID);
            ResultSet rs = st.executeQuery();
            List<Appointment> appointments = new ArrayList<>();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentID(rs.getInt("AppointmentID"));
                appointment.setCustomerID(rs.getInt("CustomerID"));
                appointment.setVehicleID(rs.getInt("VehicleID"));
                appointment.setAppointmentDate(rs.getTimestamp("Date").toLocalDateTime());
                appointment.setStatus(rs.getString("Status"));
                appointment.setDescription(rs.getString("Description"));
                appointments.add(appointment);
            }
            // Xử lý danh sách cuộc hẹn theo nhu cầu của bạn
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách cuộc hẹn theo ID khách hàng", e);
        }
    }

    //
    // public boolean updateStatus(int appointmentId, String newStatus) {
    // String sql = "UPDATE appointment SET status = ? WHERE appointment_id = ?";
    // try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
    // ps.setString(1, newStatus);
    // ps.setInt(2, appointmentId);
    // return ps.executeUpdate() > 0;
    // } catch (SQLException e) {
    // e.printStackTrace();
    // return false;
    // }
    // }
    public List<Map<String, Object>> searchAppointment(String customerName, String fromDate, String toDate,
            String[] statusList, String sortOrder) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT a.AppointmentID, a.CustomerID, a.VehicleID, a.Date, a.Status, a.Description, " +
                        "u.FullName AS customerName " +
                        "FROM Appointment a " +
                        "JOIN Customer c ON a.CustomerID = c.CustomerID " +
                        "JOIN User u ON c.UserID = u.UserID " +
                        "WHERE 1=1 ");

        if (customerName != null && !customerName.isEmpty())
            sql.append("AND u.FullName LIKE ?  ");
        if (fromDate != null && !fromDate.isEmpty())
            sql.append("AND a.Date >= ? ");
        if (toDate != null && !toDate.isEmpty())
            sql.append("AND a.Date <= ? ");
        if (statusList != null && statusList.length > 0) {
            sql.append("AND a.Status IN (");
            for (int i = 0; i < statusList.length; i++) {
                sql.append("?");
                if (i < statusList.length - 1)
                    sql.append(",");
            }
            sql.append(") ");
        }

        sql.append("ORDER BY a.Date ");
        sql.append("oldest".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC");

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            if (customerName != null && !customerName.isEmpty())
                ps.setString(index++, "%" + customerName + "%");
            if (fromDate != null && !fromDate.isEmpty())
                ps.setString(index++, fromDate);
            if (toDate != null && !toDate.isEmpty())
                ps.setString(index++, toDate);
            if (statusList != null && statusList.length > 0) {
                for (String status : statusList)
                    ps.setString(index++, status.toUpperCase());
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                // Tạo AppointmentService object như bình thường
                Appointment apm = new Appointment();
                apm.setAppointmentID(rs.getInt("AppointmentID"));
                apm.setCustomerID(rs.getInt("CustomerID"));
                apm.setVehicleID(rs.getInt("VehicleID"));
                apm.setAppointmentDate(rs.getTimestamp("Date").toLocalDateTime());
                apm.setStatus(rs.getString("Status"));
                apm.setDescription(rs.getString("Description"));

                row.put("appointment", apm);
                row.put("customerName", rs.getString("customerName")); // thông tin khách hàng
                resultList.add(row);
            }
        }

        return resultList;
    }

    public boolean updateStatus(int appointmentID, String status) {
        String sql = "UPDATE Appointment SET Status = ? WHERE AppointmentID = ?";
        try (
                PreparedStatement ps = getConnection().prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, appointmentID);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

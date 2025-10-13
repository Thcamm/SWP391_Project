package dao.appointment;

import common.DbContext;
import model.appointment.Appointment;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AppointmentDAO extends DbContext {

    public List<Appointment> getAllAppointments() {
        String sql = "SELECT * FROM Appointment ORDER BY Date DESC";
        List<Appointment> appointments = new ArrayList<>();

        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentID(rs.getInt("AppointmentID"));
                appointment.setCustomerID(rs.getInt("CustomerID"));
                appointment.setVehicleID(rs.getInt("VehicleID"));

                // vì cột trong DB là DATETIME nên dùng getTimestamp thay vì getDate
                appointment.setAppointmentDate(
                        rs.getTimestamp("Date").toLocalDateTime().toLocalDate()
                );

                appointment.setStatus(rs.getString("Status"));
                appointment.setDescription(rs.getString("Description"));
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách cuộc hẹn", e);
        }

        return appointments;
    }




    public void getAppointmentById(int appointmentID) {
        String sql = "SELECT * FROM Appointment WHERE AppointmentID = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, appointmentID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setAppointmentID(rs.getInt("AppointmentID"));
                appointment.setCustomerID(rs.getInt("CustomerID"));
                appointment.setVehicleID(rs.getInt("VehicleID"));
                appointment.setAppointmentDate(rs.getDate("Date").toLocalDate());
                appointment.setStatus(rs.getString("Status"));
                appointment.setDescription(rs.getString("Description"));
                // Xử lý cuộc hẹn theo nhu cầu của bạn
            } else {
                // Xử lý trường hợp không tìm thấy cuộc hẹn
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy cuộc hẹn theo ID", e);
        }
    }

    public void insertAppointment(Appointment appointment) {
        String sql = "INSERT INTO Appointment (CustomerID, VehicleID, Date, Status, Description) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, appointment.getCustomerID());
            st.setInt(2, appointment.getVehicleID());
            // Kiểm tra null và set date an toàn
            if (appointment.getAppointmentDate() != null) {
                st.setDate(3, java.sql.Date.valueOf(appointment.getAppointmentDate()));
            } else {
                throw new IllegalArgumentException("Appointment date cannot be null");
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
            st.setDate(3, java.sql.Date.valueOf(appointment.getAppointmentDate()));
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
                appointment.setAppointmentDate(rs.getDate("Date").toLocalDate());
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
//    public boolean updateStatus(int appointmentId, String newStatus) {
//        String sql = "UPDATE appointment SET status = ? WHERE appointment_id = ?";
//        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
//            ps.setString(1, newStatus);
//            ps.setInt(2, appointmentId);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    public List<Appointment> searchAppointment(String fromDate, String toDate,
                                               String[] statusList, String sortOrder) throws SQLException {

        List<Appointment> list = new ArrayList<>();


        StringBuilder sql = new StringBuilder("SELECT * FROM Appointment WHERE 1=1");

        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND Date >= ?");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND Date <= ?");
        }

        if (statusList != null && statusList.length > 0) {
            sql.append(" AND Status IN (");
            for (int i = 0; i < statusList.length; i++) {
                sql.append("?");
                if (i < statusList.length - 1) sql.append(", ");
            }
            sql.append(")");
        }

        if ("oldest".equalsIgnoreCase(sortOrder)) {
            sql.append(" ORDER BY Date ASC");
        } else {
            sql.append(" ORDER BY Date DESC");
        }

        try (PreparedStatement st = getConnection().prepareStatement(sql.toString())){
            int index = 1;
            if (fromDate != null && !fromDate.isEmpty()) {
                st.setString(index++, fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                st.setString(index++, toDate);
            }
            if (statusList != null && statusList.length > 0) {
                for (String status : statusList) {
                    st.setString(index++, status.toUpperCase());
                }
            }

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setAppointmentID(rs.getInt("AppointmentID"));
                a.setCustomerID(rs.getInt("CustomerID"));
                a.setVehicleID(rs.getInt("VehicleID"));
                a.setAppointmentDate(
                        rs.getTimestamp("Date").toLocalDateTime().toLocalDate()
                );
                a.setStatus(rs.getString("Status"));
                a.setDescription(rs.getString("Description"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách cuộc hẹn", e);
        }
        return list;
    }
}

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

    public int countAppointment() {
        String sql = "SELECT COUNT(*) FROM Appointment";
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public List<Map<String, Object>> getAllAppointmentsWithLimit(int limit, int offset) {
        String sql = "SELECT a.AppointmentID, a.CustomerID, a.VehicleID, a.Date, a.Status, a.Description,a.RescheduleCount, " +
                "u.FullName AS customerName " +
                "FROM Appointment a " +
                "JOIN Customer c ON a.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "ORDER BY a.Date DESC LIMIT ? OFFSET ?";

        List<Map<String, Object>> resultList = new ArrayList<>();

        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, limit);
            st.setInt(2, offset);
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
                appointment.setRescheduleCount(rs.getInt("RescheduleCount"));
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

    public List<String> getAllStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        String sql = "SHOW COLUMNS FROM Appointment LIKE 'Status'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String type = rs.getString("Type"); // enum('Pending','InProgress',...)
                type = type.replaceAll("^enum\\('", "")
                        .replaceAll("'\\)$", "")
                        .replace("'", "");
                String[] parts = type.split(",");
                for (String s : parts) {
                    statuses.add(s.trim());
                }
            }
        }
        return statuses;
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
                appointment.setRescheduleCount(rs.getInt("RescheduleCount"));
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
        String sql = "UPDATE Appointment " +
                "SET Date = ?, Status = ?, Description = ?, RescheduleCount = RescheduleCount + 1 " +
                "WHERE AppointmentID = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setTimestamp(1, java.sql.Timestamp.valueOf(appointment.getAppointmentDate()));
            st.setString(2, appointment.getStatus());
            st.setString(3, appointment.getDescription());
            st.setInt(4, appointment.getAppointmentID());
            st.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating appointment", e);
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

    public List<Appointment> getAppointmentsByCustomerId(int customerID) {
        String sql = "SELECT * FROM Appointment WHERE CustomerID = ? ORDER BY Date DESC";
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
                appointment.setRescheduleCount(rs.getInt("RescheduleCount"));
                appointments.add(appointment);

            }
            return appointments;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách cuộc hẹn theo ID khách hàng", e);
        }
    }

    public List<Appointment> getAppointmentByFilter(
            int customerId, String fromDate, String toDate, String status, String sortOrder) {

        StringBuilder sql = new StringBuilder("SELECT * FROM Appointment WHERE 1=1");
        List<Appointment> appointments = new ArrayList<>();

        if (customerId > 0)
            sql.append(" AND CustomerID = ?");
        if (fromDate != null && !fromDate.isEmpty())
            sql.append(" AND Date >= ?");
        if (toDate != null && !toDate.isEmpty())
            sql.append(" AND Date <= ?");
        if (status != null && !status.isEmpty())
            sql.append(" AND Status = ?");

        sql.append(" ORDER BY Date ")
                .append("oldest".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC");

        try (PreparedStatement st = getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            if (customerId > 0)
                st.setInt(index++, customerId);
            if (fromDate != null && !fromDate.isEmpty())
                st.setString(index++, fromDate);
            if (toDate != null && !toDate.isEmpty())
                st.setString(index++, toDate);
            if (status != null && !status.isEmpty())
                st.setString(index++, status);

            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setAppointmentID(rs.getInt("AppointmentID"));
                a.setCustomerID(rs.getInt("CustomerID"));
                a.setVehicleID(rs.getInt("VehicleID"));
                a.setAppointmentDate(rs.getTimestamp("Date").toLocalDateTime());
                a.setStatus(rs.getString("Status"));
                a.setDescription(rs.getString("Description"));
                a.setRescheduleCount(rs.getInt("RescheduleCount"));
                appointments.add(a);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return appointments;
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
    public int countSearchAppointment(String customerName, String fromDate, String toDate, String[] statusList) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total " +
                        "FROM Appointment a " +
                        "JOIN Customer c ON a.CustomerID = c.CustomerID " +
                        "JOIN User u ON c.UserID = u.UserID " +
                        "WHERE 1=1 ");

        if (customerName != null && !customerName.isEmpty())
            sql.append("AND u.FullName LIKE ? ");
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
            if (rs.next())
                return rs.getInt("total");
        }

        return 0;
    }
    public List<Map<String, Object>> searchAppointmentWithLimit(
            String customerName, String fromDate, String toDate,
            String[] statusList, String sortOrder, int limit, int offset) throws SQLException {

        List<Map<String, Object>> resultList = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT a.AppointmentID, a.CustomerID, a.VehicleID, a.Date, a.Status, a.Description, a.RescheduleCount, " +
                        "u.FullName AS customerName " +
                        "FROM Appointment a " +
                        "JOIN Customer c ON a.CustomerID = c.CustomerID " +
                        "JOIN User u ON c.UserID = u.UserID " +
                        "WHERE 1=1 ");

        if (customerName != null && !customerName.isEmpty())
            sql.append("AND u.FullName LIKE ? ");
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
        sql.append(" LIMIT ? OFFSET ?");

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

            ps.setInt(index++, limit);
            ps.setInt(index, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                Appointment apm = new Appointment();
                apm.setAppointmentID(rs.getInt("AppointmentID"));
                apm.setCustomerID(rs.getInt("CustomerID"));
                apm.setVehicleID(rs.getInt("VehicleID"));
                apm.setAppointmentDate(rs.getTimestamp("Date").toLocalDateTime());
                apm.setStatus(rs.getString("Status"));
                apm.setDescription(rs.getString("Description"));
                apm.setRescheduleCount(rs.getInt("RescheduleCount"));
                row.put("appointment", apm);
                row.put("customerName", rs.getString("customerName"));
                resultList.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm cuộc hẹn với tên khách hàng", e);
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
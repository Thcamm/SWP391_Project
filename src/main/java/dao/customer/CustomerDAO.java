package dao.customer;

import common.DbContext;
import dao.vehicle.VehicleDAO;
import model.customer.Customer;

import java.sql.*;

public class CustomerDAO extends DbContext {

    public boolean insertCustomer(Customer customer) {
        String sqlUser = "INSERT INTO User " +
                "(RoleID, FullName, Email, PhoneNumber, Gender, Birthdate, Address, ActiveStatus, UserName, PasswordHash) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlCustomer = "INSERT INTO Customer (UserID, PointLoyalty) VALUES (?, ?)";

        Connection conn = null;
        PreparedStatement stUser = null;
        PreparedStatement stCustomer = null;
        ResultSet rs = null;

        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false); // 🔹 Bắt đầu transaction

            // === 1. Insert vào bảng User ===
            stUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
            stUser.setInt(1, customer.getRoleId());
            stUser.setString(2, customer.getFullName());
            stUser.setString(3, customer.getEmail());

            if (customer.getPhoneNumber() == null || customer.getPhoneNumber().isBlank()) {
                stUser.setNull(4, Types.VARCHAR);
            } else {
                stUser.setString(4, customer.getPhoneNumber());
            }

            if (customer.getGender() == null || customer.getGender().isBlank()) {
                stUser.setNull(5, Types.VARCHAR);
            } else {
                stUser.setString(5, customer.getGender());
            }

            if (customer.getBirthDate() == null) {
                stUser.setNull(6, Types.DATE);
            } else {
                stUser.setDate(6, customer.getBirthDate());
            }

            if (customer.getAddress() == null || customer.getAddress().isBlank()) {
                stUser.setNull(7, Types.VARCHAR);
            } else {
                stUser.setString(7, customer.getAddress());
            }

            stUser.setBoolean(8, customer.isActiveStatus());
            stUser.setString(9, customer.getUserName());
            stUser.setString(10, customer.getPasswordHash());

            int affected = stUser.executeUpdate();

            if (affected == 0) {
                conn.rollback();
                throw new SQLException("Không thể thêm người dùng (User).");
            }

            rs = stUser.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            } else {
                conn.rollback();
                throw new SQLException("Không lấy được UserID vừa tạo.");
            }


            stCustomer = conn.prepareStatement(sqlCustomer);
            stCustomer.setInt(1, userId);
            stCustomer.setInt(2, customer.getPointLoyalty());
            stCustomer.executeUpdate();

            conn.commit(); // thành công
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (rs != null) rs.close();
                if (stUser != null) stUser.close();
                if (stCustomer != null) stCustomer.close();
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCustomerDuplicate(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE Email = ? ";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setString(1, email);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

//    public List<Customer> searchCustomers(String fullName, String contact, String licensePlate) {
//        List<Customer> list = new ArrayList<>();
//
//        String sql = "SELECT DISTINCT c.* " +
//                "FROM customer c " +
//                "LEFT JOIN vehicle v ON c.CustomerID = v.CustomerID " +
//                "WHERE (c.FullName LIKE ? OR ? IS NULL) " +
//                "AND ((c.Email LIKE ? OR c.PhoneNumber LIKE ?) OR ? IS NULL) " +
//                "AND (v.LicensePlate LIKE ? OR ? IS NULL)";
//
//        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
//
//            String nameParam = (fullName == null || fullName.isEmpty()) ? null : "%" + fullName + "%";
//            String contactParam = (contact == null || contact.isEmpty()) ? null : "%" + contact + "%";
//            String licenseParam = (licensePlate == null || licensePlate.isEmpty()) ? null : "%" + licensePlate + "%";
//
//            st.setString(1, nameParam);
//            st.setString(2, nameParam);
//            st.setString(3, contactParam);
//            st.setString(4, contactParam);
//            st.setString(5, contactParam);
//            st.setString(6, licenseParam);
//            st.setString(7, licenseParam);
//
//            ResultSet rs = st.executeQuery();
//
//            while (rs.next()) {
//                Customer c = new Customer();
//                c.setCustomerId(rs.getInt("CustomerID"));
//                c.setFullName(rs.getString("FullName"));
//                c.setEmail(rs.getString("Email"));
//                c.setPhoneNumber(rs.getString("PhoneNumber"));
//                c.setGender(rs.getString("Gender"));
//                Date sqlDate = rs.getDate("Birthdate");
//                if (sqlDate != null) {
//                    c.setBirthDate(java.sql.Date.valueOf(((java.sql.Date) sqlDate).toLocalDate()));
//                }
//
//                c.setAddress(rs.getString("Address"));
//                list.add(c);
//            }
//
//
//            VehicleDAO vehicleDAO = new VehicleDAO();
//            for (Customer c : list) {
//                c.setVehicles(vehicleDAO.getVehiclesByCustomerId(c.getCustomerId()));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return list;
//    }
}

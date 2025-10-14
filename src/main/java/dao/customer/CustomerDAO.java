package dao.customer;

import common.DbContext;
import model.customer.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO extends DbContext {

//    public boolean insertCustomer(Customer customer) {
//
//        String sql = "INSERT INTO customer (FullName, Email, PhoneNumber, Gender, Birthdate, Address) " +
//                "VALUES (?, ?, ?, ?, ?, ?)";
//
//        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
//
//            st.setString(1, customer.getFullName());
//
//            if (customer.getEmail() == null || customer.getEmail().isBlank()) {
//                st.setNull(2, java.sql.Types.VARCHAR);
//            } else {
//                st.setString(2, customer.getEmail());
//            }
//
//            if (customer.getPhoneNumber() == null || customer.getPhoneNumber().isBlank()) {
//                st.setNull(3, java.sql.Types.VARCHAR);
//            } else {
//                st.setString(3, customer.getPhoneNumber());
//            }
//
//            if (customer.getGender() == null || customer.getGender() .isBlank()) {
//                st.setNull(4, java.sql.Types.VARCHAR);
//            } else {
//                st.setString(4, customer.getGender() );
//            }
//
//            if (customer.getBirthDate() == null ) {
//                st.setNull(5, java.sql.Types.DATE);
//            } else {
//                java.sql.Date sqlDate = java.sql.Date.valueOf(customer.getBirthDate());
//                st.setDate(5, sqlDate);
//            }
//
//            if (customer.getAddress() == null || customer.getAddress().isBlank()) {
//                st.setNull(6, java.sql.Types.VARCHAR);
//            } else {
//                st.setString(6, customer.getAddress());
//            }
//
//            int rowsAffected = st.executeUpdate();
//            return rowsAffected > 0;
//
//        } catch (SQLException e ) {
//            throw new RuntimeException("Lỗi khi thêm khách hàng", e);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public boolean isCustomerDuplicate(String email, String phoneNumber) {
        String sql = "SELECT COUNT(*) FROM customer WHERE Email = ? OR PhoneNumber = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setString(1, email);
            st.setString(2, phoneNumber);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getCustomerIdByUserId(int userId) {
        String sql = "SELECT CustomerID FROM Customer WHERE UserID = ?";

        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, userId);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                return rs.getInt("CustomerID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Không tìm thấy
    }
    public Customer getCustomerByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE UserID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("CustomerID"));
                    customer.setUserId(rs.getInt("UserID"));
                    // Set các thuộc tính khác nếu có...
                    return customer;
                }
            }
        }
        return null;
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
//                    c.setBirthDate(((java.sql.Date) sqlDate).toLocalDate());
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
public List<Customer> getAllActiveCustomers() throws SQLException {
    List<Customer> customers = new ArrayList<>();
    String sql = "SELECT c.CustomerID, c.UserID, u.FullName " +
            "FROM Customer c JOIN User u ON c.UserID = u.UserID " +
            "WHERE u.ActiveStatus = 1 ORDER BY u.FullName";

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Customer customer = new Customer();
            customer.setCustomerId(rs.getInt("CustomerID"));
            customer.setUserId(rs.getInt("UserID"));

            customer.setFullName(rs.getString("FullName"));

            customers.add(customer);
        }
    }
    return customers;
}
}

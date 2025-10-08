package dao.customer;

import common.DbContext;
import dao.vehicle.VehicleDAO;
import model.customer.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerDAO extends DbContext {

    public boolean insertCustomer(Customer customer) {

        String sql = "INSERT INTO customer (FullName, Email, PhoneNumber, Gender, Birthdate, Address) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {

            st.setString(1, customer.getFullName());

            if (customer.getEmail() == null || customer.getEmail().isBlank()) {
                st.setNull(2, java.sql.Types.VARCHAR);
            } else {
                st.setString(2, customer.getEmail());
            }

            if (customer.getPhoneNumber() == null || customer.getPhoneNumber().isBlank()) {
                st.setNull(3, java.sql.Types.VARCHAR);
            } else {
                st.setString(3, customer.getPhoneNumber());
            }

            if (customer.getGender() == null || customer.getGender() .isBlank()) {
                st.setNull(4, java.sql.Types.VARCHAR);
            } else {
                st.setString(4, customer.getGender() );
            }

            if (customer.getBirthDate() == null ) {
                st.setNull(5, java.sql.Types.DATE);
            } else {
                java.sql.Date sqlDate = java.sql.Date.valueOf(customer.getBirthDate());
                st.setDate(5, sqlDate);
            }

            if (customer.getAddress() == null || customer.getAddress().isBlank()) {
                st.setNull(6, java.sql.Types.VARCHAR);
            } else {
                st.setString(6, customer.getAddress());
            }

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e ) {
            throw new RuntimeException("Lỗi khi thêm khách hàng", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    public List<Customer> searchCustomers(String fullName, String contact, String licensePlate) {
        List<Customer> list = new ArrayList<>();

        String sql = "SELECT DISTINCT c.* " +
                "FROM customer c " +
                "LEFT JOIN vehicle v ON c.CustomerID = v.CustomerID " +
                "WHERE (c.FullName LIKE ? OR ? IS NULL) " +
                "AND ((c.Email LIKE ? OR c.PhoneNumber LIKE ?) OR ? IS NULL) " +
                "AND (v.LicensePlate LIKE ? OR ? IS NULL)";

        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {

            String nameParam = (fullName == null || fullName.isEmpty()) ? null : "%" + fullName + "%";
            String contactParam = (contact == null || contact.isEmpty()) ? null : "%" + contact + "%";
            String licenseParam = (licensePlate == null || licensePlate.isEmpty()) ? null : "%" + licensePlate + "%";

            st.setString(1, nameParam);
            st.setString(2, nameParam);
            st.setString(3, contactParam);
            st.setString(4, contactParam);
            st.setString(5, contactParam);
            st.setString(6, licenseParam);
            st.setString(7, licenseParam);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("CustomerID"));
                c.setFullName(rs.getString("FullName"));
                c.setEmail(rs.getString("Email"));
                c.setPhoneNumber(rs.getString("PhoneNumber"));
                c.setGender(rs.getString("Gender"));
                Date sqlDate = rs.getDate("Birthdate");
                if (sqlDate != null) {
                    c.setBirthDate(((java.sql.Date) sqlDate).toLocalDate());
                }

                c.setAddress(rs.getString("Address"));
                list.add(c);
            }


            VehicleDAO vehicleDAO = new VehicleDAO();
            for (Customer c : list) {
                c.setVehicles(vehicleDAO.getVehiclesByCustomerId(c.getCustomerId()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}

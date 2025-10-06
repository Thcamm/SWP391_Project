package dao.customer;

import common.DbContext;
import model.customer.Customer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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

}

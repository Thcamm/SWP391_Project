package dao.customer;

import common.DbContext;
import dao.vehicle.VehicleDAO;
import model.customer.Customer;
import model.vehicle.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            conn.setAutoCommit(false);

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

    public List<Customer> searchCustomers(String name, String emailOrPhone, String licensePlate,
                                          String sortOrder, String fromDate, String toDate) {
        Map<Integer, Customer> customerMap = new LinkedHashMap<>();

        StringBuilder sql = new StringBuilder(
                "SELECT c.CustomerID, u.UserID, u.FullName, u.Email, u.PhoneNumber, u.CreatedAt, " +
                        "v.VehicleID, v.LicensePlate, v.Brand, v.Model, v.YearManufacture " +
                        "FROM customer c " +
                        "JOIN user u ON c.UserID = u.UserID " +
                        "LEFT JOIN vehicle v ON c.CustomerID = v.CustomerID " +
                        "WHERE 1=1 "
        );

        if (name != null && !name.trim().isEmpty()) {
            sql.append("AND u.FullName LIKE ? ");
        }
        if (emailOrPhone != null && !emailOrPhone.trim().isEmpty()) {
            sql.append("AND (u.Email LIKE ? OR u.PhoneNumber LIKE ?) ");
        }
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            sql.append("AND v.LicensePlate LIKE ? ");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append("AND u.CreatedAt >= ? ");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append("AND u.CreatedAt <= ? ");
        }

        sql.append("ORDER BY u.CreatedAt ");
        sql.append("oldest".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC");

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            int index = 1;

            if (name != null && !name.trim().isEmpty()) {
                ps.setString(index++, "%" + name.trim() + "%");
            }
            if (emailOrPhone != null && !emailOrPhone.trim().isEmpty()) {
                ps.setString(index++, "%" + emailOrPhone.trim() + "%");
                ps.setString(index++, "%" + emailOrPhone.trim() + "%");
            }
            if (licensePlate != null && !licensePlate.trim().isEmpty()) {
                ps.setString(index++, "%" + licensePlate.trim() + "%");
            }
            if (fromDate != null && !fromDate.isEmpty()) {
                ps.setDate(index++, java.sql.Date.valueOf(fromDate));
            }
            if (toDate != null && !toDate.isEmpty()) {
                ps.setDate(index++, java.sql.Date.valueOf(toDate));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int customerID = rs.getInt("CustomerID");

                Customer customer = customerMap.get(customerID);
                if (customer == null) {
                    customer = new Customer();
                    customer.setCustomerId(customerID);
                    customer.setUserId(rs.getInt("UserID"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    customer.setVehicles(new ArrayList<>());
                    customerMap.put(customerID, customer);
                }

                int vehicleID = rs.getInt("VehicleID");
                if (vehicleID != 0) {
                    Vehicle v = new Vehicle();
                    v.setVehicleID(vehicleID);
                    v.setCustomerID(customerID);
                    v.setLicensePlate(rs.getString("LicensePlate"));
                    v.setBrand(rs.getString("Brand"));
                    v.setModel(rs.getString("Model"));
                    v.setYearManufacture(rs.getInt("YearManufacture"));
                    customer.getVehicles().add(v);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(customerMap.values());
    }


    public List<Customer> getAllCustomers() {
        Map<Integer, Customer> customerMap = new LinkedHashMap<>();

        String sql = "SELECT c.CustomerID, u.UserID, u.FullName, u.Email, u.PhoneNumber, u.CreatedAt, " +
                "v.VehicleID, v.LicensePlate, v.Brand, v.Model, v.YearManufacture " +
                "FROM customer c " +
                "JOIN user u ON c.UserID = u.UserID " +
                "LEFT JOIN vehicle v ON c.CustomerID = v.CustomerID " +
                "ORDER BY u.CreatedAt" ;

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int customerID = rs.getInt("CustomerID");

                // Kiểm tra xem đã có customer này trong map chưa
                Customer customer = customerMap.get(customerID);
                if (customer == null) {
                    customer = new Customer();
                    customer.setCustomerId(customerID);
                    customer.setUserId(rs.getInt("UserID"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    customer.setVehicles(new ArrayList<>());
                    customerMap.put(customerID, customer);
                }

                // Nếu có vehicle thì thêm vào list
                int vehicleID = rs.getInt("VehicleID");
                if (vehicleID != 0) {
                    Vehicle v = new Vehicle();
                    v.setVehicleID(vehicleID);
                    v.setCustomerID(customerID);
                    v.setLicensePlate(rs.getString("LicensePlate"));
                    v.setBrand(rs.getString("Brand"));
                    v.setModel(rs.getString("Model"));
                    v.setYearManufacture(rs.getInt("YearManufacture"));
                    customer.getVehicles().add(v);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(customerMap.values());
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


}

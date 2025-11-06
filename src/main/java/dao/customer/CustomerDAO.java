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

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {

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
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEmailDuplicate(String email) {
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
    public boolean isPhoneNumberDuplicate(String phoneNumber) {
        String sql = "SELECT COUNT(*) FROM user WHERE PhoneNumber = ? ";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setString(1, phoneNumber);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countSearchCustomers(String name, String emailOrPhone, String licensePlate,
                                    String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT c.CustomerID) " +
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

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql.toString())) {
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

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Customer> searchCustomersWithLimit(String name, String emailOrPhone, String licensePlate,
                                                   String sortOrder, String fromDate, String toDate,
                                                   int limit, int offset) {
        Map<Integer, Customer> customerMap = new LinkedHashMap<>();

        // Bước 1: Lấy danh sách CustomerID với LIMIT (không JOIN vehicle)
        StringBuilder sqlCustomers = new StringBuilder(
                "SELECT DISTINCT c.CustomerID, u.UserID, u.FullName, u.Email, u.PhoneNumber, u.CreatedAt " +
                        "FROM customer c " +
                        "JOIN user u ON c.UserID = u.UserID " +
                        "LEFT JOIN vehicle v ON c.CustomerID = v.CustomerID " +
                        "WHERE 1=1 "
        );

        if (name != null && !name.trim().isEmpty()) {
            sqlCustomers.append("AND u.FullName LIKE ? ");
        }
        if (emailOrPhone != null && !emailOrPhone.trim().isEmpty()) {
            sqlCustomers.append("AND (u.Email LIKE ? OR u.PhoneNumber LIKE ?) ");
        }
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            sqlCustomers.append("AND v.LicensePlate LIKE ? ");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            sqlCustomers.append("AND u.CreatedAt >= ? ");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sqlCustomers.append("AND u.CreatedAt <= ? ");
        }

        sqlCustomers.append("ORDER BY u.CreatedAt ");
        sqlCustomers.append("oldest".equalsIgnoreCase(sortOrder) ? "ASC " : "DESC ");
        sqlCustomers.append("LIMIT ? OFFSET ?");

        List<Integer> customerIDs = new ArrayList<>();

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sqlCustomers.toString())) {
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

            ps.setInt(index++, limit);
            ps.setInt(index, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int customerID = rs.getInt("CustomerID");
                    customerIDs.add(customerID);

                    Customer customer = new Customer();
                    customer.setCustomerId(customerID);
                    customer.setUserId(rs.getInt("UserID"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    customer.setVehicles(new ArrayList<>());
                    customerMap.put(customerID, customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bước 2: Nếu có customer, lấy tất cả vehicles của những customer đó
        if (!customerIDs.isEmpty()) {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < customerIDs.size(); i++) {
                placeholders.append(i > 0 ? ",?" : "?");
            }

            String sqlVehicles = "SELECT VehicleID, CustomerID, LicensePlate, Brand, Model, YearManufacture " +
                    "FROM vehicle WHERE CustomerID IN (" + placeholders + ")";

            try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sqlVehicles)) {
                for (int i = 0; i < customerIDs.size(); i++) {
                    ps.setInt(i + 1, customerIDs.get(i));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int customerID = rs.getInt("CustomerID");
                        Customer customer = customerMap.get(customerID);

                        if (customer != null) {
                            Vehicle v = new Vehicle();
                            v.setVehicleID(rs.getInt("VehicleID"));
                            v.setCustomerID(customerID);
                            v.setLicensePlate(rs.getString("LicensePlate"));
                            v.setBrand(rs.getString("Brand"));
                            v.setModel(rs.getString("Model"));
                            v.setYearManufacture(rs.getInt("YearManufacture"));
                            customer.getVehicles().add(v);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new ArrayList<>(customerMap.values());
    }

    public List<Customer> getCustomersWithLimit(int limit, int offset) {
        Map<Integer, Customer> customerMap = new LinkedHashMap<>();

        // Bước 1: Lấy danh sách customer với LIMIT
        String sqlCustomers = "SELECT c.CustomerID, u.UserID, u.FullName, u.Email, u.PhoneNumber, u.CreatedAt " +
                "FROM customer c " +
                "JOIN user u ON c.UserID = u.UserID " +
                "ORDER BY u.CreatedAt DESC " +
                "LIMIT ? OFFSET ?";

        List<Integer> customerIDs = new ArrayList<>();

        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sqlCustomers)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int customerID = rs.getInt("CustomerID");
                    customerIDs.add(customerID);

                    Customer customer = new Customer();
                    customer.setCustomerId(customerID);
                    customer.setUserId(rs.getInt("UserID"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                    customer.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    customer.setVehicles(new ArrayList<>());
                    customerMap.put(customerID, customer);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bước 2: Lấy vehicles của những customer đó
        if (!customerIDs.isEmpty()) {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < customerIDs.size(); i++) {
                placeholders.append(i > 0 ? ",?" : "?");
            }

            String sqlVehicles = "SELECT VehicleID, CustomerID, LicensePlate, Brand, Model, YearManufacture " +
                    "FROM vehicle WHERE CustomerID IN (" + placeholders + ")";

            try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sqlVehicles)) {
                for (int i = 0; i < customerIDs.size(); i++) {
                    ps.setInt(i + 1, customerIDs.get(i));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int customerID = rs.getInt("CustomerID");
                        Customer customer = customerMap.get(customerID);

                        if (customer != null) {
                            Vehicle v = new Vehicle();
                            v.setVehicleID(rs.getInt("VehicleID"));
                            v.setCustomerID(customerID);
                            v.setLicensePlate(rs.getString("LicensePlate"));
                            v.setBrand(rs.getString("Brand"));
                            v.setModel(rs.getString("Model"));
                            v.setYearManufacture(rs.getInt("YearManufacture"));
                            customer.getVehicles().add(v);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return new ArrayList<>(customerMap.values());
    }
    public int countCustomers() {
        String sql = "SELECT COUNT(*) FROM customer";
        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }



    public List<Customer> getAllCustomers() {
        return getCustomersWithLimit(Integer.MAX_VALUE, 0);
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

        return -1;
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
                    return customer;
                }
            }
        }
        return null;
    }
public Customer getCustomerById(int customerId) throws SQLException {
        Customer customer = null;


        String sql = "SELECT c.CustomerID, u.UserID, u.FullName, u.Email, u.PhoneNumber " +
                "FROM Customer c " +
                "JOIN User u ON c.UserID = u.UserID " +
                "WHERE c.CustomerID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    // Lấy thông tin từ cả 2 bảng
                    customer.setCustomerId(rs.getInt("CustomerID"));
                    customer.setUserId(rs.getInt("UserID"));
                    customer.setFullName(rs.getString("FullName"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setPhoneNumber(rs.getString("PhoneNumber"));
                }
            }
        }catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy thông tin khách hàng theo ID", e);
        }
        return customer;
    }
    public Integer getCustomerIdByWorkOrderId(int workOrderId) throws SQLException {
        String sql = """
            SELECT s.CustomerID
            FROM WorkOrder w
            JOIN ServiceRequest s ON w.RequestID = s.RequestID
            WHERE w.WorkOrderID = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("CustomerID");
            }
        }
        return null;
    }
}

package dao.employee.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.employee.admin.UserDisplay;
import common.DbContext;
import model.user.User;

public class AdminDAO extends DbContext {

    public ArrayList<UserDisplay> searchAllUsersWithRole(String keyword, Integer roleId, Boolean activeStatus,
            String sortBy)
            throws SQLException {

        // SUPER SIMPLE TEST: Just count how many users exist
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM User");
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int userCount = rs.getInt(1);
                System.out.println(" SIMPLE COUNT TEST: Database has " + userCount + " users in User table");
            }
        } catch (SQLException e) {
            System.err.println("  SIMPLE COUNT FAILED: " + e.getMessage());
        }

        StringBuilder sql = new StringBuilder(
                "SELECT u.UserID, u.RoleID, u.FullName, u.UserName, u.Email, u.PhoneNumber, " +
                        "u.ActiveStatus, u.CreatedAt, u.UpdatedAt, r.RoleName " +
                        "FROM User u " +
                        "LEFT JOIN RoleInfo r ON u.RoleID = r.RoleID " +
                        "WHERE 1=1 ");

        ArrayList<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.UserName LIKE ? OR u.Email LIKE ? OR u.FullName LIKE ?) ");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // role filter
        if (roleId != null && roleId > 0) {
            sql.append("AND u.RoleID = ? ");
            params.add(roleId);
        }
        // Status filter - ADMIN có thể xem cả inactive users
        if (activeStatus != null) {
            sql.append("AND u.ActiveStatus = ? ");
            params.add(activeStatus);
        }

        // Sorting logic
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "userid":
                    sql.append("ORDER BY u.UserID ASC");
                    break;
                case "username":
                    sql.append("ORDER BY u.UserName ASC");
                    break;
                case "fullname":
                    sql.append("ORDER BY u.FullName ASC");
                    break;
                case "email":
                    sql.append("ORDER BY u.Email ASC");
                    break;
                case "rolename":
                    sql.append("ORDER BY r.RoleName ASC, u.FullName ASC");
                    break;
                case "status":
                    sql.append("ORDER BY u.ActiveStatus DESC, u.FullName ASC");
                    break;
                default:
                    sql.append("ORDER BY u.UserID ASC");
            }
        } else {
            sql.append("ORDER BY u.UserID ASC");
        }

        ArrayList<UserDisplay> users = new ArrayList<>();

        System.out.println(" DEBUG QUERY: " + sql.toString());
        System.out.println(" DEBUG PARAMS: " + params.toString());

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            System.out.println(" DEBUG: Database connection successful!");

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println(" DEBUG: Query executed successfully!");
                while (rs.next()) {
                    UserDisplay user = extractUserDisplay(rs);
                    users.add(user);
                    System.out
                            .println(" DEBUG: Found user - " + user.getUserName() + " (" + user.getFullName() + ")");
                }
            }
        } catch (SQLException e) {
            System.err.println("  DEBUG SQL ERROR: " + e.getMessage());
            System.err.println(" DEBUG SQL State: " + e.getSQLState());
            System.err.println(" DEBUG Error Code: " + e.getErrorCode());
            e.printStackTrace();
            throw e;
        }
        System.out.println("ADMIN Search executed - Found " + users.size() + " users");
        return users;
    }

    /**
     * ADMIN FUNCTION: Get all users for management (including inactive)
     */
    public ArrayList<User> getAllUsersForAdmin() throws SQLException {
        String sql = "SELECT * FROM User ORDER BY FullName";
        ArrayList<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = extractUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return users;
    }

    /**
     * ADMIN FUNCTION: Count search results
     */
    public int countSearchResults(String keyword, Integer roleId, Boolean activeStatus) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM User u WHERE 1=1 ");
        ArrayList<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.UserName LIKE ? OR u.Email LIKE ? OR u.FullName LIKE ?) ");
            String searchPattern = "%" + keyword.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (roleId != null && roleId > 0) {
            sql.append("AND u.RoleID = ? ");
            params.add(roleId);
        }

        if (activeStatus != null) {
            sql.append("AND u.ActiveStatus = ? ");
            params.add(activeStatus);
        }

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * ADMIN FUNCTION: Create new user with auto Employee record creation for
     * non-Customer roles
     */
    public boolean createUser(User user) throws SQLException {
        return createUser(user, null, null, null);
    }

    /**
     * ADMIN FUNCTION: Create new user with Employee details for non-Customer roles
     */
    public boolean createUser(User user, String employeeCode, Double salary, Integer createdByEmployeeId)
            throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert into User table
            String userSql = "INSERT INTO `User` (RoleID, FullName, UserName, Email, PhoneNumber, PasswordHash, ActiveStatus) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            int newUserId;
            try (PreparedStatement userPs = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                userPs.setInt(1, user.getRoleId());
                userPs.setString(2, user.getFullName());
                userPs.setString(3, user.getUserName());
                userPs.setString(4, user.getEmail());
                userPs.setString(5, user.getPhoneNumber());
                userPs.setString(6, user.getPasswordHash());
                userPs.setBoolean(7, user.isActiveStatus());

                int userResult = userPs.executeUpdate();
                if (userResult == 0) {
                    conn.rollback();
                    return false;
                }

                // Get the generated UserID
                try (ResultSet generatedKeys = userPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newUserId = generatedKeys.getInt(1);
                        user.setUserId(newUserId);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 2. Check if we need to create Employee record (for non-Customer roles)
            if (!isCustomerRole(user.getRoleId())) {
                String roleName = getRoleNameById(user.getRoleId());

                // Generate employee code if not provided
                if (employeeCode == null || employeeCode.trim().isEmpty()) {
                    employeeCode = generateEmployeeCode(roleName, newUserId);
                }

                // Create Employee record
                String empSql = "INSERT INTO Employee (UserID, EmployeeCode, Salary, ManagedBy, CreatedBy) " +
                        "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement empPs = conn.prepareStatement(empSql)) {
                    empPs.setInt(1, newUserId);
                    empPs.setString(2, employeeCode);

                    if (salary != null) {
                        empPs.setDouble(3, salary);
                    } else {
                        empPs.setNull(3, java.sql.Types.DOUBLE);
                    }
                    empPs.setNull(4, java.sql.Types.INTEGER);
                    if (createdByEmployeeId != null) {
                        empPs.setInt(5, createdByEmployeeId);
                    } else {
                        empPs.setNull(5, java.sql.Types.INTEGER);
                    }

                    int empResult = empPs.executeUpdate();
                    if (empResult == 0) {
                        conn.rollback();
                        System.err.println("Failed to create Employee record for user: " + user.getUserName());
                        return false;
                    }

                    System.out.println("ADMIN: Employee record created successfully - Code: " + employeeCode +
                            " for user: " + user.getUserName());
                }
            }

            conn.commit();
            System.out.println("ADMIN: User creation successful for username: " + user.getUserName());
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println(
                    "ADMIN: User creation failed for username: " + user.getUserName() + " - " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * ADMIN FUNCTION: Update existing user
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE User SET RoleID=?, FullName=?, UserName=?, Email=?, " +
                "PhoneNumber=?, PasswordHash=?, ActiveStatus=?, UpdatedAt=NOW() WHERE UserID=?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getPasswordHash());
            ps.setBoolean(7, user.isActiveStatus());
            ps.setInt(8, user.getUserId());

            boolean success = ps.executeUpdate() > 0;
            System.out.println("ADMIN: User update " + (success ? "successful" : "failed") +
                    " for UserID: " + user.getUserId());
            return success;
        }
    }

    /**
     * ADMIN FUNCTION: Get user by ID (including inactive users)
     * This method is specifically for admin operations and returns users
     * regardless of their ActiveStatus.
     * 
     * @param userId The ID of the user to retrieve
     * @return User object if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserID = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * ADMIN FUNCTION: Get user by email (including inactive users)
     * This method is specifically for admin operations and returns users
     * regardless of their ActiveStatus. Used for duplicate email checking.
     * 
     * @param email The email address to search for
     * @return User object if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM User WHERE Email = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * ADMIN FUNCTION: Get UserDisplay by ID with role info (including inactive
     * users)
     */
    public UserDisplay getUserDisplayById(int userId) throws SQLException {
        String sql = "SELECT u.UserID, u.RoleID, u.FullName, u.UserName, u.Email, u.PhoneNumber, " +
                "u.ActiveStatus, u.CreatedAt, u.UpdatedAt, r.RoleName " +
                "FROM User u LEFT JOIN RoleInfo r ON u.RoleID = r.RoleID " +
                "WHERE u.UserID = ?";

        System.out.println(" AdminDAO.getUserDisplayById() called for ID: " + userId);

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserDisplay user = extractUserDisplay(rs);
                    System.out.println(" User found: " + user.getUserName());
                    return user;
                } else {
                    System.out.println(" No user found for ID: " + userId);
                    return null;
                }
            }
        }
    }

    /**
     * ADMIN FUNCTION: Update user status (enable/disable)
     */
    public boolean updateUserStatus(int userId, boolean newStatus, String currentUser) throws SQLException {
        String sql = "UPDATE User SET ActiveStatus = ?, UpdatedAt = NOW() WHERE UserID = ?";

        System.out.println(" AdminDAO.updateUserStatus() called");
        System.out.println("    User ID: " + userId);
        System.out.println("    New Status: " + newStatus);
        System.out.println("    Updated by: " + currentUser);

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, newStatus);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            boolean success = rowsAffected > 0;

            System.out.println(" Update result: " + rowsAffected + " rows affected, success: " + success);
            return success;
        }
    }

    /**
     * ADMIN FUNCTION: Get user by username (including inactive)
     */
    public User getUserByUsername(String userName) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserName = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        }
        return null;
    }

    // ===== HELPER METHODS =====

    private UserDisplay extractUserDisplay(ResultSet rs) throws SQLException {
        UserDisplay user = new UserDisplay();
        user.setUserId(rs.getInt("UserID"));
        user.setUserName(rs.getString("UserName"));
        user.setFullName(rs.getString("FullName"));
        user.setEmail(rs.getString("Email"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setRoleId(rs.getInt("RoleID"));
        user.setActiveStatus(rs.getBoolean("ActiveStatus"));

        // Handle timestamps
        if (rs.getTimestamp("CreatedAt") != null) {
            user.setCreatedAt(rs.getTimestamp("CreatedAt").toString());
        }
        if (rs.getTimestamp("UpdatedAt") != null) {
            user.setUpdatedAt(rs.getTimestamp("UpdatedAt").toString());
        }

        // Role information
        String roleName = rs.getString("RoleName");
        user.setRoleName(roleName != null ? roleName : "Unknown");
        user.setRoleBadgeClass(getRoleBadgeClass(rs.getInt("RoleID")));

        return user;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserID"));
        user.setRoleId(rs.getInt("RoleID"));
        user.setFullName(rs.getString("FullName"));
        user.setUserName(rs.getString("UserName"));
        user.setEmail(rs.getString("Email"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setActiveStatus(rs.getBoolean("ActiveStatus"));
        return user;
    }

    private String getRoleBadgeClass(int roleId) {
        switch (roleId) {
            case 1:
                return "bg-danger"; // Admin
            case 2:
                return "bg-warning text-dark"; // Manager
            case 3:
                return "bg-info"; // Employee
            case 4:
                return "bg-primary"; // User
            case 5:
                return "bg-secondary"; // Guest
            default:
                return "bg-dark"; // Unknown
        }
    }

    // Get EmployeeID associated with a given userName
    public Integer getEmployeeIdByUsername(String userName) throws SQLException {
        // SQL: Find EmployeeID by joining Employee and User tables on UserID
        String sql = "SELECT e.EmployeeID FROM Employee e " +
                "JOIN User u ON e.UserID = u.UserID " +
                "WHERE u.UserName = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("EmployeeID");
                }
            }
        }
        return null;
    }

    public boolean executePromoteToEmployeeSP(int userId, String newRoleName,
            String employeeCode, double salary,
            int managedByEmployeeId, int createdByEmployeeId)
            throws SQLException {

        // Đảm bảo bạn đã import java.sql.CallableStatement
        String sql = "{CALL SP_PromoteCustomerToEmployee(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = getConnection();
                java.sql.CallableStatement cs = conn.prepareCall(sql)) { // Sử dụng CallableStatement

            cs.setInt(1, userId);
            cs.setString(2, newRoleName);
            cs.setString(3, employeeCode);
            cs.setDouble(4, salary);
            cs.setInt(5, managedByEmployeeId);
            cs.setInt(6, createdByEmployeeId);

            cs.execute();
            return true;

        } catch (SQLException e) {
            System.err.println(" AdminDAO SQL Error executing SP: " + e.getMessage());
            throw e; // Bắt buộc phải re-throw để AdminService có thể bắt lỗi transaction
        }
    }

    public boolean updateUserBasicInfo(User user) throws SQLException {
        // Cập nhật các trường cơ bản bao gồm PhoneNumber, Email, FullName, RoleID, và
        // ActiveStatus
        String sql = "UPDATE User SET RoleID=?, FullName=?, Email=?, PhoneNumber=?, ActiveStatus=?, UpdatedAt=NOW() WHERE UserID=?";

        System.out.println(" AdminDAO.updateUserBasicInfo() called");
        System.out.println("    User ID: " + user.getUserId());
        System.out.println("    Full Name: " + user.getFullName());
        System.out.println("    Email: " + user.getEmail());
        System.out.println("    Phone Number: " + user.getPhoneNumber());
        System.out.println("    Role ID: " + user.getRoleId());
        System.out.println("    Active Status: " + user.isActiveStatus());

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setBoolean(5, user.isActiveStatus());
            ps.setInt(6, user.getUserId());

            int rowsAffected = ps.executeUpdate();
            boolean success = rowsAffected > 0;

            System.out.println(" Update result: " + rowsAffected + " rows affected, success: " + success);
            return success;
        }
    }

    /**
     * Check if a role is Customer role
     */
    private boolean isCustomerRole(int roleId) throws SQLException {
        String sql = "SELECT RoleName FROM RoleInfo WHERE RoleID = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String roleName = rs.getString("RoleName").toLowerCase();
                    return roleName.equals("customer") || roleName.equals("khách hàng");
                }
            }
        }
        return false; // Default to false if role not found
    }

    /**
     * Get role name by role ID
     */
    private String getRoleNameById(int roleId) throws SQLException {
        String sql = "SELECT RoleName FROM RoleInfo WHERE RoleID = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("RoleName");
                }
            }
        }
        return "Unknown";
    }

    /**
     * Generate unique employee code based on role and user ID
     */
    private String generateEmployeeCode(String roleName, int userId) {
        String prefix;

        if (roleName == null) {
            prefix = "EMP";
        } else {
            switch (roleName.toLowerCase()) {
                case "admin":
                    prefix = "ADM";
                    break;
                case "tech manager":
                case "technical manager":
                    prefix = "TM";
                    break;
                case "technician":
                    prefix = "TECH";
                    break;
                case "accountant":
                    prefix = "ACC";
                    break;
                case "storekeeper":
                    prefix = "STORE";
                    break;
                default:
                    prefix = "EMP";
                    break;
            }
        }

        // Generate code: PREFIX + UserID padded to 4 digits
        return prefix + String.format("%04d", userId);
    }

}

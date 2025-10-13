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
     * ADMIN FUNCTION: Create new user
     */
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO `User` (RoleID, FullName, UserName, Email, PhoneNumber, PasswordHash, ActiveStatus) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getPasswordHash());
            ps.setBoolean(7, user.isActiveStatus());

            boolean success = ps.executeUpdate() > 0;
            System.out.println("ADMIN: User creation " + (success ? "successful" : "failed") +
                    " for username: " + user.getUserName());
            return success;
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
     * ADMIN FUNCTION: Get user by ID (including inactive)
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
     * ADMIN FUNCTION: Get UserDisplay by ID with role info
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

}

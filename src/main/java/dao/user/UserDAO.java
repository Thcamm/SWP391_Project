package dao.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import common.DbContext;
import model.user.User;

public class UserDAO extends DbContext {
    public Connection getDBConnect() throws SQLException {
        return DbContext.getConnection();
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserID = ? AND ActiveStatus = 1";
        try (Connection conn = getDBConnect();
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

    public User getPasswordHashByUsername(String userName) throws SQLException {
        String sql = "SELECT PasswordHash FROM User WHERE UserName = ? AND ActiveStatus = 1";
        try (Connection conn = getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setPasswordHash(rs.getString("PasswordHash"));
                    return user;
                }
            }
        }
        return null;
    }

    public User getUserByUserName(String userName) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserName = ? AND ActiveStatus = 1";
        try (Connection conn = getDBConnect();
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

    public ArrayList<User> getAllActiveUsers() throws SQLException {
        String sql = "SELECT * FROM User WHERE ActiveStatus = 1";
        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        }
        return users;
    }

    public User getUserByUsername(String userName) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserName = ? AND ActiveStatus = 1";
        try (Connection conn = getDBConnect();
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

    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO User (RoleID, FullName, UserName, Email, PhoneNumber, PasswordHash, ActiveStatus) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getPasswordHash());
            ps.setBoolean(7, user.isActiveStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE User SET RoleID=?, FullName=?, UserName=?, Email=?, PhoneNumber=?, PasswordHash=?, ActiveStatus=? WHERE UserID=?";
        try (Connection conn = getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getPasswordHash());
            ps.setBoolean(7, user.isActiveStatus());
            ps.setInt(8, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "UPDATE User SET ActiveStatus = 0 WHERE UserID = ?";
        try (Connection conn = getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
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

    public int findRoleIdByUserId(int userId) {
        final String sql = "SELECT RoleID FROM User WHERE UserID = ?";
        try (Connection c = getDBConnect();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("RoleID") : -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findRoleIdByUserId failed", e);
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM User WHERE Email = ? AND ActiveStatus = 1";
        try (Connection conn = DbContext.getConnection();
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

    public boolean insertGoogleUser(User user) throws SQLException {
        String sql = "INSERT INTO User (RoleID, FullName, UserName, Email, ActiveStatus) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setBoolean(5, user.isActiveStatus());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isEmailExists(String email, int currentUserId) throws SQLException {
        String sql = "SELECT 1 FROM `User` WHERE Email = ? AND UserID != ?";
        try (Connection conn = getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean updateUserProfile(User user) throws SQLException {
        String sql = "UPDATE `User` SET FullName=?, Email=?, PhoneNumber=?, gender=?, birthdate=?, address=? WHERE UserID=?";
        try (Connection conn = getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getGender());
            ps.setDate(5, user.getBirthDate());
            ps.setString(6, user.getAddress());
            ps.setInt(7, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

}
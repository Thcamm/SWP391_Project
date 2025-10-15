package dao.user;

<<<<<<<<<Temporary merge branch 1

import dao.DbContext;=========>>>>>>>>>Temporary merge branch 2
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.user.User;
import common.DbContext;

public class UserDAO extends DbContext {

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserID = ? AND ActiveStatus = 1";
        try (Connection conn = DbContext.getConnection(); // Gọi trực tiếp
<<<<<<<<< Temporary merge branch 1
                PreparedStatement ps = conn.prepareStatement(sql)) {
=========
             PreparedStatement ps = conn.prepareStatement(sql)) {
>>>>>>>>> Temporary merge branch 2
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        }
        return null;
    }

<<<<<<<<< Temporary merge branch 1

    public User getUserByUserName(String userName) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserName = ? AND ActiveStatus = 1";
        try (Connection conn = DbContext.getConnection(); // Gọi trực tiếp
                PreparedStatement ps = conn.prepareStatement(sql)) {
=========
    // Lấy user bằng username (khi user đã active), đã bỏ phương thức bị trùng
    public User getUserByUserName(String userName) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserName = ? AND ActiveStatus = 1";
        try (Connection conn = DbContext.getConnection(); // Gọi trực tiếp
             PreparedStatement ps = conn.prepareStatement(sql)) {
>>>>>>>>> Temporary merge branch 2
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUser(rs);
                }
            }
        }
        return null;
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

    public ArrayList<User> getAllActiveUsers() throws SQLException {
        String sql = "SELECT * FROM User WHERE ActiveStatus = 1";
        ArrayList<User> users = new ArrayList<>();
        try (Connection conn = DbContext.getConnection(); // Gọi trực tiếp
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        }
        return users;
    }

    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO User (RoleID, FullName, UserName, Email, PhoneNumber, " +
                "Gender, BirthDate, Address, PasswordHash, ActiveStatus) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection(); // Gọi trực tiếp
<<<<<<<<< Temporary merge branch 1
                PreparedStatement ps = conn.prepareStatement(sql)) {
=========
             PreparedStatement ps = conn.prepareStatement(sql)) {
>>>>>>>>> Temporary merge branch 2
            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getGender());
            ps.setDate(7, user.getBirthDate());
            ps.setString(8, user.getAddress());
            ps.setString(9, user.getPasswordHash());
            ps.setBoolean(10, user.isActiveStatus());
            return ps.executeUpdate() > 0;
        }
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

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE User SET RoleID=?, FullName=?, UserName=?, Email=?, PhoneNumber=?, Gender=?, BirthDate=?, Address=?, PasswordHash=?, ActiveStatus=? WHERE UserID=?";
        try (Connection conn = DbContext.getConnection();
<<<<<<<<< Temporary merge branch 1
                PreparedStatement ps = conn.prepareStatement(sql)) {
=========
             PreparedStatement ps = conn.prepareStatement(sql)) {
>>>>>>>>> Temporary merge branch 2
            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getGender());
            ps.setDate(7, user.getBirthDate());
            ps.setString(8, user.getAddress());
            ps.setString(9, user.getPasswordHash());
            ps.setBoolean(10, user.isActiveStatus());
            ps.setInt(11, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "UPDATE User SET ActiveStatus = 0 WHERE UserID = ?";
        try (Connection conn = DbContext.getConnection(); // Gọi trực tiếp
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public int reassignUserRole(int fromRoleId, int toRoleId) throws SQLException {
        String sql = "UPDATE `User`  SET RoleID = ? WHERE ROLEID =?";
        try (Connection c = DbContext.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, toRoleId);
            ps.setInt(2, fromRoleId);
            return ps.executeUpdate();
        }
    }

    public boolean isEmailExists(String email, int currentUserId) throws SQLException {
        String sql = "SELECT 1 FROM `User` WHERE Email = ? AND UserID != ?";
        try (Connection conn = DbContext.getConnection();
<<<<<<<<< Temporary merge branch 1
                PreparedStatement ps = conn.prepareStatement(sql)) {
=========
             PreparedStatement ps = conn.prepareStatement(sql)) {
>>>>>>>>> Temporary merge branch 2
            ps.setString(1, email);
            ps.setInt(2, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean updateUserProfile(User user) throws SQLException {
        String sql = "UPDATE `User` SET FullName=?, Email=?, PhoneNumber=?, gender=?, birthdate=?, address=? WHERE UserID=?";
        try (Connection conn = DbContext.getConnection();
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

    public int findRoleIdByUserId(int userId) throws SQLException {
        String sql = "SELECT RoleID FROM User WHERE UserID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("RoleID");
                }
                return -1;
            }
        }
    }

    public List<Integer> findUserIdsByRoleName(String roleName) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT u.UserID FROM User u JOIN RoleInfo r ON u.RoleID = r.RoleID WHERE r.RoleName = ?";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("UserID"));
                }
            }
        }
        return userIds;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserID"));
        user.setRoleId(rs.getInt("RoleID"));
        user.setFullName(rs.getString("FullName"));
        user.setUserName(rs.getString("UserName"));
        user.setEmail(rs.getString("Email"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setGender(rs.getString("Gender"));
        user.setBirthDate(rs.getDate("BirthDate"));
        user.setAddress(rs.getString("Address"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setActiveStatus(rs.getBoolean("ActiveStatus"));
        user.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return user;
    }
}
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dao.user;

import common.DbContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.user.User;

public class UserDAO extends DbContext {
    public Connection getDBConnect() throws SQLException {
        return DbContext.getConnection();
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserID = ? AND ActiveStatus = 1";

        try (
                Connection conn = this.getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User var6 = this.extractUser(rs);
                    return var6;
                }
            }
        }

        return null;
    }

    public User getPasswordHashByUsername(String userName) throws SQLException {
        String sql = "SELECT PasswordHash FROM User WHERE UserName = ? AND ActiveStatus = 1";

        try (
                Connection conn = this.getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setPasswordHash(rs.getString("PasswordHash"));
                    User var7 = user;
                    return var7;
                }
            }
        }

        return null;
    }

    public User getUserByUserName(String userName) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserName = ? AND ActiveStatus = 1";

        try (
                Connection conn = this.getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User var6 = this.extractUser(rs);
                    return var6;
                }
            }
        }

        return null;
    }

    public ArrayList<User> getAllActiveUsers() throws SQLException {
        String sql = "SELECT * FROM User WHERE ActiveStatus = 1";
        ArrayList<User> users = new ArrayList();

        try (
                Connection conn = this.getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
        ) {
            while(rs.next()) {
                users.add(this.extractUser(rs));
            }
        }

        return users;
    }

    public User getUserByUsername(String userName) throws SQLException {
        String sql = "SELECT * FROM User WHERE UserName = ? AND ActiveStatus = 1";

        try (
                Connection conn = this.getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setString(1, userName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User var6 = this.extractUser(rs);
                    return var6;
                }
            }
        }

        return null;
    }

    public boolean addUser(User user) throws SQLException {
        String sql = "INSERT INTO User (RoleID, FullName, UserName, Email, PhoneNumber, PasswordHash, ActiveStatus) VALUES (?, ?, ?, ?, ?, ?, ?)";

        boolean var5;
        try (
                Connection conn = this.getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getPasswordHash());
            ps.setBoolean(7, user.isActiveStatus());
            var5 = ps.executeUpdate() > 0;
        }

        return var5;
    }

    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE User SET RoleID=?, FullName=?, UserName=?, Email=?, PhoneNumber=?, PasswordHash=?, ActiveStatus=? WHERE UserID=?";

        boolean var5;
        try (
                Connection conn = this.getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, user.getRoleId());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getUserName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhoneNumber());
            ps.setString(6, user.getPasswordHash());
            ps.setBoolean(7, user.isActiveStatus());
            ps.setInt(8, user.getUserId());
            var5 = ps.executeUpdate() > 0;
        }

        return var5;
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "UPDATE User SET ActiveStatus = 0 WHERE UserID = ?";

        boolean var5;
        try (
                Connection conn = this.getDBConnect();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, userId);
            var5 = ps.executeUpdate() > 0;
        }

        return var5;
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
        user.setCreatedAt(rs.getTimestamp("CreatedAt"));
        user.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        user.setGender(rs.getString("gender"));
        user.setBirthDate(rs.getDate("birthdate"));
        user.setAddress(rs.getString("address"));
        return user;
    }

    public int findRoleIdByUserId(int userId) {
        String sql = "SELECT RoleID FROM User WHERE UserID = ?";

        try {
            int var6;
            try (
                    Connection c = this.getDBConnect();
                    PreparedStatement ps = c.prepareStatement("SELECT RoleID FROM User WHERE UserID = ?");
            ) {
                ps.setInt(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    var6 = rs.next() ? rs.getInt("RoleID") : -1;
                }
            }

            return var6;
        } catch (SQLException e) {
            throw new RuntimeException("findRoleIdByUserId failed", e);
        }
    }
<<<<<<< Updated upstream

    public int reassignUserRole(int fromRoleId, int toRoleId) throws SQLException{
        String sql = "UPDATE `User`  SET RoleID = ? WHERE ROLEID =?";
        try(Connection c = DbContext.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, toRoleId);
            ps.setInt(2, fromRoleId);
            return ps.executeUpdate();
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

=======
>>>>>>> Stashed changes
}

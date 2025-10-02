package dao.user;

import common.Db;
import model.user.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    public int findRoleIdByUserId(int userId) {
        final String sql = "SELECT RoleID FROM `User` WHERE UserID = ?";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findRoleIdByUserId failed", e);
        }
    }

}
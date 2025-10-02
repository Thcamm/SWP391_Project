package dao.rbac;

import common.Db;
import model.rbac.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoleDao {
    public List<Role> findAll() throws Exception {
        String sql = "SELECT RoleID, RoleName FROM RoleInfo ORDER BY RoleName";
        List<Role> list = new ArrayList<>();

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Role(rs.getInt("RoleID"), rs.getString("RoleName")));
            }
        } catch (SQLException e) {

            throw new RuntimeException("Query roles failed", e);
        }
        return list;
    }

    public Role findById(int roleId) throws SQLException {
        String sql = "SELECT RoleID, RoleName FROM RoleInfo WHERE RoleID = ?";
        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Role(rs.getInt("RoleID"), rs.getString("RoleName")) : null;
            }

        }

    }

    public Set<String> getPermissionCodesOfUser(int userId) throws SQLException {
        Set<String> permissionCodes = new HashSet<>();

        final String sql = "SELECT p.Code AS perm_code FROM `User` u JOIN RolePermission rp ON u.RoleID = rp.RoleID JOIN Permission p ON rp.PermID = p.PermID WHERE u.UserID = ? AND p.Active = 1";

        try (Connection c = Db.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    permissionCodes.add(rs.getString("perm_code"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Permission codes failed", e);
        }
        return permissionCodes;
    }

}
package dao.rbac;

import common.DbContext;
import model.rbac.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoleDao {
    public List<Role> findAll() throws Exception {
        String sql = "SELECT RoleID, RoleName FROM RoleInfo ORDER BY RoleID";
        List<Role> list = new ArrayList<>();

        try (Connection c = DbContext.getConnection();
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
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? new Role(rs.getInt("RoleID"), rs.getString("RoleName")) : null;
            }

        }

    }



    public Role findByName(String roleName) throws SQLException {
        String sql ="SELECT RoleID, RoleName FROM RoleInfo WHERE LOWER(RoleName) = LOWER(?)";
        try (Connection c = DbContext.getConnection();
        PreparedStatement ps  =  c.prepareStatement(sql)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return new Role(rs.getInt("RoleID"), rs.getString("RoleName"));
                }
                return null;
            }
        }
    }

    public List<Role> findByName(String keyword, int offset, int limit) throws SQLException {
        List<Role> list = new ArrayList<>();
        String sql = """
        SELECT r.RoleID, r.RoleName, COUNT(u.UserID) AS userCount
        FROM RoleInfo r
        LEFT JOIN `User` u ON r.RoleID = u.RoleID
        WHERE LOWER(r.RoleName) LIKE LOWER(?)
        GROUP BY r.RoleID, r.RoleName
        ORDER BY r.RoleID ASC
        LIMIT ? OFFSET ?
    """;
        try (Connection c = DbContext.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, "%" + keyword + "%");
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Role r = new Role();
                r.setRoleId(rs.getInt("RoleID"));
                r.setRoleName(rs.getString("RoleName"));
                r.setUserCount(rs.getInt("userCount"));
                list.add(r);
            }

        }

        return list;
    }

    public int countByName(String keyword) throws SQLException {
        String sql = "SELECT COUNT(*) FROM RoleInfo WHERE LOWER(RoleName) LIKE LOWER(?)";
        try (Connection con = DbContext.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public Set<String> getPermissionCodesOfUser(int userId) throws SQLException {
        Set<String> permissionCodes = new HashSet<>();

        final String sql = "SELECT p.Code AS perm_code FROM `User` u JOIN RolePermission rp ON u.RoleID = rp.RoleID JOIN Permission p ON rp.PermID = p.PermID WHERE u.UserID = ? AND p.Active = 1";

        try (Connection c = DbContext.getConnection();
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

    //Create role
    public int insert(String roleName) throws SQLException {
        String sql = "INSERT INTO RoleInfo (RoleName) VALUES(?)";
        try(Connection c = DbContext.getConnection();
        PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, roleName);
            ps.executeUpdate();

            try(ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
            throw new SQLException("No generated key returned");

        }catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Role name already exists", e);
        }

    }

    public boolean existsByNameIgnorecase(String name) throws SQLException {
        final String sql = "SELECT 1 FROM RoleInfo WHERE LOWER(RoleName) = LOWER(?) LIMIT 1";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByNameIgnorecaseExceptId(String name, int excludeId) throws SQLException {
        final String sql = "SELECT 1 FROM RoleInfo WHERE LOWER(RoleName) = LOWER(?) AND RoleID <> ? LIMIT 1";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    //Update role
    public void update(Role role) throws SQLException {
        String sql = "UPDATE RoleInfo SET RoleName = ? WHERE RoleID=?";
        try(Connection c = DbContext.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role.getRoleName());
            ps.setInt(2, role.getRoleId());
            ps.executeUpdate();
        }
    }

    //Delete role
    public boolean delete(int roleId) throws SQLException {
        String sql = "DELETE FROM RoleInfo WHERE RoleID = ?";
        try(Connection c = DbContext.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            int affected = ps.executeUpdate();
            return affected > 0;
        }catch (SQLException e) {
            if(e.getSQLState().startsWith("23")) {
                throw new SQLException("Cannot delete role as it is referenced by other records.", e);
            } else {
                throw e;
            }
        }
    }

    public int countUsersByRole(int roleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM `User` WHERE RoleID = ?";
        try(Connection c = DbContext.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);

            }
        }

    }

    public List<Role> findPaginated(int offset, int limit) throws Exception {
        String sql = """
        SELECT r.RoleID, r.RoleName, COUNT(u.UserID) AS userCount
        FROM RoleInfo r
        LEFT JOIN `User` u ON r.RoleID = u.RoleID
        GROUP BY r.RoleID, r.RoleName
        ORDER BY r.RoleID
        LIMIT ? OFFSET ?
    """;
        List<Role> list = new ArrayList<>();
        try(Connection c = DbContext.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Role r = new Role();
                    r.setRoleId(rs.getInt("RoleID"));
                    r.setRoleName(rs.getString("RoleName"));
                    r.setUserCount(rs.getInt("userCount"));
                    list.add(r);
                }
            }
        }catch (SQLException e) {
            throw new RuntimeException("Query paginated roles failed",e);
        }

        return list;
    }


    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM RoleInfo";
        try(Connection c = DbContext.getConnection();
        PreparedStatement ps = c.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()
        ) {
           return rs.next() ? rs.getInt(1) : 0;
        }
    }







}
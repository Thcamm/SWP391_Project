package dao.rbac;

import common.DbContext;
import model.rbac.Permission;

import java.sql.*;
import java.util.*;

public class PermissionDao {

    public int insert(Permission p) {
        String sql = "INSERT INTO Permission(Code, Name, Category, Description) VALUEs(?, ?, ?, ?)";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.code);
            ps.setString(2, p.name);
            ps.setString(3, p.category);
            ps.setString(4, p.description);
//            ps.setBoolean(5, p.active);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Insert permission failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Insert permission failed", e);
        }
    }

    public Permission findById(int permId) throws SQLException {
        String sql = "SELECT PermID, Code, Name, Category, Description From Permission WHERE PermID = ?";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, permId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Permission p = new Permission();
                    p.permId = rs.getInt("PermID");
                    p.code = rs.getString("Code");
                    p.name = rs.getString("Name");
                    p.category = rs.getString("Category");
                    p.description = rs.getString("Description");
//                    p.active = rs.getBoolean("Active");
                    return p;
                }
            }
        }
        return null;
    }

    public boolean update(Permission p) throws SQLException {
        String sql = "UPDATE Permission SET Code = ?, Name = ?, Category = ?, Description = ? WHERE PermID = ?";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.code);
            ps.setString(2, p.name);
            ps.setString(3, p.category);
            ps.setString(4, p.description);
            ps.setInt(5, p.permId);
//            ps.setBoolean(5, p.active);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int permId) throws SQLException {
        String sql = "DELETE FROM Permission WHERE PermID = ?";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, permId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean exitsByCode(String code) throws SQLException {
        String sql = "SELECT 1 FROM Permission WHERE LOWER(Code) = LOWER(?) LIMIT 1";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByCodeExceptId(String code, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM Permission WHERE LOWER(Code) = LOWER(?) AND PermID <> ? LIMIT 1";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByNameIgnorecaseExceptId(String name, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM Permission WHERE LOWER(Name) = LOWER(?) AND PermID <> ? LIMIT 1";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByNameIgnorecase(String name) throws SQLException {
        String sql = "SELECT 1 FROM Permission WHERE LOWER(Name) = LOWER(?) LIMIT 1";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    public List<Permission> findAll(String keyword, String category) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT PermID, Code, Name, Category FROM Permission WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sb.append("AND (Code LIKE ? OR Name LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");

        }

        if (category != null && !category.isBlank()) {
            sb.append("AND Category = ? ");
            params.add(category);

        }

        sb.append("ORDER BY Category, Code");
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            List<Permission> list = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Permission p = new Permission();
                    p.permId = rs.getInt("PermID");
                    p.code = rs.getString("Code");
                    p.name = rs.getString("Name");
                    p.category = rs.getString("Category");
//                    p.5 = rs.getBoolean("Active");
                    list.add(p);
                }
            }
            return list;
        }
    }

    public Set<Integer> getPermissionIdsOfRole(int roleId) throws SQLException {
        String sql = "SELECT PermID FROM RolePermission WHERE RoleID = ?";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            Set<Integer> set = new HashSet<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    set.add(rs.getInt(1));
                }
                return set;
            }
        }
    }

    public void replacePermissions(int roleId, List<Integer> permIds, int actorUserId) throws SQLException {
        String del = "DELETE FROM RolePermission WHERE RoleID = ?";
        String ins = "INSERT INTO RolePermission(RoleID, PermID, AssignedByUserID, AssignedAt) VALUES(?, ?, ?, NOW())";

        try (Connection c = DbContext.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement psd = c.prepareStatement(del)) {
                psd.setInt(1, roleId);
                psd.executeUpdate();
            }

            if (permIds != null && !permIds.isEmpty()) {
                try (PreparedStatement psi = c.prepareStatement(ins)) {
                    for (Integer pid : permIds) {
                        psi.setInt(1, roleId);
                        psi.setInt(2, pid);
                        psi.setInt(3, actorUserId);

                        psi.addBatch();
                    }

                    psi.executeBatch();
                }
            }

            c.commit();

        } catch (SQLException e) {
            throw e;
        }

    }

    public int findPermIdByCode(String requiredPermission) {
        final String sql =
                "SELECT PermID FROM Permission WHERE Code = ? LIMIT 1";

        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, requiredPermission);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("PermID") : -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("findPermIdByCode failed", e);
        }
    }

    public int countAll(String keyword, String category) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT COUNT(PermID) FROM Permission WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sb.append("AND (Code LIKE ? OR Name LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");

        }

        if (category != null && !category.isBlank()) {
            sb.append("AND LOWER(Category) LIKE LOWER(?) ");
            params.add("%" + category + "%");

        }

        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            }

        }
    }

    public List<Permission> findPaginated(String keyword, String category, int offset, int limit) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT PermID, Code, Name, Category FROM Permission WHERE 1 = 1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sb.append("AND (Code LIKE ? OR Name LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");

        }

        if (category != null && !category.isBlank()) {
            sb.append("AND Category = ? ");
            params.add(category);

        }

        sb.append("ORDER BY Category, Code LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            List<Permission> list = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Permission p = new Permission();
                    p.permId = rs.getInt("PermID");
                    p.code = rs.getString("Code");
                    p.name = rs.getString("Name");
                    p.category = rs.getString("Category");
//                    p.active = rs.getBoolean("Active");
                    list.add(p);
                }
            }
            return list;
        }
    }


}

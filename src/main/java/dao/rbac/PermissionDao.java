package dao.rbac;

import common.DbContext;
import model.rbac.Permission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PermissionDao {

    public List<Permission> findAll(String keyword, String category) throws SQLException {
        StringBuilder sb = new StringBuilder("SELECT PermID, Code, Name, Category, Active FROM Permission WHERE Active = 1 ");
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
                    p.active = rs.getBoolean("Active");
                    list.add(p);
                }
            }
            return list;
        }
    }

    public Set<Integer> getPermissionIdsOfRole(int roleId) throws SQLException{
        String sql = "SELECT PermID FROM RolePermission WHERE RoleID = ?";
        try(Connection c = DbContext.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, roleId);
            Set<Integer> set = new HashSet<>();
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    set.add(rs.getInt(1));
                }
                return set;
            }
        }
    }

    public void replacePermissions(int roleId, List<Integer> permIds, int actorUserId) throws SQLException {
        String del = "DELETE FROM RolePermission WHERE RoleID = ?";
        String ins = "INSERT INTO RolePermission(RoleID, PermID, AssignedByUserID, AssignedAt) VALUES(?, ?, ?, NOW())";

        try(Connection c = DbContext.getConnection()){
            c.setAutoCommit(false);
            try (PreparedStatement psd = c.prepareStatement(del)){
                psd.setInt(1, roleId);
                psd.executeUpdate();
            }

            if(permIds != null && !permIds.isEmpty()){
                try (PreparedStatement psi = c.prepareStatement(ins)){
                    for(Integer pid : permIds){
                        psi.setInt(1, roleId);
                        psi.setInt(2, pid);
                        psi.setInt(3, actorUserId);

                        psi.addBatch();
                    }

                    psi.executeBatch();
                }
            }

            c.commit();

        }catch (SQLException e){
            try{
                if(DbContext.getConnection() != null){
                    DbContext.getConnection().rollback();
                }
            }catch (Exception rollbackEx){
                throw new RuntimeException("Rollback failed", rollbackEx);
            }
        }

    }

    public int findPermIdByCode(String requiredPermission) {
        final String sql =
                "SELECT PermID FROM Permission WHERE Code = ? AND Active = 1 LIMIT 1";

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
}

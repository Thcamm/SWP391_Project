package dao.employee.admin.rbac;

import common.DbContext;
import model.employee.admin.rbac.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MenuDao {
    public List<MenuItem> byPerms (Set<Integer> permIds) throws SQLException {
        String base = "SELECT MenuID, Label, Path, Icon, OrderNo, ParentID, PermID FROM MenuItem ";
        String sql ;
        if(permIds == null || permIds.isEmpty()){
            sql = base + "WHERE PermID IS NULL ORDER BY ParentID, OrderNo";

        }else{
            String in = String.join(",", permIds.stream().map(i -> "?").toList());
            sql = base + "WHERE PermID IS NULL OR PermID IN ("+in+") ORDER BY ParentID, OrderNo";
        }

        try(Connection c = DbContext.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)
        ){
            if(permIds != null){
                int i = 1;
                for(Integer id : permIds){
                    ps.setInt(i++, id);
                }

            }

            List<MenuItem> list = new ArrayList<>();
            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    MenuItem item = new MenuItem();
                    item.menuId = rs.getInt("MenuID");
                    item.label = rs.getString("Label");
                    item.path = rs.getString("Path");
                    item.icon = rs.getString("Icon");
                    item.orderNo = rs.getInt("OrderNo");
                    int pid = rs.getInt("ParentID");
                    item.parentId = rs.wasNull() ? null : pid;
                    int p = rs.getInt("PermID");
                    item.permId = rs.wasNull() ? null : p;

                    list.add(item);
                }
            }
            return list;
        }

    }
}

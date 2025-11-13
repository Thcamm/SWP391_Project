package dao.inventory;

import common.DbContext;
import model.inventory.WorkOrderPart;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class WorkOrderPartDAO extends DbContext {
    public List<WorkOrderPart> getPartsByWorkOrderId(int workOrderId) throws Exception {
        String sql = "SELECT * FROM WorkOrderPart wop " +
                "JOIN WorkOrderDetail wod on wod.DetailID = wop.DetailID " +
                "JOIN WorkOrder wo on wod.WorkOrderID = wo.WorkOrderID WHERE wo.workOrderId = ?";
        List<WorkOrderPart> parts = new java.util.ArrayList<>();
        try(PreparedStatement ps = getConnection().prepareStatement(sql)){
            ps.setInt(1, workOrderId);
            try(ResultSet rs = ps.executeQuery()){

                while (rs.next()){
                    WorkOrderPart part = new WorkOrderPart();
                    part.setWorkOrderPartId(rs.getInt("WorkOrderPartID"));
                    part.setDetailID(rs.getInt("DetailID"));
                    part.setPartDetailID(rs.getInt("PartDetailID"));
                    part.setQuantityUsed(rs.getInt("QuantityUsed"));
                    part.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    parts.add(part);
                    return parts;
                }
            }
        }
        return null;
    }
}

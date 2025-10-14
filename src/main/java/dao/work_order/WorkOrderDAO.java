package dao.work_order;

import common.DbContext;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDAO {
//    public List<WorkOrder> getWorkOrdersByCustomerId(int customerId) {
//        List<WorkOrder> list = new ArrayList<>();
//
//        String sql = """
//            SELECT wo.*
//            FROM WorkOrder wo
//            INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID
//            INNER JOIN Vehicle v ON sr.VehicleID = v.VehicleID
//            INNER JOIN Customer c ON v.CustomerID = c.CustomerID
//            WHERE c.CustomerID = ?
//            ORDER BY wo.CreatedAt DESC
//        """;
//
//        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
//
//            ps.setInt(1, customerId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                WorkOrder wo = new WorkOrder();
//                wo.setWorkOrderId(rs.getInt("WorkOrderID"));
//                wo.setTechManagerId(rs.getInt("TechManagerID"));
//                wo.setRequestId(rs.getInt("RequestID"));
//                wo.setEstimateAmount(rs.getDouble("EstimateAmount"));
//                wo.setStatus(rs.getString("Status"));
//                wo.setCreatedAt(rs.getDate("CreatedAt"));
//                list.add(wo);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    public WorkOrder getWorkOrderById(int workOrderId) {
//        WorkOrder wo = null;
//        String sql = """
//            SELECT *
//            FROM WorkOrder
//            WHERE WorkOrderID = ?
//        """;
//
//        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
//
//            ps.setInt(1, workOrderId);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                wo = new WorkOrder();
//                wo.setWorkOrderId(rs.getInt("WorkOrderID"));
//                wo.setTechManagerId(rs.getInt("TechManagerID"));
//                wo.setRequestId(rs.getInt("RequestID"));
//                wo.setEstimateAmount(rs.getDouble("EstimateAmount"));
//                wo.setStatus(rs.getString("Status"));
//                wo.setCreatedAt(rs.getDate("CreatedAt"));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return wo;
//    }
//
//    public List<WorkOrderDetail> getWorkOrderDetails(int workOrderId) {
//        List<WorkOrderDetail> details = new ArrayList<>();
//
//        String sql = """
//            SELECT *
//            FROM WorkOrderDetail
//            WHERE WorkOrderID = ?
//            ORDER BY WorkOrderDetailID
//        """;
//
//        try (PreparedStatement ps = DbContext.getConnection().prepareStatement(sql)) {
//
//            ps.setInt(1, workOrderId);
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                WorkOrderDetail d = new WorkOrderDetail();
//                d.setDetailId(rs.getInt("WorkOrderDetailID"));
//                d.setSource(rs.getString("Source"));
//                d.setServiceId(rs.getInt("ServiceID"));
//                d.setTaskDescription(rs.getString("TaskDescription"));
//                d.setSource(rs.getString("Source"));
//                d.setApprovalStatus(rs.getString("ApprovalStatus"));
//                d.setEstimateHours(rs.getDouble("EstimateHours"));
//                d.setEstimateAmount(rs.getDouble("EstimateAmount"));
//                details.add(d);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return details;
//    }

}

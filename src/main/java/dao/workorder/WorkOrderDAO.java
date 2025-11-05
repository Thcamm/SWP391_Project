package dao.workorder;

import common.DbContext;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDAO extends DbContext {

    public int createWorkOrder(WorkOrder workOrder) throws SQLException {
        String sql = "INSERT INTO WorkOrder (TechManagerID, RequestID, EstimateAmount, Status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, workOrder.getTechManagerId());
            ps.setInt(2, workOrder.getRequestId());
            ps.setBigDecimal(3, workOrder.getEstimateAmount());
            ps.setString(4, workOrder.getStatus().name());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    /**
     * Create WorkOrder using provided connection (for transactions)
     */
    public int createWorkOrder(Connection conn, WorkOrder workOrder) throws SQLException {
        String sql = "INSERT INTO WorkOrder (TechManagerID, RequestID, EstimateAmount, Status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, workOrder.getTechManagerId());
            ps.setInt(2, workOrder.getRequestId());
            ps.setBigDecimal(3, workOrder.getEstimateAmount());
            ps.setString(4, workOrder.getStatus().name());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    public WorkOrder getWorkOrderById(int workOrderId) throws SQLException {
        String sql = "SELECT * FROM WorkOrder WHERE WorkOrderID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractWorkOrder(rs);
                }
            }
        }
        return null;
    }

    public List<WorkOrder> getWorkOrdersByTechManager(int techManagerId) throws SQLException {
        String sql = "SELECT * FROM WorkOrder WHERE TechManagerID = ?";
        List<WorkOrder> workOrders = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    workOrders.add(extractWorkOrder(rs));
                }
            }
        }
        return workOrders;
    }

    public List<WorkOrder> getAllWorkOrders() throws SQLException {
        String sql = "SELECT * FROM WorkOrder ORDER BY CreatedAt DESC";
        List<WorkOrder> workOrders = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                workOrders.add(extractWorkOrder(rs));
            }
        }
        return workOrders;
    }

    public boolean updateWorkOrderStatus(int workOrderId, WorkOrder.Status status) throws SQLException {
        String sql = "UPDATE WorkOrder SET Status = ? WHERE WorkOrderID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, workOrderId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean addWorkOrderDetail(WorkOrderDetail detail) throws SQLException {
        String sql = "INSERT INTO WorkOrderDetail (WorkOrderID, Source, DiagnosticID, ApprovalStatus, TaskDescription, EstimateHours, EstimateAmount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detail.getWorkOrderId());
            ps.setString(2, detail.getSource().name());
            if (detail.getDiagnosticId() != null) {
                ps.setInt(3, detail.getDiagnosticId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, detail.getApprovalStatus().name());
            ps.setString(5, detail.getTaskDescription());
            ps.setBigDecimal(6, detail.getEstimateHours());
            ps.setBigDecimal(7, detail.getEstimateAmount());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Add WorkOrderDetail using provided connection (for transactions)
     */
    public int addWorkOrderDetail(Connection conn, WorkOrderDetail detail) throws SQLException {
        String sql = "INSERT INTO WorkOrderDetail (WorkOrderID, Source, DiagnosticID, ApprovalStatus, TaskDescription, EstimateHours, EstimateAmount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, detail.getWorkOrderId());
            ps.setString(2, detail.getSource().name());
            if (detail.getDiagnosticId() != null) {
                ps.setInt(3, detail.getDiagnosticId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            if (detail.getApprovalStatus() != null) {
                ps.setString(4, detail.getApprovalStatus().name());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            ps.setString(5, detail.getTaskDescription());
            ps.setBigDecimal(6, detail.getEstimateHours());
            ps.setBigDecimal(7, detail.getEstimateAmount());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    // Helper method to map ResultSet to WorkOrder
    private WorkOrder extractWorkOrder(ResultSet rs) throws SQLException {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setWorkOrderId(rs.getInt("WorkOrderID"));
        workOrder.setTechManagerId(rs.getInt("TechManagerID"));
        workOrder.setRequestId(rs.getInt("RequestID"));
        workOrder.setEstimateAmount(rs.getBigDecimal("EstimateAmount"));
        workOrder.setStatus(WorkOrder.Status.valueOf(rs.getString("Status")));
        workOrder.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return workOrder;
    }

    private void maybeCloseWorkOrder(Connection c, int detailId) throws SQLException {
        Integer woId = null;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT WorkOrderID FROM WorkOrderDetail WHERE DetailID = ?")) {
            ps.setInt(1, detailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) woId = rs.getInt(1);
            }
        }
        if (woId == null) return;

        // còn WOD nào chưa complete?
        boolean anyNotComplete;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM WorkOrderDetail WHERE WorkOrderID = ? AND (detail_status IS NULL OR detail_status <> 'COMPLETE') LIMIT 1")) {
            ps.setInt(1, woId);
            try (ResultSet rs = ps.executeQuery()) {
                anyNotComplete = rs.next();
            }
        }
        if (!anyNotComplete) {
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE WorkOrder SET Status = 'COMPLETE' WHERE WorkOrderID = ?")) {
                ps.setInt(1, woId);
                ps.executeUpdate();
            }
        }
    }

}

package dao.workorder;

import common.DbContext;
import model.workorder.WorkOrderDetail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDetailDAO extends DbContext {

    public int createWorkOrderDetail(WorkOrderDetail detail) throws SQLException {
        // Note: Column names in DB are lowercase with underscores: source,
        // diagnostic_id, approval_status
        String sql = "INSERT INTO WorkOrderDetail (WorkOrderID, source, diagnostic_id, approval_status, TaskDescription, EstimateHours, EstimateAmount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
     * Create WorkOrderDetail using provided connection (for transactions)
     * This is used when you need to create detail within an existing transaction
     */
    public int createWorkOrderDetail(Connection conn, WorkOrderDetail detail) throws SQLException {
        String sql = "INSERT INTO WorkOrderDetail (WorkOrderID, source, diagnostic_id, approval_status, TaskDescription, EstimateHours, EstimateAmount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

    public WorkOrderDetail getWorkOrderDetailById(int detailId) throws SQLException {
        String sql = "SELECT * FROM WorkOrderDetail WHERE DetailID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractWorkOrderDetail(rs);
                }
            }
        }
        return null;
    }

    public List<WorkOrderDetail> getWorkOrderDetailsByWorkOrder(int workOrderId) throws SQLException {
        String sql = "SELECT * FROM WorkOrderDetail WHERE WorkOrderID = ? ORDER BY DetailID";
        List<WorkOrderDetail> details = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(extractWorkOrderDetail(rs));
                }
            }
        }
        return details;
    }

    public boolean updateWorkOrderDetail(WorkOrderDetail detail) throws SQLException {
        String sql = "UPDATE WorkOrderDetail SET TaskDescription = ?, EstimateHours = ?, EstimateAmount = ? WHERE DetailID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, detail.getTaskDescription());
            ps.setBigDecimal(2, detail.getEstimateHours());
            ps.setBigDecimal(3, detail.getEstimateAmount());
            ps.setInt(4, detail.getDetailId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteWorkOrderDetail(int detailId) throws SQLException {
        String sql = "DELETE FROM WorkOrderDetail WHERE DetailID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean approveWorkOrderDetail(int detailId, int approvedByUserId) throws SQLException {
        // Note: Column names: approval_status, approved_by_user_id, approved_at
        String sql = "UPDATE WorkOrderDetail SET approval_status = ?, approved_by_user_id = ?, approved_at = CURRENT_TIMESTAMP WHERE DetailID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, WorkOrderDetail.ApprovalStatus.APPROVED.name());
            ps.setInt(2, approvedByUserId);
            ps.setInt(3, detailId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean declineWorkOrderDetail(int detailId, int approvedByUserId) throws SQLException {
        // Note: Column names: approval_status, approved_by_user_id, approved_at
        String sql = "UPDATE WorkOrderDetail SET approval_status = ?, approved_by_user_id = ?, approved_at = CURRENT_TIMESTAMP WHERE DetailID = ?";
        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, WorkOrderDetail.ApprovalStatus.DECLINED.name());
            ps.setInt(2, approvedByUserId);
            ps.setInt(3, detailId);
            return ps.executeUpdate() > 0;
        }
    }

    // Helper method to map ResultSet to WorkOrderDetail
    private WorkOrderDetail extractWorkOrderDetail(ResultSet rs) throws SQLException {
        WorkOrderDetail detail = new WorkOrderDetail();
        detail.setDetailId(rs.getInt("DetailID"));
        detail.setWorkOrderId(rs.getInt("WorkOrderID"));
        detail.setSource(WorkOrderDetail.Source.valueOf(rs.getString("source")));

        int diagnosticId = rs.getInt("diagnostic_id");
        if (!rs.wasNull()) {
            detail.setDiagnosticId(diagnosticId);
        }

        detail.setApprovalStatus(WorkOrderDetail.ApprovalStatus.valueOf(rs.getString("approval_status")));

        int approvedByUserId = rs.getInt("approved_by_user_id");
        if (!rs.wasNull()) {
            detail.setApprovedByUserId(approvedByUserId);
        }

        detail.setApprovedAt(rs.getTimestamp("approved_at"));
        detail.setTaskDescription(rs.getString("TaskDescription"));
        detail.setEstimateHours(rs.getBigDecimal("EstimateHours"));
        detail.setEstimateAmount(rs.getBigDecimal("EstimateAmount"));
        detail.setActualHours(rs.getBigDecimal("ActualHours"));

        return detail;
    }
}
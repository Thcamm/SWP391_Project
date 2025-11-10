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

public class WorkOrderDetailDAO {

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

    public void recomputeActualHoursAndMaybeMarkComplete(int assignmentId) {
        String sql = """
        SELECT DetailID
        FROM TaskAssignment
        WHERE AssignmentID = ?
    """;
        try (Connection c = DbContext.getConnection()) {
            c.setAutoCommit(false);

            Integer detailId = null;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, assignmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) detailId = rs.getInt(1);
                }
            }
            if (detailId == null) { c.rollback(); return; }

            // 1) Tính tổng giờ thực tế của tất cả task thuộc DetailID
            String updHours = """
            UPDATE WorkOrderDetail wod
            JOIN (
                SELECT ta.DetailID,
                       COALESCE(SUM(
                           GREATEST(0,
                               TIMESTAMPDIFF(MINUTE, ta.StartAt,
                                   COALESCE(ta.CompleteAt, NOW()))
                           )
                       ),0) AS minutes_total
                FROM TaskAssignment ta
                WHERE ta.DetailID = ?
                GROUP BY ta.DetailID
            ) x ON x.DetailID = wod.DetailID
            SET wod.ActualHours = ROUND(x.minutes_total / 60.0, 2)
            WHERE wod.DetailID = ?
        """;
            try (PreparedStatement ps = c.prepareStatement(updHours)) {
                ps.setInt(1, detailId);
                ps.setInt(2, detailId);
                ps.executeUpdate();
            }

            // 2) Kiểm tra tất cả task của WOD đã COMPLETE chưa
            String allDoneSql = """
            SELECT SUM(CASE WHEN Status = 'COMPLETE' THEN 1 ELSE 0 END) AS done_cnt,
                   COUNT(*) AS all_cnt
            FROM TaskAssignment
            WHERE DetailID = ?
        """;
            boolean allDone = false;
            try (PreparedStatement ps = c.prepareStatement(allDoneSql)) {
                ps.setInt(1, detailId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int done = rs.getInt("done_cnt");
                        int allc = rs.getInt("all_cnt");
                        allDone = (allc > 0 && done == allc);
                    }
                }
            }

            if (allDone) {
                String markDone = "UPDATE WorkOrderDetail SET detail_status = 'COMPLETE' WHERE DetailID = ?";
                try (PreparedStatement ps = c.prepareStatement(markDone)) {
                    ps.setInt(1, detailId);
                    ps.executeUpdate();
                }
            }


            c.commit();
        } catch (SQLException e) {
            // log error
        }

    }


    public boolean hasPendingApprovalOrOpenWorkOrder(int assignmentId) {
        // 1) có work order detail linked đến diagnostic của assignment này mà approval_status = 'PENDING'
        final String sqlPending =
                "SELECT 1 " +
                        "FROM workorderdetail wod " +
                        "JOIN vehiclediagnostic vd ON vd.VehicleDiagnosticID = wod.diagnostic_id " +
                        "WHERE vd.AssignmentID = ? " +
                        "  AND (wod.approval_status = 'PENDING' OR wod.approval_status IS NULL) " +
                        "LIMIT 1";

        // 2) hoặc workorder chưa completed (nếu bạn có cột status ở workorder)
        final String sqlOpenWO =
                "SELECT 1 " +
                        "FROM workorderdetail wod " +
                        "JOIN workorder wo ON wo.WorkOrderID = wod.WorkOrderID " +
                        "JOIN vehiclediagnostic vd ON vd.VehicleDiagnosticID = wod.diagnostic_id " +
                        "WHERE vd.AssignmentID = ? " +
                        "  AND (wo.status IS NULL OR wo.status <> 'COMPLETED') " +
                        "LIMIT 1";

        try (Connection c = DbContext.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(sqlPending)) {
                ps.setInt(1, assignmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return true;
                }
            }
            try (PreparedStatement ps = c.prepareStatement(sqlOpenWO)) {
                ps.setInt(1, assignmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return true;
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // an toàn: lỗi -> chặn complete
        }
    }
}
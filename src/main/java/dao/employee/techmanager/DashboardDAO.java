package dao.employee.techmanager;

import common.DbContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for Tech Manager Dashboard statistics.
 * Handles database queries for dashboard metrics across 6 workflow phases.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class DashboardDAO {

    // =========================================================================
    // PHASE 1: RECEPTION & DIAGNOSIS ASSIGNMENT
    // =========================================================================

    /**
     * Count ServiceRequests in 'PENDING' status.
     * These are new requests awaiting TM approval.
     * 
     * @return count of pending service requests
     * @throws SQLException if database error occurs
     */
    public int countPendingServiceRequests() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ServiceRequest WHERE Status = 'PENDING'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count Diagnosis tasks that are 'ASSIGNED'.
     * These tasks have been assigned by TM but not yet started by Technician.
     * 
     * @return count of assigned diagnosis tasks
     * @throws SQLException if database error occurs
     */
    public int countAssignedDiagnosis() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE task_type = 'DIAGNOSIS' AND Status = 'ASSIGNED'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // PHASE 2: KTV DIAGNOSIS & QUOTE CREATION
    // =========================================================================

    /**
     * Count Diagnosis tasks that are 'IN_PROGRESS'.
     * The Technician has started diagnosis and is preparing the quote.
     * 
     * @return count of in-progress diagnosis tasks
     * @throws SQLException if database error occurs
     */
    public int countInProgressDiagnosis() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE task_type = 'DIAGNOSIS' AND Status = 'IN_PROGRESS'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count VehicleDiagnostics (Quotes) in 'SUBMITTED' status.
     * These are quotes created by Technicians awaiting customer approval.
     * 
     * @return count of quotes pending customer approval
     * @throws SQLException if database error occurs
     */
    public int countPendingCustomerApproval() throws SQLException {
        String sql = "SELECT COUNT(*) FROM VehicleDiagnostic WHERE Status = 'SUBMITTED'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // PHASE 3 & 4: CUSTOMER APPROVAL + AUTO-BRIDGE
    // =========================================================================

    /**
     * Count WorkOrderDetails (source='DIAGNOSTIC') that don't have a TaskAssignment
     * yet.
     * These are created automatically by trigger after customer approval
     * and are waiting for TM to assign repair tasks.
     * 
     * @return count of unassigned WorkOrderDetails
     * @throws SQLException if database error occurs
     */
    public int countUnassignedWorkOrderDetails() throws SQLException {
        String sql = "SELECT COUNT(*) FROM WorkOrderDetail wod " +
                "WHERE wod.source = 'DIAGNOSTIC' " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM TaskAssignment ta " +
                "    WHERE ta.DetailID = wod.DetailID AND ta.task_type = 'REPAIR'" +
                ")";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // PHASE 5: REPAIR ASSIGNMENT
    // =========================================================================

    /**
     * Count Repair tasks that are 'ASSIGNED' (waiting for KTV to start).
     * 
     * @return count of assigned repair tasks
     * @throws SQLException if database error occurs
     */
    public int countAssignedRepairs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE task_type = 'REPAIR' AND Status = 'ASSIGNED'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count Repair tasks that are 'IN_PROGRESS' (KTV is working).
     * 
     * @return count of in-progress repair tasks
     * @throws SQLException if database error occurs
     */
    public int countInProgressRepairs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE task_type = 'REPAIR' AND Status = 'IN_PROGRESS'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // PHASE 6 & 7: COMPLETION & CLOSURE
    // =========================================================================

    /**
     * Count Repair tasks that are 'COMPLETE'.
     * These are finished by KTV and pending final review by TM.
     * 
     * @return count of completed repair tasks
     * @throws SQLException if database error occurs
     */
    public int countCompletedRepairs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE task_type = 'REPAIR' AND Status = 'COMPLETE'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count WorkOrders where all tasks are COMPLETE but WorkOrder status is still
     * OPEN.
     * These are ready for TM to close.
     * 
     * @return count of work orders ready for closure
     * @throws SQLException if database error occurs
     */
    public int countWorkOrdersReadyForClosure() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT wo.WorkOrderID) " +
                "FROM WorkOrder wo " +
                "WHERE wo.Status = 'OPEN' " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM WorkOrderDetail wod " +
                "    LEFT JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "    WHERE wod.WorkOrderID = wo.WorkOrderID " +
                "    AND (ta.AssignmentID IS NULL OR ta.Status != 'COMPLETE')" +
                ")";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count WorkOrders with Status = 'CLOSED' or 'COMPLETE'.
     * 
     * @return count of closed work orders
     * @throws SQLException if database error occurs
     */
    public int countClosedWorkOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM WorkOrder " +
                "WHERE Status IN ('CLOSED', 'COMPLETE')";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count all WorkOrders ever created in the system.
     * 
     * @return total count of work orders
     * @throws SQLException if database error occurs
     */
    public int countTotalWorkOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM WorkOrder";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // RECENT ACTIVITY METRICS
    // =========================================================================

    /**
     * Count new ServiceRequests created today.
     * 
     * @return count of today's service requests
     * @throws SQLException if database error occurs
     */
    public int countTodayRequests() throws SQLException {
        String sql = "SELECT COUNT(*) FROM ServiceRequest " +
                "WHERE DATE(RequestDate) = CURDATE()";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count TaskAssignments marked 'COMPLETE' within the current week.
     * 
     * @return count of tasks completed this week
     * @throws SQLException if database error occurs
     */
    public int countThisWeekCompleted() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE Status = 'COMPLETE' " +
                "AND CompleteAt IS NOT NULL " +
                "AND WEEK(CompleteAt) = WEEK(NOW())";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

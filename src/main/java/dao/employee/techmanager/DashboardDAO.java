package dao.employee.techmanager;

import common.DbContext;
import model.dto.ActivityLogDTO;
import model.dto.DiagnosticApprovalDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Tech Manager Dashboard statistics.
 * Handles database queries for dashboard metrics across 7 workflow phases.
 * 
 * @author SWP391 Team
 * @version 2.0 (Enhanced with activity logs & diagnostic monitoring)
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
     * DEPRECATED: Triage step removed - Classification now happens during approval.
     * Kept for backward compatibility only.
     * 
     * @return always returns 0
     * @throws SQLException if database error occurs
     * @deprecated Direct Classification workflow - Remove after confirming no
     *             dependencies
     */
    @Deprecated
    public int countPendingTriageDetails() throws SQLException {
        // Feature deprecated - Direct Classification in approval step
        return 0;
    }

    /**
     * LUỒNG MỚI - GĐ 3: Count WorkOrderDetails (DIAGNOSTIC only) that need
     * diagnosis assignment
     * for a specific Tech Manager.
     * 
     * REFACTORED: Changed from source='REQUEST' to source='DIAGNOSTIC'
     * - After Triage, only DIAGNOSTIC services need diagnosis
     * - REQUEST services skip diagnosis and go directly to repair
     * 
     * @param techManagerId the Tech Manager's employee ID
     * @return count of details needing diagnosis assignment
     * @throws SQLException if database error occurs
     */
    public int countAssignedDiagnosisForManager(int techManagerId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT wd.DetailID) " +
                "FROM WorkOrderDetail wd " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "LEFT JOIN TaskAssignment ta ON wd.DetailID = ta.DetailID AND ta.task_type = 'DIAGNOSIS' " +
                "WHERE wo.TechManagerID = ? " +
                "AND wd.source = 'DIAGNOSTIC' " + // CHANGED: Only DIAGNOSTIC needs diagnosis
                "AND (wo.Status = 'PENDING' OR wo.Status = 'IN_PROCESS') " +
                "AND ta.AssignmentID IS NULL";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Count WorkOrderDetails (from DIAGNOSTIC source) that need diagnosis
     * assignment.
     * DEPRECATED: Use countAssignedDiagnosisForManager() instead to get accurate
     * count.
     * This method counts ALL unassigned details regardless of TechManager.
     * 
     * LUỒNG MỚI: Changed from source='REQUEST' to source='DIAGNOSTIC'
     * 
     * @return count of details needing diagnosis assignment
     * @throws SQLException if database error occurs
     */
    @Deprecated
    public int countAssignedDiagnosis() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT wd.DetailID) " +
                "FROM WorkOrderDetail wd " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "LEFT JOIN TaskAssignment ta ON wd.DetailID ta.DetailID AND ta.task_type = 'DIAGNOSIS' " +
                "WHERE wd.source = 'DIAGNOSTIC' " + // CHANGED: Only DIAGNOSTIC needs diagnosis (LUỒNG MỚI)
                "AND (wo.Status = 'PENDING' OR wo.Status = 'IN_PROCESS') " +
                "AND ta.AssignmentID IS NULL";

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
     * Count Diagnosis tasks that are 'IN_PROGRESS' for a specific Tech Manager.
     * The Technician has started diagnosis and is preparing the quote.
     * 
     * @param techManagerId the Tech Manager's employee ID
     * @return count of in-progress diagnosis tasks
     * @throws SQLException if database error occurs
     */
    public int countInProgressDiagnosisForManager(int techManagerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "WHERE wo.TechManagerID = ? " +
                "AND ta.task_type = 'DIAGNOSIS' " +
                "AND ta.Status = 'IN_PROGRESS'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /**
     * Count Diagnosis tasks that are 'IN_PROGRESS'.
     * DEPRECATED: Use countInProgressDiagnosisForManager() instead.
     * This method counts ALL in-progress diagnosis tasks regardless of TechManager.
     * 
     * @return count of in-progress diagnosis tasks
     * @throws SQLException if database error occurs
     */
    @Deprecated
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
     * LUỒNG MỚI - GĐ 5: Count WorkOrderDetails (BOTH sources) awaiting repair
     * assignment.
     * 
     * REFACTORED: Changed from only DIAGNOSTIC to BOTH REQUEST and DIAGNOSTIC
     * - REQUEST: Services classified as "Làm luôn" in Triage (skip diagnosis)
     * - DIAGNOSTIC: Services that went through diagnosis and were approved
     * 
     * These are created either:
     * 1. From Triage (source='REQUEST') - Direct repair
     * 2. From trigger after customer approval (source='DIAGNOSTIC') -
     * Post-diagnosis repair
     * 
     * @return count of unassigned WorkOrderDetails
     * @throws SQLException if database error occurs
     */
    public int countUnassignedWorkOrderDetails() throws SQLException {
        // DEBUG: Also get details to compare with RepairAssignmentDAO
        String debugSql = "SELECT wod.DetailID, wod.approval_status, wod.source, " +
                "(SELECT COUNT(*) FROM TaskAssignment ta WHERE ta.DetailID = wod.DetailID AND ta.task_type = 'REPAIR') as hasRepair "
                +
                "FROM WorkOrderDetail wod " +
                "WHERE (wod.source = 'REQUEST' OR wod.source = 'DIAGNOSTIC')";

        String sql = "SELECT COUNT(*) FROM WorkOrderDetail wod " +
                "WHERE wod.approval_status = 'APPROVED' " + // CRITICAL: Must match RepairAssignmentDAO filter
                "AND (wod.source = 'REQUEST' OR wod.source = 'DIAGNOSTIC') " + // CHANGED: Both sources
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM TaskAssignment ta " +
                "    WHERE ta.DetailID = wod.DetailID AND ta.task_type = 'REPAIR'" +
                ")";

        try (Connection conn = DbContext.getConnection()) {
            // DEBUG: Log all WODs with REQUEST/DIAGNOSTIC source
            System.out.println("\n=== [DashboardDAO.countUnassignedWorkOrderDetails] DEBUG ===");
            try (PreparedStatement debugPs = conn.prepareStatement(debugSql);
                    ResultSet debugRs = debugPs.executeQuery()) {
                int totalCount = 0;
                int approvedCount = 0;
                int notApprovedCount = 0;
                while (debugRs.next()) {
                    totalCount++;
                    int detailId = debugRs.getInt("DetailID");
                    String approval = debugRs.getString("approval_status");
                    String source = debugRs.getString("source");
                    int hasRepair = debugRs.getInt("hasRepair");

                    if ("APPROVED".equals(approval) && hasRepair == 0) {
                        approvedCount++;
                        System.out.println("  ✓ DetailID=" + detailId + ", source=" + source + ", approval=" + approval
                                + ", hasRepair=" + hasRepair);
                    } else {
                        notApprovedCount++;
                        System.out.println("  ✗ DetailID=" + detailId + ", source=" + source + ", approval=" + approval
                                + ", hasRepair=" + hasRepair + " (FILTERED OUT)");
                    }
                }
                System.out.println("Total WODs with REQUEST/DIAGNOSTIC: " + totalCount);
                System.out.println("APPROVED + No Repair: " + approvedCount);
                System.out.println("Not matching criteria: " + notApprovedCount);
            }

            // Actual count query
            try (PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                int count = rs.next() ? rs.getInt(1) : 0;
                System.out.println("Final COUNT result: " + count);
                System.out.println("=== [DashboardDAO] DEBUG END ===\n");
                return count;
            }
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
     * IN_PROCESS.
     * These are ready for TM to close (GĐ7).
     * 
     * @return count of work orders ready for closure
     * @throws SQLException if database error occurs
     */
    public int countWorkOrdersReadyForClosure() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT wo.WorkOrderID) " +
                "FROM WorkOrder wo " +
                "WHERE wo.Status = 'IN_PROCESS' " +
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
     * Count WorkOrders with Status = 'COMPLETE'.
     * 
     * @return count of closed work orders
     * @throws SQLException if database error occurs
     */
    public int countClosedWorkOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM WorkOrder " +
                "WHERE Status = 'COMPLETE'";

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

    // =========================================================================
    // NEW: RECENT ACTIVITY LOGS (GĐ ALL)
    // =========================================================================

    /**
     * Get recent technician activities for dashboard display.
     * Shows last 20 activities ordered by time (most recent first).
     * 
     * @return list of activity log DTOs
     * @throws SQLException if database error occurs
     */
    public List<ActivityLogDTO> getRecentActivities() throws SQLException {
        String sql = "SELECT " +
                "    tal.ActivityID, " +
                "    u.FullName AS TechnicianName, " +
                "    tal.ActivityType, " +
                "    tal.Description, " +
                "    tal.ActivityTime, " +
                "    tal.TaskAssignmentID, " +
                "    ta.TaskDescription, " +
                "    v.Brand AS VehicleInfo " +
                "FROM TechnicianActivityLog tal " +
                "JOIN Employee e ON tal.TechnicianID = e.EmployeeID " +
                "JOIN User u ON e.UserID = u.UserID " +
                "LEFT JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                "LEFT JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "LEFT JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "LEFT JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "LEFT JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "ORDER BY tal.ActivityTime DESC " +
                "LIMIT 20";

        List<ActivityLogDTO> activities = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ActivityLogDTO dto = new ActivityLogDTO();
                dto.setActivityID(rs.getInt("ActivityID"));
                dto.setTechnicianName(rs.getString("TechnicianName"));
                dto.setActivityType(rs.getString("ActivityType"));
                dto.setDescription(rs.getString("Description"));
                dto.setActivityTime(rs.getTimestamp("ActivityTime"));
                dto.setTaskAssignmentID(rs.getObject("TaskAssignmentID") != null
                        ? rs.getInt("TaskAssignmentID")
                        : null);
                dto.setTaskDescription(rs.getString("TaskDescription"));
                dto.setVehicleInfo(rs.getString("VehicleInfo"));
                activities.add(dto);
            }
        }

        return activities;
    }

    // =========================================================================
    // NEW: DIAGNOSTIC MONITORING (GĐ3)
    // =========================================================================

    /**
     * Get diagnostics (quotes) pending customer approval.
     * Ordered by days pending (oldest first).
     * 
     * @return list of diagnostic approval DTOs
     * @throws SQLException if database error occurs
     */
    public List<DiagnosticApprovalDTO> getPendingDiagnosticApprovals() throws SQLException {
        String sql = "SELECT " +
                "    vd.VehicleDiagnosticID, " +
                "    wo.WorkOrderID, " +
                "    CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "    u_cust.FullName AS CustomerName, " +
                "    u_cust.PhoneNumber AS CustomerPhone, " +
                "    vd.IssueFound, " +
                "    vd.EstimateCost, " +
                "    vd.Status, " +
                "    vd.CreatedAt, " +
                "    DATEDIFF(NOW(), vd.CreatedAt) AS DaysPending, " +
                "    u_tech.FullName AS TechnicianName, " +
                "    e_tech.EmployeeID AS TechnicianID " +
                "FROM VehicleDiagnostic vd " +
                "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                "JOIN Employee e_tech ON ta.AssignToTechID = e_tech.EmployeeID " +
                "JOIN User u_tech ON e_tech.UserID = u_tech.UserID " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "WHERE vd.Status = 'SUBMITTED' " +
                "ORDER BY DaysPending DESC";

        List<DiagnosticApprovalDTO> diagnostics = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DiagnosticApprovalDTO dto = new DiagnosticApprovalDTO();
                dto.setDiagnosticID(rs.getInt("VehicleDiagnosticID"));
                dto.setWorkOrderID(rs.getInt("WorkOrderID"));
                dto.setVehicleInfo(rs.getString("VehicleInfo"));
                dto.setCustomerName(rs.getString("CustomerName"));
                dto.setCustomerPhone(rs.getString("CustomerPhone"));
                dto.setIssueFound(rs.getString("IssueFound"));
                dto.setEstimateCost(rs.getBigDecimal("EstimateCost"));
                dto.setStatus(rs.getString("Status"));
                dto.setCreatedAt(rs.getTimestamp("CreatedAt"));
                dto.setDaysPending(rs.getInt("DaysPending"));
                dto.setTechnicianName(rs.getString("TechnicianName"));
                dto.setTechnicianID(rs.getInt("TechnicianID"));
                diagnostics.add(dto);
            }
        }

        return diagnostics;
    }
}

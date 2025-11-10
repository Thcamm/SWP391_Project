package service.employee.techmanager;

import dao.employee.techmanager.DashboardDAO;
import model.dto.ActivityLogDTO;
import model.dto.DiagnosticApprovalDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for Tech Manager Dashboard operations.
 * Handles business logic for dashboard statistics across 7 workflow phases.
 * 
 * @author SWP391 Team
 * @version 2.0 (Enhanced with activity monitoring)
 */
public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAO();
    }

    // =========================================================================
    // PHASE 1: RECEPTION & DIAGNOSIS ASSIGNMENT
    // =========================================================================

    /**
     * Get count of pending service requests (awaiting TM approval).
     * 
     * @return count of pending requests
     * @throws SQLException if database error occurs
     */
    public int countPendingServiceRequests() throws SQLException {
        return dashboardDAO.countPendingServiceRequests();
    }

    /**
     * Get count of assigned diagnosis tasks (assigned but not started).
     * 
     * @return count of assigned diagnosis tasks
     * @throws SQLException if database error occurs
     */
    public int countAssignedDiagnosis() throws SQLException {
        return dashboardDAO.countAssignedDiagnosis();
    }

    // =========================================================================
    // PHASE 2: KTV DIAGNOSIS & QUOTE CREATION
    // =========================================================================

    /**
     * Get count of in-progress diagnosis tasks (KTV is diagnosing).
     * 
     * @return count of in-progress diagnosis
     * @throws SQLException if database error occurs
     */
    public int countInProgressDiagnosis() throws SQLException {
        return dashboardDAO.countInProgressDiagnosis();
    }

    /**
     * Get count of quotes pending customer approval (SUBMITTED status).
     * 
     * @return count of pending quotes
     * @throws SQLException if database error occurs
     */
    public int countPendingCustomerApproval() throws SQLException {
        return dashboardDAO.countPendingCustomerApproval();
    }

    // =========================================================================
    // PHASE 3 & 4: CUSTOMER APPROVAL + AUTO-BRIDGE
    // =========================================================================

    /**
     * Get count of WorkOrderDetails created by trigger but not yet assigned.
     * These are from approved quotes waiting for TM to assign repair tasks.
     * 
     * @return count of unassigned WorkOrderDetails
     * @throws SQLException if database error occurs
     */
    public int countUnassignedWorkOrderDetails() throws SQLException {
        return dashboardDAO.countUnassignedWorkOrderDetails();
    }

    // =========================================================================
    // PHASE 5: REPAIR ASSIGNMENT
    // =========================================================================

    /**
     * Get count of assigned repair tasks (assigned but not started).
     * 
     * @return count of assigned repairs
     * @throws SQLException if database error occurs
     */
    public int countAssignedRepairs() throws SQLException {
        return dashboardDAO.countAssignedRepairs();
    }

    /**
     * Get count of in-progress repair tasks (KTV is working).
     * 
     * @return count of in-progress repairs
     * @throws SQLException if database error occurs
     */
    public int countInProgressRepairs() throws SQLException {
        return dashboardDAO.countInProgressRepairs();
    }

    // =========================================================================
    // PHASE 6 & 7: COMPLETION & CLOSURE
    // =========================================================================

    /**
     * Get count of completed repair tasks (finished by KTV).
     * 
     * @return count of completed repairs
     * @throws SQLException if database error occurs
     */
    public int countCompletedRepairs() throws SQLException {
        return dashboardDAO.countCompletedRepairs();
    }

    /**
     * Get count of WorkOrders ready for closure (all tasks complete).
     * 
     * @return count of work orders ready to close
     * @throws SQLException if database error occurs
     */
    public int countWorkOrdersReadyForClosure() throws SQLException {
        return dashboardDAO.countWorkOrdersReadyForClosure();
    }

    /**
     * Get count of closed WorkOrders.
     * 
     * @return count of closed work orders
     * @throws SQLException if database error occurs
     */
    public int countClosedWorkOrders() throws SQLException {
        return dashboardDAO.countClosedWorkOrders();
    }

    /**
     * Get total count of all WorkOrders ever created.
     * 
     * @return total count of work orders
     * @throws SQLException if database error occurs
     */
    public int countTotalWorkOrders() throws SQLException {
        return dashboardDAO.countTotalWorkOrders();
    }

    // =========================================================================
    // RECENT ACTIVITY METRICS
    // =========================================================================

    /**
     * Get count of service requests created today.
     * 
     * @return count of today's requests
     * @throws SQLException if database error occurs
     */
    public int countTodayRequests() throws SQLException {
        return dashboardDAO.countTodayRequests();
    }

    /**
     * Get count of tasks completed this week.
     * 
     * @return count of this week's completed tasks
     * @throws SQLException if database error occurs
     */
    public int countThisWeekCompleted() throws SQLException {
        return dashboardDAO.countThisWeekCompleted();
    }

    // =========================================================================
    // NEW: RECENT ACTIVITY MONITORING
    // =========================================================================

    /**
     * Get recent technician activities for dashboard display.
     * 
     * @return list of recent activities
     * @throws SQLException if database error occurs
     */
    public List<ActivityLogDTO> getRecentActivities() throws SQLException {
        return dashboardDAO.getRecentActivities();
    }

    // =========================================================================
    // NEW: DIAGNOSTIC MONITORING (GĐ3)
    // =========================================================================

    /**
     * Get diagnostics pending customer approval.
     * These are quotes submitted by technicians waiting for customer decision.
     * 
     * @return list of pending diagnostics
     * @throws SQLException if database error occurs
     */
    public List<DiagnosticApprovalDTO> getPendingDiagnosticApprovals() throws SQLException {
        return dashboardDAO.getPendingDiagnosticApprovals();
    }

    /**
     * Get count of overdue diagnostics (pending more than 2 days).
     * This helps TM identify quotes that need customer follow-up.
     * 
     * @return count of overdue diagnostics
     * @throws SQLException if database error occurs
     */
    public int countOverdueDiagnostics() throws SQLException {
        List<DiagnosticApprovalDTO> pending = getPendingDiagnosticApprovals();
        return (int) pending.stream()
                .filter(DiagnosticApprovalDTO::isOverdue)
                .count();
    }
}

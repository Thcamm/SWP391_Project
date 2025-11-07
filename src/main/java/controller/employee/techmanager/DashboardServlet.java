package controller.employee.techmanager;

import dao.workorder.RejectedTaskDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import common.DbContext;

/**
 * Servlet controller for the Technical Manager Dashboard.
 * This servlet gathers and displays key performance indicators (KPIs) and
 * statistics related to the 6-phase garage workflow.
 *
 * <p>
 * <b>NEW WORKFLOW (6 Phases):</b>
 * </p>
 * <ol>
 * <li><b>Phase 1:</b> Reception & Diagnosis Assignment (TM -> KTV)</li>
 * <li><b>Phase 2:</b> KTV Diagnosis & Quote Creation (KTV creates quote)</li>
 * <li><b>Phase 3:</b> Customer Approval (Customer approves/rejects quote)</li>
 * <li><b>Phase 4:</b> System Auto-Bridge (System creates WorkOrderDetail)</li>
 * <li><b>Phase 5:</b> Repair Assignment (TM -> KTV)</li>
 * <li><b>Phase 6:</b> Repair Completion & WorkOrder Closure</li>
 * </ol>
 */
@WebServlet("/techmanager/dashboard")
public class DashboardServlet extends HttpServlet {

    private final RejectedTaskDAO rejectedTaskDAO = new RejectedTaskDAO();

    /**
     * Handles the HTTP GET request by fetching all dashboard statistics
     * and forwarding them to the dashboard JSP.
     *
     * @param request  the HttpServletRequest object.
     * @param response the HttpServletResponse object.
     * @throws ServletException if a servlet-specific error occurs.
     * @throws IOException      if an I/O error occurs.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DbContext.getConnection()) {
            Map<String, Integer> stats = new HashMap<>();

            // ===== Phase 1: Reception & Diagnosis Assignment =====
            stats.put("pendingRequests", countPendingServiceRequests(conn));
            stats.put("assignedDiagnosis", countAssignedDiagnosis(conn));

            // ===== Phase 2: KTV Diagnosis & Quote Creation =====
            stats.put("inProgressDiagnosis", countInProgressDiagnosis(conn));
            stats.put("pendingQuotes", countPendingCustomerApproval(conn));

            // ===== Phase 3 & 4: Customer Approval + Auto-Bridge =====
            stats.put("approvedQuotes", countApprovedQuotes(conn));

            // ===== Phase 5: Repair Assignment =====
            stats.put("activeRepairs", countActiveRepairs(conn));

            // ===== Phase 6: Completion =====
            stats.put("completedRepairs", countCompletedRepairs(conn));
            stats.put("totalWorkOrders", countTotalWorkOrders(conn));

            // ===== Recent Activity =====
            stats.put("todayRequests", countTodayRequests(conn));
            stats.put("thisWeekCompleted", countThisWeekCompleted(conn));

            // ===== Management Alerts (SLA & Reassignment) =====
            stats.put("rejectedTasks", rejectedTaskDAO.countRejectedTasks());
            stats.put("overdueTasks", countOverdueTasks(conn));
            stats.put("declinedTasks", countDeclinedTasks(conn));
            stats.put("tasksNeedReassignment", countTasksNeedReassignment(conn));

            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/view/techmanager/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Use a more specific error message for the user/log
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load dashboard statistics: " + e.getMessage());
        }
    }

    // =========================================================================
    // PHASE 1: RECEPTION & DIAGNOSIS ASSIGNMENT
    // =========================================================================

    /**
     * Counts ServiceRequests in 'PENDING' status.
     * These are new requests awaiting TM approval to be converted into a WorkOrder.
     *
     * @param conn The active database connection.
     * @return The count of pending service requests.
     * @throws SQLException if a database access error occurs.
     */
    private int countPendingServiceRequests(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ServiceRequest WHERE Status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Counts Diagnosis tasks that are 'ASSIGNED'.
     * These tasks have been assigned by the TM but not yet started by a Technician.
     *
     * @param conn The active database connection.
     * @return The count of assigned diagnosis tasks.
     * @throws SQLException if a database access error occurs.
     */
    private int countAssignedDiagnosis(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'DIAGNOSIS' AND Status = 'ASSIGNED'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // PHASE 2: KTV DIAGNOSIS & QUOTE CREATION
    // =========================================================================

    /**
     * Counts Diagnosis tasks that are 'IN_PROGRESS'.
     * The Technician has started the diagnosis and is preparing the quote.
     *
     * @param conn The active database connection.
     * @return The count of in-progress diagnosis tasks.
     * @throws SQLException if a database access error occurs.
     */
    private int countInProgressDiagnosis(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'DIAGNOSIS' AND Status = 'IN_PROGRESS'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Counts VehicleDiagnostics (Quotes) in 'SUBMITTED' status.
     * These are quotes created by Technicians and are awaiting customer approval.
     *
     * @param conn The active database connection.
     * @return The count of quotes pending customer approval.
     * @throws SQLException if a database access error occurs.
     */
    private int countPendingCustomerApproval(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM VehicleDiagnostic WHERE Status = 'SUBMITTED'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // PHASE 3 & 4: CUSTOMER APPROVAL + AUTO-BRIDGE
    // =========================================================================

    /**
     * Counts VehicleDiagnostics (Quotes) 'APPROVED' by the customer.
     * The system bridge should have automatically created a corresponding
     * WorkOrderDetail.
     * These are ready for the TM to assign repair tasks.
     *
     * @param conn The active database connection.
     * @return The count of approved quotes.
     * @throws SQLException if a database access error occurs.
     */
    private int countApprovedQuotes(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM VehicleDiagnostic WHERE Status = 'APPROVED'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // PHASE 5: REPAIR ASSIGNMENT
    // =========================================================================

    /**
     * Counts active Repair tasks.
     * This includes tasks that are 'ASSIGNED' (waiting for KTV) or 'IN_PROGRESS'
     * (KTV is working).
     *
     * @param conn The active database connection.
     * @return The count of active repair tasks.
     * @throws SQLException if a database access error occurs.
     */
    private int countActiveRepairs(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'REPAIR' AND Status IN ('ASSIGNED', 'IN_PROGRESS')";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // PHASE 6: COMPLETION
    // =========================================================================

    /**
     * Counts Repair tasks that are 'COMPLETE'.
     * These are finished by the KTV and are pending final review and WorkOrder
     * closure by the TM.
     *
     * @param conn The active database connection.
     * @return The count of completed repair tasks.
     * @throws SQLException if a database access error occurs.
     */
    private int countCompletedRepairs(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'REPAIR' AND Status = 'COMPLETE'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Counts all WorkOrders ever created in the system.
     *
     * @param conn The active database connection.
     * @return The total count of work orders.
     * @throws SQLException if a database access error occurs.
     */
    private int countTotalWorkOrders(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM WorkOrder";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // RECENT ACTIVITY METRICS
    // =========================================================================

    /**
     * Counts new ServiceRequests created today.
     *
     * @param conn The active database connection.
     * @return The count of today's service requests.
     * @throws SQLException if a database access error occurs.
     */
    private int countTodayRequests(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM ServiceRequest WHERE DATE(RequestDate) = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Counts TaskAssignments marked 'COMPLETE' within the current week.
     *
     * @param conn The active database connection.
     * @return The count of tasks completed this week.
     * @throws SQLException if a database access error occurs.
     */
    private int countThisWeekCompleted(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE Status = 'COMPLETE' AND CompleteAt IS NOT NULL AND WEEK(CompleteAt) = WEEK(NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // =========================================================================
    // MANAGEMENT ALERTS (SLA & REASSIGNMENT)
    // =========================================================================

    /**
     * Counts 'ASSIGNED' tasks that are past their `planned_start` time
     * but have not been started (`StartAt` is NULL).
     * This indicates an SLA violation that the TM needs to address.
     *
     * @param conn The active database connection.
     * @return The count of overdue tasks.
     * @throws SQLException if a database access error occurs.
     */
    private int countOverdueTasks(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE Status = 'ASSIGNED' " +
                "AND StartAt IS NULL " +
                "AND planned_start IS NOT NULL " +
                "AND planned_start < NOW()";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Counts tasks that were proactively 'DECLINED' by a Technician.
     * These tasks are marked as 'CANCELLED' and have a non-null `declined_at`
     * timestamp.
     * They require TM attention for reassignment.
     *
     * @param conn The active database connection.
     * @return The count of declined tasks.
     * @throws SQLException if a database access error occurs.
     */
    private int countDeclinedTasks(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE declined_at IS NOT NULL " +
                "AND Status = 'CANCELLED'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Counts all tasks currently in a 'CANCELLED' state that need reassignment.
     * This aggregates:
     * 1. Tasks proactively declined by Technicians.
     * 2. Tasks manually cancelled by TMs (potentially due to being overdue).
     *
     * @param conn The active database connection.
     * @return The total count of tasks needing reassignment.
     * @throws SQLException if a database access error occurs.
     */
    private int countTasksNeedReassignment(Connection conn) throws SQLException {
        // This query assumes that any 'CANCELLED' task (that was either declined
        // or was overdue) is eligible for reassignment.
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE Status = 'CANCELLED' " +
                "AND (declined_at IS NOT NULL OR " +
                "     (planned_start IS NOT NULL AND planned_start < NOW()))";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
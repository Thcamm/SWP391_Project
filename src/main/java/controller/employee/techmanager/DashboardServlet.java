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
import java.util.HashMap;
import java.util.Map;

import common.DbContext;

/**
 * Tech Manager Dashboard - Overview statistics
 * 
 * NEW WORKFLOW (6 Phases):
 * Phase 1: Reception & Diagnosis Assignment (TM -> KTV)
 * Phase 2: KTV Diagnosis & Quote Creation (KTV creates quote)
 * Phase 3: Customer Approval (Customer approves/rejects quote)
 * Phase 4: System Auto-Bridge (System creates WorkOrderDetail after customer
 * approval)
 * Phase 5: Repair Assignment (TM -> KTV)
 * Phase 6: Repair Completion & WorkOrder Closure
 */
@WebServlet("/techmanager/dashboard")
public class DashboardServlet extends HttpServlet {

    private final RejectedTaskDAO rejectedTaskDAO = new RejectedTaskDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DbContext.getConnection()) {
            Map<String, Integer> stats = new HashMap<>();

            // ===== PHASE 1: Reception & Diagnosis Assignment =====
            stats.put("pendingRequests", countPendingServiceRequests(conn)); // ServiceRequests waiting for TM approval
            stats.put("assignedDiagnosis", countAssignedDiagnosis(conn)); // Diagnosis tasks assigned to Technicians

            // ===== PHASE 2: KTV Diagnosis & Quote Creation =====
            stats.put("inProgressDiagnosis", countInProgressDiagnosis(conn)); // Technicians working on diagnosis
            stats.put("pendingQuotes", countPendingCustomerApproval(conn)); // Quotes waiting for customer approval

            // ===== PHASE 3 & 4: Customer Approval + Auto WorkOrderDetail Creation =====
            stats.put("approvedQuotes", countApprovedQuotes(conn)); // Quotes approved by customer (ready for repair
                                                                    // assignment)

            // ===== PHASE 5: Repair Assignment =====
            stats.put("activeRepairs", countActiveRepairs(conn)); // Repair tasks in progress

            // ===== PHASE 6: Completion =====
            stats.put("completedRepairs", countCompletedRepairs(conn)); // Completed repairs (ready for closure)
            stats.put("totalWorkOrders", countTotalWorkOrders(conn)); // Total work orders in system

            // ===== Recent Activity =====
            stats.put("todayRequests", countTodayRequests(conn)); // New requests today
            stats.put("thisWeekCompleted", countThisWeekCompleted(conn)); // Completed this week

            // ===== Management Alerts =====
            stats.put("rejectedTasks", rejectedTaskDAO.countRejectedTasks()); // Tasks rejected by technicians

            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/view/techmanager/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading dashboard: " + e.getMessage());
        }
    }

    // ===== PHASE 1: Reception & Diagnosis Assignment =====

    /**
     * Count ServiceRequests that are PENDING (waiting for TM to approve and create
     * WorkOrder)
     */
    private int countPendingServiceRequests(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM ServiceRequest WHERE Status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count Diagnosis tasks that are ASSIGNED (waiting for Technician to start)
     */
    private int countAssignedDiagnosis(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'DIAGNOSIS' AND Status = 'ASSIGNED'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ===== PHASE 2: KTV Diagnosis & Quote Creation =====

    /**
     * Count Diagnosis tasks IN_PROGRESS (Technician is working on diagnosis &
     * creating quote)
     */
    private int countInProgressDiagnosis(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'DIAGNOSIS' AND Status = 'IN_PROGRESS'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count VehicleDiagnostics (Quotes) that are SUBMITTED (waiting for customer
     * approval)
     */
    private int countPendingCustomerApproval(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM VehicleDiagnostic WHERE Status = 'SUBMITTED'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ===== PHASE 3 & 4: Customer Approval + Auto WorkOrderDetail =====

    /**
     * Count VehicleDiagnostics (Quotes) APPROVED by customer
     * These are ready for TM to assign repair tasks
     * (System should auto-create WorkOrderDetail when customer approves)
     */
    private int countApprovedQuotes(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM VehicleDiagnostic WHERE Status = 'APPROVED'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ===== PHASE 5: Repair Assignment =====

    /**
     * Count Repair tasks that are active (ASSIGNED or IN_PROGRESS)
     */
    private int countActiveRepairs(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'REPAIR' AND Status IN ('ASSIGNED', 'IN_PROGRESS')";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ===== PHASE 6: Completion =====

    /**
     * Count Repair tasks that are COMPLETE (ready for TM to close WorkOrder)
     */
    private int countCompletedRepairs(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'REPAIR' AND Status = 'COMPLETE'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count total WorkOrders in the system
     */
    private int countTotalWorkOrders(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM WorkOrder";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ===== RECENT ACTIVITY =====

    /**
     * Count ServiceRequests created today
     */
    private int countTodayRequests(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM ServiceRequest WHERE DATE(RequestDate) = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count TaskAssignments completed this week
     */
    private int countThisWeekCompleted(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE Status = 'COMPLETE' AND CompleteAt IS NOT NULL AND WEEK(CompleteAt) = WEEK(NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

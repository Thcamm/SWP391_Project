package controller.employee.techmanager;

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
 */
@WebServlet("/techmanager/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DbContext.getConnection()) {
            Map<String, Integer> stats = new HashMap<>();

            // Phase 1 Statistics
            stats.put("pendingRequests", countPendingRequests(conn));
            stats.put("assignedDiagnosis", countAssignedDiagnosis(conn));

            // Phase 2 Statistics
            stats.put("completedDiagnosis", countCompletedDiagnosis(conn));
            stats.put("pendingApproval", countPendingCustomerApproval(conn));

            // Phase 3 Statistics
            stats.put("approvedQuotes", countApprovedQuotes(conn));
            stats.put("activeRepairs", countActiveRepairs(conn));

            // Phase 4 Statistics
            stats.put("completedRepairs", countCompletedRepairs(conn));
            stats.put("totalWorkOrders", countTotalWorkOrders(conn));

            // Recent activity
            stats.put("todayRequests", countTodayRequests(conn));
            stats.put("thisWeekCompleted", countThisWeekCompleted(conn));

            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/view/techmanager/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading dashboard: " + e.getMessage());
        }
    }

    // === PHASE 1: Reception & Diagnosis ===

    private int countPendingRequests(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM WorkOrderDetail WHERE approval_status = 'PENDING' AND source = 'RECEPTION'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int countAssignedDiagnosis(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'DIAGNOSTIC' AND status = 'ASSIGNED'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // === PHASE 2: Review & Quote ===

    private int countCompletedDiagnosis(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'DIAGNOSTIC' AND status = 'COMPLETE'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int countPendingCustomerApproval(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM WorkOrderDetail WHERE approval_status = 'PENDING' AND source = 'DIAGNOSTIC'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // === PHASE 3: Repair Assignment ===

    private int countApprovedQuotes(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM WorkOrderDetail WHERE approval_status = 'APPROVED' AND source = 'DIAGNOSTIC'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int countActiveRepairs(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'REPAIR' AND status IN ('ASSIGNED', 'IN_PROGRESS')";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // === PHASE 4: Monitor & Complete ===

    private int countCompletedRepairs(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE task_type = 'REPAIR' AND status = 'COMPLETE'";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int countTotalWorkOrders(Connection conn) throws Exception {
        String sql = "SELECT COUNT(DISTINCT WorkOrderID) FROM WorkOrderDetail";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // === RECENT ACTIVITY ===

    private int countTodayRequests(Connection conn) throws Exception {
        // Count WorkOrders created today (WorkOrder table has CreatedAt column)
        String sql = "SELECT COUNT(DISTINCT wo.WorkOrderID) " +
                "FROM WorkOrder wo " +
                "WHERE DATE(wo.CreatedAt) = CURDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private int countThisWeekCompleted(Connection conn) throws Exception {
        // Column name is CompleteAt (PascalCase) in TaskAssignment table
        String sql = "SELECT COUNT(*) FROM TaskAssignment WHERE Status = 'COMPLETE' AND WEEK(CompleteAt) = WEEK(NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

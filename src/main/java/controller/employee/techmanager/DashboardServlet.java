package controller.employee.techmanager;

import model.dto.ActivityLogDTO;
import model.dto.DiagnosticApprovalDTO;
import service.employee.techmanager.DashboardService;
import service.employee.techmanager.TechManagerService;
import service.employee.techmanager.DeclinedTaskService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet controller for the Technical Manager Dashboard.
 * This servlet gathers and displays key performance indicators (KPIs) and
 * statistics related to the 7-phase garage workflow.
 *
 * <p>
 * <b>NEW WORKFLOW (7 Phases):</b>
 * </p>
 * <ol>
 * <li><b>GĐ0:</b> ServiceRequest Creation (Customer/Receptionist)</li>
 * <li><b>GĐ1:</b> ServiceRequest Approval & Diagnosis Assignment (TM →
 * KTV)</li>
 * <li><b>GĐ2:</b> KTV Diagnosis & Quote Creation (KTV creates
 * VehicleDiagnostic)</li>
 * <li><b>GĐ3:</b> Customer Approval (Customer approves/rejects quote)</li>
 * <li><b>GĐ4:</b> System Auto-Bridge (Trigger creates WorkOrderDetail +
 * WorkOrderPart)</li>
 * <li><b>GĐ5:</b> Repair Assignment (TM → KTV)</li>
 * <li><b>GĐ6:</b> Repair Execution (KTV performs repair)</li>
 * <li><b>GĐ7:</b> WorkOrder Closure (TM closes WorkOrder)</li>
 * </ol>
 * 
 * @version 3.0 (Enhanced with activity monitoring & diagnostic tracking)
 */
@WebServlet("/techmanager/dashboard")
public class DashboardServlet extends HttpServlet {

    private final DashboardService dashboardService = new DashboardService();
    private final TechManagerService techManagerService = new TechManagerService();
    private final DeclinedTaskService declinedTaskService = new DeclinedTaskService();

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

        try {
            // Get TechManager's EmployeeID from session
            HttpSession session = request.getSession();
            String userName = (String) session.getAttribute("userName");

            if (userName == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Get TechManager's Employee ID
            Integer techManagerEmployeeId = techManagerService.getTechManagerEmployeeId(userName);
            if (techManagerEmployeeId == null) {
                request.setAttribute("errorMessage", "TechManager employee record not found");
                request.getRequestDispatcher("/view/error.jsp").forward(request, response);
                return;
            }

            Map<String, Integer> stats = new HashMap<>();

            // ===== GĐ0→1: Tiếp nhận & Duyệt Yêu cầu =====
            stats.put("pendingRequests", dashboardService.countPendingServiceRequests());

            // ===== LUỒNG MỚI - GĐ2: Phân loại (Triage) =====
            stats.put("pendingTriage", dashboardService.countPendingTriageDetails());

            // ===== GĐ1→3: Phân công Chẩn đoán (DIAGNOSTIC only) =====
            // FIX: Use TechManager-specific count instead of global count
            stats.put("assignedDiagnosis", dashboardService.countAssignedDiagnosisForManager(techManagerEmployeeId));

            // ===== GĐ2: KTV Chẩn đoán & Báo giá =====
            // FIX: Use TechManager-specific count instead of global count
            stats.put("inProgressDiagnosis",
                    dashboardService.countInProgressDiagnosisForManager(techManagerEmployeeId));

            // ===== GĐ3: Chờ Khách hàng Duyệt =====
            stats.put("pendingQuotes", dashboardService.countPendingCustomerApproval());
            stats.put("overdueDiagnostics", dashboardService.countOverdueDiagnostics()); // NEW: Báo giá quá hạn

            // ===== GĐ4→5: Cầu nối Tự động → Chờ phân công Sửa chữa (BOTH sources) =====
            stats.put("unassignedWorkOrderDetails", dashboardService.countUnassignedWorkOrderDetails());

            // ===== GĐ5→6: Phân công & Thực hiện Sửa chữa =====
            stats.put("assignedRepairs", dashboardService.countAssignedRepairs());
            stats.put("inProgressRepairs", dashboardService.countInProgressRepairs());
            stats.put("activeRepairs", stats.get("assignedRepairs") + stats.get("inProgressRepairs"));

            // ===== GĐ6→7: Hoàn tất & Đóng lệnh =====
            stats.put("completedRepairs", dashboardService.countCompletedRepairs());
            stats.put("workOrdersReadyForClosure", dashboardService.countWorkOrdersReadyForClosure());
            stats.put("closedWorkOrders", dashboardService.countClosedWorkOrders());
            stats.put("totalWorkOrders", dashboardService.countTotalWorkOrders());

            // ===== Hoạt động Gần đây =====
            stats.put("todayRequests", dashboardService.countTodayRequests());
            stats.put("thisWeekCompleted", dashboardService.countThisWeekCompleted());

            // ===== Cảnh báo Quản lý (Xử lý Ngoại lệ) =====
            stats.put("declinedTasksCount", declinedTaskService.countDeclinedTasks());
            stats.put("overdueTasks", techManagerService.countOverdueTasks());
            stats.put("declinedTasks", techManagerService.countDeclinedTasks());
            stats.put("tasksNeedReassignment", techManagerService.countTasksNeedReassignment());

            // ===== NEW: Activity Logs & Diagnostic Details =====
            List<ActivityLogDTO> recentActivities = dashboardService.getRecentActivities();
            List<DiagnosticApprovalDTO> pendingDiagnostics = dashboardService.getPendingDiagnosticApprovals();

            request.setAttribute("stats", stats);
            request.setAttribute("recentActivities", recentActivities);
            request.setAttribute("pendingDiagnostics", pendingDiagnostics);

            request.getRequestDispatcher("/view/techmanager/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load dashboard statistics: " + e.getMessage());
        }
    }
}
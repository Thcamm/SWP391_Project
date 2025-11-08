package controller.employee.techmanager;

import dao.workorder.RejectedTaskDAO;
import service.employee.techmanager.DashboardService;
import service.employee.techmanager.TechManagerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet controller for the Technical Manager Dashboard.
 * This servlet gathers and displays key performance indicators (KPIs) and
 * statistics related to the 6-phase garage workflow.
 *
 * <p><b>NEW WORKFLOW (6 Phases):</b></p>
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

    // Tối ưu: Khởi tạo service một lần thay vì mỗi lần request
    private final DashboardService dashboardService = new DashboardService();
    private final TechManagerService techManagerService = new TechManagerService();
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

        try {
            Map<String, Integer> stats = new HashMap<>();

            // ===== GĐ 1: Tiếp nhận & Phân công Chẩn đoán =====
            stats.put("pendingRequests", dashboardService.countPendingServiceRequests());
            stats.put("assignedDiagnosis", dashboardService.countAssignedDiagnosis());

            // ===== GĐ 2: KTV Chẩn đoán & Báo giá =====
            stats.put("inProgressDiagnosis", dashboardService.countInProgressDiagnosis());
            stats.put("pendingQuotes", dashboardService.countPendingCustomerApproval()); // Báo giá (VD) chờ khách duyệt

            // ===== GĐ 3 & 4: Khách duyệt + Cầu nối Tự động =====
            // (unassignedWorkOrderDetails là WorkOrderDetail được tạo bởi trigger nhưng chưa có TaskAssignment)
            stats.put("unassignedWorkOrderDetails", dashboardService.countUnassignedWorkOrderDetails());

            // ===== GĐ 5: Phân công Sửa chữa =====
            stats.put("assignedRepairs", dashboardService.countAssignedRepairs());
            stats.put("inProgressRepairs", dashboardService.countInProgressRepairs());
            
            // (Số liệu gộp)
            stats.put("activeRepairs", stats.get("assignedRepairs") + stats.get("inProgressRepairs"));

            // ===== GĐ 6 & 7: Hoàn tất & Đóng lệnh =====
            stats.put("completedRepairs", dashboardService.countCompletedRepairs()); // KTV đã làm xong
            stats.put("workOrdersReadyForClosure", dashboardService.countWorkOrdersReadyForClosure()); // TM cần đóng
            stats.put("closedWorkOrders", dashboardService.countClosedWorkOrders());
            stats.put("totalWorkOrders", dashboardService.countTotalWorkOrders());

            // ===== Hoạt động Gần đây =====
            stats.put("todayRequests", dashboardService.countTodayRequests());
            stats.put("thisWeekCompleted", dashboardService.countThisWeekCompleted());

            // ===== Cảnh báo Quản lý (Xử lý Ngoại lệ) =====
            stats.put("rejectedTasks", rejectedTaskDAO.countRejectedTasks()); // GĐ 6: Khách từ chối Báo giá
            stats.put("overdueTasks", techManagerService.countOverdueTasks()); // GĐ 6: Task trễ SLA
            stats.put("declinedTasks", techManagerService.countDeclinedTasks()); // GĐ 6: KTV từ chối Task
            
            // GĐ 7: Tổng số task cần gán lại
            stats.put("tasksNeedReassignment", techManagerService.countTasksNeedReassignment()); 

            request.setAttribute("stats", stats);
            request.getRequestDispatcher("/view/techmanager/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load dashboard statistics: " + e.getMessage());
        }
    }
}
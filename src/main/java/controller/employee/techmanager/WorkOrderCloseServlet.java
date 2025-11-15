package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.employee.techmanager.WorkOrderCloseDTO;
import service.employee.techmanager.WorkOrderCloseService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

/**
 * [REFACTORED] Thin Controller for Work Order Closure (GĐ7)
 * (Lọc theo TM và lấy thêm thống kê)
 *
 * @author SWP391 Team
 * @version 2.1 (Fixed)
 */
@WebServlet("/techmanager/close-workorders")
public class WorkOrderCloseServlet extends HttpServlet {

    private final WorkOrderCloseService workOrderCloseService;

    // Constructor
    public WorkOrderCloseServlet() {
        this.workOrderCloseService = new WorkOrderCloseService();
    }

    // Constructor for testing (Giữ nguyên)
    public WorkOrderCloseServlet(WorkOrderCloseService workOrderCloseService) {
        this.workOrderCloseService = workOrderCloseService;
    }

    /**
     * [FIXED] GET: Display list of work orders ready for closure
     * (Now filters by the logged-in Tech Manager and gets new stats)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        try {
            // --- BẮT ĐẦU SỬA LỖI ---
            if (session == null || session.getAttribute("userName") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            String userName = (String) session.getAttribute("userName");

            // 1. Lấy TechManager ID (Bắt buộc để lọc)
            Integer techManagerId = workOrderCloseService.getTechManagerEmployeeId(userName);

            if (techManagerId == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Access Denied: User is not a valid Tech Manager.");
                return;
            }
            // --- KẾT THÚC SỬA LỖI ---

            // 2. Lấy danh sách WorkOrder (Đã lọc theo ID)
            List<WorkOrderCloseDTO> readyToClose = workOrderCloseService.getWorkOrdersReadyForClosure(techManagerId);

            // 3. (MỚI) Lấy 2 thống kê mới
            int totalClosedToday = workOrderCloseService.countWorkOrdersClosedToday(techManagerId);
            int totalClosedMonth = workOrderCloseService.countWorkOrdersClosedThisMonth(techManagerId);

            // 4. Set attributes cho JSP
            request.setAttribute("workOrders", readyToClose);
            request.setAttribute("totalReady", readyToClose.size());
            request.setAttribute("totalClosedToday", totalClosedToday); // MỚI
            request.setAttribute("totalClosedMonth", totalClosedMonth); // MỚI

            // 5. Forward to JSP
            request.getRequestDispatcher("/view/techmanager/close-workorders.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load work orders: " + e.getMessage());
        }
    }

    /**
     * [FIXED] POST: Close a work order
     * (Now passes techManagerId to service layer)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userName") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String userName = (String) session.getAttribute("userName");

        if ("close".equals(action)) {
            // Truyền 'userName' vào handleClose
            handleClose(request, response, userName);
        } else {
            response.sendRedirect(request.getContextPath() + "/techmanager/close-workorders");
        }
    }

    /**
     * [FIXED] Handle closing a WorkOrder
     * (Now passes techManagerId to service layer for validation/logging)
     */
    private void handleClose(HttpServletRequest request, HttpServletResponse response, String userName)
            throws IOException, ServletException {

        try {
            int workOrderID = Integer.parseInt(request.getParameter("workOrderID"));

            // 1. Lấy TechManager ID
            Integer techManagerId = workOrderCloseService.getTechManagerEmployeeId(userName);
            if (techManagerId == null) {
                redirectWithMessage(request, response, "TechManager employee record not found", "error");
                return;
            }

            // 2. Gọi Service (Truyền cả workOrderID và techManagerId)
            boolean success = workOrderCloseService.closeWorkOrder(workOrderID, techManagerId);

            if (success) {
                redirectWithMessage(request, response, "Work Order #" + workOrderID + " closed successfully",
                        "success");
            } else {
                redirectWithMessage(request, response,
                        "Failed to close Work Order. (Already closed or tasks not complete)", "danger");
            }

        } catch (NumberFormatException e) {
            redirectWithMessage(request, response, "Invalid Work Order ID", "danger");
        } catch (IllegalStateException e) {
            // Lỗi nghiệp vụ (ví dụ: "Tasks not complete")
            redirectWithMessage(request, response, e.getMessage(), "warning");
        } catch (SQLException e) {
            e.printStackTrace();
            redirectWithMessage(request, response, "Database error: " + e.getMessage(), "danger");
        }
    }

    /**
     * Helper method to redirect with message
     */
    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response,
            String message, String type) throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() +
                "/techmanager/close-workorders?message=" + encodedMessage +
                "&type=" + type);
    }
}
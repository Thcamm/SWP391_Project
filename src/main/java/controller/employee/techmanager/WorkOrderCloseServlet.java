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
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet for closing WorkOrders (GĐ7 - Final Phase).
 * TechManager reviews completed work orders and closes them.
 * 
 * REFACTORED: Now uses 3-tier architecture (Servlet → Service → DAO)
 * 
 * @author SWP391 Team
 * @version 2.0
 */
@WebServlet("/techmanager/close-workorders")
public class WorkOrderCloseServlet extends HttpServlet {

    private final WorkOrderCloseService workOrderCloseService;

    // Constructor
    public WorkOrderCloseServlet() {
        this.workOrderCloseService = new WorkOrderCloseService();
    }

    // Constructor for dependency injection (testing)
    public WorkOrderCloseServlet(WorkOrderCloseService workOrderCloseService) {
        this.workOrderCloseService = workOrderCloseService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<WorkOrderCloseDTO> readyToClose = workOrderCloseService.getWorkOrdersReadyForClosure();
            request.setAttribute("workOrders", readyToClose);
            request.setAttribute("totalReady", readyToClose.size());

            request.getRequestDispatcher("/view/techmanager/close-workorders.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load work orders: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("close".equals(action)) {
            handleClose(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/techmanager/close-workorders");
        }
    }

    /**
     * Handle closing a WorkOrder.
     */
    private void handleClose(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            int workOrderID = Integer.parseInt(request.getParameter("workOrderID"));

            // Verify session user is TechManager
            HttpSession session = request.getSession();
            String userName = (String) session.getAttribute("userName");
            if (userName == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Close the work order using service layer
            boolean success = workOrderCloseService.closeWorkOrder(workOrderID);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/close-workorders?message=Work Order #" + workOrderID +
                        " closed successfully&type=success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/close-workorders?message=Failed to close Work Order. " +
                        "Please verify all tasks are complete.&type=danger");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/close-workorders?message=Invalid Work Order ID&type=danger");
        } catch (IllegalStateException e) {
            // Business logic error from service layer
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/close-workorders?message=" + e.getMessage() + "&type=danger");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/close-workorders?message=Database error: " +
                    e.getMessage() + "&type=danger");
        }
    }
}

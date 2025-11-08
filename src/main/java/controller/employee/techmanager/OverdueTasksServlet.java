package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.employee.techmanager.OverdueTaskDTO;
import service.employee.techmanager.OverdueTaskService;

import java.io.IOException;
import java.util.List;

/**
 * Overdue Tasks Servlet - SLA Violation Monitoring
 * Displays tasks that violated SLA (past planned_start time without starting)
 * Allows TechManager to cancel overdue tasks (→ moves to reassignment list)
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored to 3-tier architecture)
 */
@WebServlet("/techmanager/overdue-tasks")
public class OverdueTasksServlet extends HttpServlet {

    private OverdueTaskService overdueTaskService;

    @Override
    public void init() throws ServletException {
        this.overdueTaskService = new OverdueTaskService();
    }

    /**
     * GET: Display list of overdue tasks
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get overdue tasks
            List<OverdueTaskDTO> overdueTasks = overdueTaskService.getOverdueTasks();

            request.setAttribute("overdueTasks", overdueTasks);
            request.setAttribute("totalOverdue", overdueTasks.size());

            // Handle messages
            String message = request.getParameter("message");
            String type = request.getParameter("type");
            if (message != null) {
                request.setAttribute("message", message);
                request.setAttribute("messageType", type != null ? type : "info");
            }

            request.getRequestDispatcher("/view/techmanager/overdue-tasks.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading overdue tasks: " + e.getMessage());
        }
    }

    /**
     * POST: Cancel an overdue task (→ moves to reassignment list)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String assignmentIdStr = request.getParameter("assignmentId");

        if (assignmentIdStr == null) {
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/overdue-tasks?message=Missing assignment ID&type=error");
            return;
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);

            // Cancel task via service
            String resultMessage = overdueTaskService.cancelOverdueTask(assignmentId);

            if (resultMessage.contains("successfully")) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/overdue-tasks?message=" + resultMessage + "&type=success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/overdue-tasks?message=" + resultMessage + "&type=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/overdue-tasks?message=Error: " + e.getMessage() + "&type=error");
        }
    }
}

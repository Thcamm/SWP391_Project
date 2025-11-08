package controller.employee.techmanager;

import model.employee.techmanager.RejectedTaskDTO;
import service.employee.techmanager.RejectedTaskService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for displaying rejected tasks that need reassignment.
 * Read-only view for monitoring workflow health.
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored to 3-tier architecture)
 */
@WebServlet("/techmanager/rejected-tasks")
public class RejectedTasksServlet extends HttpServlet {

    private RejectedTaskService rejectedTaskService;

    @Override
    public void init() throws ServletException {
        this.rejectedTaskService = new RejectedTaskService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get rejected tasks from service
            List<RejectedTaskDTO> rejectedTasks = rejectedTaskService.getRejectedTasks();

            // Set attribute and forward to JSP
            request.setAttribute("rejectedTasks", rejectedTasks);
            request.getRequestDispatcher("/view/techmanager/rejected-tasks.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading rejected tasks: " + e.getMessage());
        }
    }
}

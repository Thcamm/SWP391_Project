package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.employee.techmanager.DeclinedTaskDTO;
import service.employee.techmanager.DeclinedTaskService;

import java.io.IOException;
import java.util.List;

/**
 * Declined Tasks Servlet - Technician Rejection Monitoring
 * Displays tasks declined by technicians (proactive cancellation)
 * Read-only view for monitoring workflow health
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored to 3-tier architecture)
 */
@WebServlet("/techmanager/declined-tasks")
public class DeclinedTasksServlet extends HttpServlet {

    private DeclinedTaskService declinedTaskService;

    @Override
    public void init() throws ServletException {
        this.declinedTaskService = new DeclinedTaskService();
    }

    /**
     * GET: Display list of declined tasks
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get declined tasks
            List<DeclinedTaskDTO> declinedTasks = declinedTaskService.getDeclinedTasks();

            request.setAttribute("declinedTasks", declinedTasks);
            request.setAttribute("totalDeclined", declinedTasks.size());

            // Handle messages
            String message = request.getParameter("message");
            String type = request.getParameter("type");
            if (message != null) {
                request.setAttribute("message", message);
                request.setAttribute("messageType", type != null ? type : "info");
            }

            request.getRequestDispatcher("/view/techmanager/declined-tasks.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading declined tasks: " + e.getMessage());
        }
    }
}

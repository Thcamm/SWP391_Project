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

        System.out.println("[DeclinedTasksServlet] ===== START doGet() =====");
        
        try {
            // Get declined tasks
            System.out.println("[DeclinedTasksServlet] Calling declinedTaskService.getDeclinedTasks()...");
            List<DeclinedTaskDTO> declinedTasks = declinedTaskService.getDeclinedTasks();
            
            System.out.println("[DeclinedTasksServlet] Retrieved " + declinedTasks.size() + " declined tasks");
            
            // Log each declined task
            if (!declinedTasks.isEmpty()) {
                for (int i = 0; i < declinedTasks.size(); i++) {
                    DeclinedTaskDTO task = declinedTasks.get(i);
                    System.out.println("[DeclinedTasksServlet] Task " + (i+1) + ": " +
                            "AssignmentID=" + task.getAssignmentId() +
                            ", Technician=" + task.getTechnicianName() +
                            ", Reason=" + task.getDeclineReason());
                }
            } else {
                System.out.println("[DeclinedTasksServlet] WARNING: No declined tasks found in database!");
            }

            request.setAttribute("declinedTasks", declinedTasks);
            request.setAttribute("totalDeclined", declinedTasks.size());

            // Handle messages
            String message = request.getParameter("message");
            String type = request.getParameter("type");
            if (message != null) {
                request.setAttribute("message", message);
                request.setAttribute("messageType", type != null ? type : "info");
            }

            System.out.println("[DeclinedTasksServlet] Forwarding to JSP...");
            request.getRequestDispatcher("/view/techmanager/declined-tasks.jsp").forward(request, response);
            System.out.println("[DeclinedTasksServlet] ===== END doGet() =====");

        } catch (Exception e) {
            System.err.println("[DeclinedTasksServlet] ERROR: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading declined tasks: " + e.getMessage());
        }
    }
}

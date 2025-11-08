package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.employee.Employee;
import model.employee.techmanager.TaskReassignmentDTO;
import service.employee.techmanager.TaskReassignmentService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tasks Need Reassignment - Unified Workflow
 * Displays ALL cancelled tasks that need reassignment:
 * 1. Overdue tasks (cancelled by TM due to SLA violation)
 * 2. Declined tasks (cancelled by technician proactively)
 * 
 * Allows TechManager to reassign to different technician with new scheduling
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored to 3-tier architecture)
 */
@WebServlet("/techmanager/reassign-tasks")
public class TasksNeedReassignmentServlet extends HttpServlet {

    private TaskReassignmentService taskReassignmentService;

    @Override
    public void init() throws ServletException {
        this.taskReassignmentService = new TaskReassignmentService();
    }

    /**
     * GET: Display list of tasks needing reassignment + available technicians
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get tasks needing reassignment
            List<TaskReassignmentDTO> cancelledTasks = taskReassignmentService.getCancelledTasks();

            // Get available technicians
            List<Employee> technicians = taskReassignmentService.getAvailableTechnicians();

            request.setAttribute("cancelledTasks", cancelledTasks);
            request.setAttribute("technicians", technicians);
            request.setAttribute("totalCancelled", cancelledTasks.size());

            // Handle messages
            String message = request.getParameter("message");
            String type = request.getParameter("type");
            if (message != null) {
                request.setAttribute("message", message);
                request.setAttribute("messageType", type != null ? type : "info");
            }

            request.getRequestDispatcher("/view/techmanager/reassign-tasks.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading tasks for reassignment: " + e.getMessage());
        }
    }

    /**
     * POST: Reassign cancelled task to new technician with new scheduling
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String assignmentIdStr = request.getParameter("assignmentId");
        String newTechnicianIdStr = request.getParameter("newTechnicianId");
        String plannedStartStr = request.getParameter("plannedStart");
        String plannedEndStr = request.getParameter("plannedEnd");

        if (assignmentIdStr == null || newTechnicianIdStr == null) {
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/reassign-tasks?message=Missing required fields&type=error");
            return;
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);
            int newTechnicianId = Integer.parseInt(newTechnicianIdStr);

            // Parse new scheduling times
            LocalDateTime plannedStart = null;
            LocalDateTime plannedEnd = null;

            if (plannedStartStr != null && !plannedStartStr.trim().isEmpty()) {
                plannedStart = LocalDateTime.parse(plannedStartStr);
            }

            if (plannedEndStr != null && !plannedEndStr.trim().isEmpty()) {
                plannedEnd = LocalDateTime.parse(plannedEndStr);
            }

            // Perform reassignment via service
            String resultMessage = taskReassignmentService.reassignTask(
                    assignmentId, newTechnicianId, plannedStart, plannedEnd);

            if (resultMessage.contains("successfully")) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/reassign-tasks?message=" + resultMessage + "&type=success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/reassign-tasks?message=" + resultMessage + "&type=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/reassign-tasks?message=Error: " + e.getMessage() + "&type=error");
        }
    }
}

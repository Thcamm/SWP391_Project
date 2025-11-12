package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import model.employee.techmanager.ScheduledTaskDTO;
import service.employee.techmanager.TechnicianScheduleService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * AJAX Endpoint: View Technician's Daily Schedule
 * Used by assignment forms to check technician availability before assigning
 * tasks
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored to 3-tier architecture)
 */
@WebServlet("/techmanager/technician-schedule")
public class TechnicianScheduleServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private TechnicianScheduleService technicianScheduleService;

    @Override
    public void init() throws ServletException {
        this.technicianScheduleService = new TechnicianScheduleService();
    }

    /**
     * GET: Retrieve technician's schedule for a specific date
     * Parameters:
     * - technicianId (required): Employee ID of technician
     * - date (optional): Date in format yyyy-MM-dd (default: today)
     * 
     * Returns JSON array of scheduled tasks
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String technicianIdStr = request.getParameter("technicianId");
        String dateStr = request.getParameter("date");

        // Validate parameters
        if (technicianIdStr == null || technicianIdStr.trim().isEmpty()) {
            sendError(response, "Missing required parameter: technicianId");
            return;
        }

        try {
            int technicianId = Integer.parseInt(technicianIdStr);
            LocalDate targetDate = (dateStr != null && !dateStr.trim().isEmpty())
                    ? LocalDate.parse(dateStr, DATE_FORMATTER)
                    : LocalDate.now();

            // Query scheduled tasks via service
            List<ScheduledTaskDTO> scheduledTasks = technicianScheduleService.getScheduledTasks(technicianId,
                    targetDate);

            // Convert to JSON response
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("technicianId", technicianId);
            jsonResponse.addProperty("date", targetDate.format(DATE_FORMATTER));
            jsonResponse.addProperty("totalTasks", scheduledTasks.size());

            JsonArray tasksArray = new JsonArray();
            for (ScheduledTaskDTO task : scheduledTasks) {
                JsonObject taskJson = new JsonObject();
                taskJson.addProperty("assignmentId", task.getAssignmentId());
                taskJson.addProperty("taskType", task.getTaskType());
                taskJson.addProperty("status", task.getStatus());
                taskJson.addProperty("plannedStart", task.getPlannedStart());
                taskJson.addProperty("plannedEnd", task.getPlannedEnd());
                taskJson.addProperty("taskDescription", task.getTaskDescription());
                taskJson.addProperty("vehicleInfo", task.getVehicleInfo());
                taskJson.addProperty("isOverdue", task.isOverdue());
                tasksArray.add(taskJson);
            }
            jsonResponse.add("tasks", tasksArray);

            response.getWriter().write(gson.toJson(jsonResponse));

        } catch (NumberFormatException e) {
            sendError(response, "Invalid technicianId format");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error loading schedule: " + e.getMessage());
        }
    }

    /**
     * Send JSON error response
     */
    private void sendError(HttpServletResponse response, String message) throws IOException {
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("success", false);
        errorJson.addProperty("error", message);
        response.getWriter().write(gson.toJson(errorJson));
    }
}

package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import common.DbContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * AJAX Endpoint: View Technician's Daily Schedule
 * Used by assignment forms to check technician availability before assigning
 * tasks
 */
@WebServlet("/techmanager/technician-schedule")
public class TechnicianScheduleServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM HH:mm");

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

            // Query scheduled tasks for this technician on target date
            List<ScheduledTaskDTO> scheduledTasks = getScheduledTasks(technicianId, targetDate);

            // Convert to JSON response
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("technicianId", technicianId);
            jsonResponse.addProperty("date", targetDate.format(DATE_FORMATTER));
            jsonResponse.addProperty("totalTasks", scheduledTasks.size());

            JsonArray tasksArray = new JsonArray();
            for (ScheduledTaskDTO task : scheduledTasks) {
                JsonObject taskJson = new JsonObject();
                taskJson.addProperty("assignmentId", task.assignmentId);
                taskJson.addProperty("taskType", task.taskType);
                taskJson.addProperty("status", task.status);
                taskJson.addProperty("plannedStart", task.plannedStart);
                taskJson.addProperty("plannedEnd", task.plannedEnd);
                taskJson.addProperty("taskDescription", task.taskDescription);
                taskJson.addProperty("vehicleInfo", task.vehicleInfo);
                taskJson.addProperty("isOverdue", task.isOverdue);
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
     * Query database for scheduled tasks
     */
    private List<ScheduledTaskDTO> getScheduledTasks(int technicianId, LocalDate targetDate) throws Exception {
        List<ScheduledTaskDTO> tasks = new ArrayList<>();

        String sql = "SELECT ta.AssignmentID, ta.task_type, ta.Status, " +
                "ta.planned_start, ta.planned_end, ta.TaskDescription, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS vehicle_info " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "WHERE ta.AssignToTechID = ? " +
                "AND ta.planned_start IS NOT NULL " +
                "AND DATE(ta.planned_start) = ? " +
                "AND ta.Status IN ('ASSIGNED', 'IN_PROGRESS') " +
                "ORDER BY ta.planned_start";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, technicianId);
            ps.setDate(2, java.sql.Date.valueOf(targetDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ScheduledTaskDTO task = new ScheduledTaskDTO();
                    task.assignmentId = rs.getInt("AssignmentID");
                    task.taskType = rs.getString("task_type");
                    task.status = rs.getString("Status");

                    Timestamp plannedStartTs = rs.getTimestamp("planned_start");
                    Timestamp plannedEndTs = rs.getTimestamp("planned_end");

                    if (plannedStartTs != null) {
                        LocalDateTime startDt = plannedStartTs.toLocalDateTime();
                        task.plannedStart = startDt.format(DATETIME_FORMATTER);

                        // Check if overdue
                        task.isOverdue = "ASSIGNED".equals(task.status) && LocalDateTime.now().isAfter(startDt);
                    }

                    if (plannedEndTs != null) {
                        task.plannedEnd = plannedEndTs.toLocalDateTime().format(DATETIME_FORMATTER);
                    }

                    task.taskDescription = rs.getString("TaskDescription");
                    task.vehicleInfo = rs.getString("vehicle_info");

                    tasks.add(task);
                }
            }
        }

        return tasks;
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

    /**
     * DTO for scheduled task response
     */
    private static class ScheduledTaskDTO {
        int assignmentId;
        String taskType;
        String status;
        String plannedStart;
        String plannedEnd;
        String taskDescription;
        String vehicleInfo;
        boolean isOverdue;
    }
}

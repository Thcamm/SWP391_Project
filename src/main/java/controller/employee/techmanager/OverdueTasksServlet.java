package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import common.DbContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Overdue Tasks Monitoring - SLA Violation Detection
 * Displays tasks that violated SLA (past planned_start time but not started
 * yet)
 * TechManager can cancel these tasks to trigger reassignment workflow
 */
@WebServlet("/techmanager/overdue-tasks")
public class OverdueTasksServlet extends HttpServlet {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * GET: Display list of overdue tasks
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<OverdueTaskDTO> overdueTasks = getOverdueTasks();

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
     * POST: Cancel overdue task (triggers reassignment workflow)
     * Action: Sets Status = 'CANCELLED'
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String assignmentIdStr = request.getParameter("assignmentId");

        if (!"cancel".equals(action) || assignmentIdStr == null) {
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/overdue-tasks?message=Invalid request&type=error");
            return;
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);

            boolean success = cancelTask(assignmentId);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/overdue-tasks?message=Task cancelled successfully. It will appear in reassignment list.&type=success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/overdue-tasks?message=Failed to cancel task&type=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/overdue-tasks?message=Error: " + e.getMessage() + "&type=error");
        }
    }

    /**
     * Query overdue tasks from database
     * Criteria: Status='ASSIGNED', StartAt IS NULL, planned_start < NOW()
     */
    private List<OverdueTaskDTO> getOverdueTasks() throws Exception {
        List<OverdueTaskDTO> tasks = new ArrayList<>();

        String sql = "SELECT ta.AssignmentID, ta.task_type, ta.planned_start, ta.planned_end, " +
                "ta.TaskDescription, ta.AssignedDate, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS vehicle_info, " +
                "CONCAT(e.FirstName, ' ', e.LastName) AS technician_name, " +
                "u.FullName AS customer_name, " +
                "TIMESTAMPDIFF(HOUR, ta.planned_start, NOW()) AS hours_overdue " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN User u ON sr.CustomerID = u.UserID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "WHERE ta.Status = 'ASSIGNED' " +
                "AND ta.StartAt IS NULL " +
                "AND ta.planned_start IS NOT NULL " +
                "AND ta.planned_start < NOW() " +
                "ORDER BY ta.planned_start ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                OverdueTaskDTO task = new OverdueTaskDTO();
                task.assignmentId = rs.getInt("AssignmentID");
                task.taskType = rs.getString("task_type");

                Timestamp plannedStartTs = rs.getTimestamp("planned_start");
                if (plannedStartTs != null) {
                    task.plannedStart = plannedStartTs.toLocalDateTime().format(DATETIME_FORMATTER);
                }

                Timestamp plannedEndTs = rs.getTimestamp("planned_end");
                if (plannedEndTs != null) {
                    task.plannedEnd = plannedEndTs.toLocalDateTime().format(DATETIME_FORMATTER);
                }

                Timestamp assignedDateTs = rs.getTimestamp("AssignedDate");
                if (assignedDateTs != null) {
                    task.assignedDate = assignedDateTs.toLocalDateTime().format(DATETIME_FORMATTER);
                }

                task.taskDescription = rs.getString("TaskDescription");
                task.vehicleInfo = rs.getString("vehicle_info");
                task.technicianName = rs.getString("technician_name");
                task.customerName = rs.getString("customer_name");
                task.hoursOverdue = rs.getInt("hours_overdue");

                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Cancel a task by setting Status = 'CANCELLED'
     * After cancellation, task will appear in reassignment list
     */
    private boolean cancelTask(int assignmentId) throws Exception {
        String sql = "UPDATE TaskAssignment SET Status = 'CANCELLED' WHERE AssignmentID = ?";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * DTO for overdue task display
     */
    public static class OverdueTaskDTO {
        private int assignmentId;
        private String taskType;
        private String plannedStart;
        private String plannedEnd;
        private String assignedDate;
        private String taskDescription;
        private String vehicleInfo;
        private String technicianName;
        private String customerName;
        private int hoursOverdue;

        // Getters
        public int getAssignmentId() {
            return assignmentId;
        }

        public String getTaskType() {
            return taskType;
        }

        public String getPlannedStart() {
            return plannedStart;
        }

        public String getPlannedEnd() {
            return plannedEnd;
        }

        public String getAssignedDate() {
            return assignedDate;
        }

        public String getTaskDescription() {
            return taskDescription;
        }

        public String getVehicleInfo() {
            return vehicleInfo;
        }

        public String getTechnicianName() {
            return technicianName;
        }

        public String getCustomerName() {
            return customerName;
        }

        public int getHoursOverdue() {
            return hoursOverdue;
        }
    }
}

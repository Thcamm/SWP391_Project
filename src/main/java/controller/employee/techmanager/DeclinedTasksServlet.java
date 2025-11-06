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
 * Declined Tasks Monitoring
 * Displays tasks that were declined by technicians with decline reasons
 * Provides visibility into why technicians declined tasks
 */
@WebServlet("/techmanager/declined-tasks")
public class DeclinedTasksServlet extends HttpServlet {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * GET: Display list of declined tasks
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<DeclinedTaskDTO> declinedTasks = getDeclinedTasks();

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

    /**
     * Query declined tasks from database
     * Criteria: declined_at IS NOT NULL, Status='CANCELLED'
     */
    private List<DeclinedTaskDTO> getDeclinedTasks() throws Exception {
        List<DeclinedTaskDTO> tasks = new ArrayList<>();

        String sql = "SELECT ta.AssignmentID, ta.task_type, ta.planned_start, ta.planned_end, " +
                "ta.TaskDescription, ta.AssignedDate, ta.declined_at, ta.decline_reason, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS vehicle_info, " +
                "CONCAT(e.FirstName, ' ', e.LastName) AS technician_name, " +
                "u.FullName AS customer_name " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN User u ON sr.CustomerID = u.UserID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "WHERE ta.declined_at IS NOT NULL " +
                "AND ta.Status = 'CANCELLED' " +
                "ORDER BY ta.declined_at DESC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DeclinedTaskDTO task = new DeclinedTaskDTO();
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

                Timestamp declinedAtTs = rs.getTimestamp("declined_at");
                if (declinedAtTs != null) {
                    task.declinedAt = declinedAtTs.toLocalDateTime().format(DATETIME_FORMATTER);
                }

                task.declineReason = rs.getString("decline_reason");
                task.taskDescription = rs.getString("TaskDescription");
                task.vehicleInfo = rs.getString("vehicle_info");
                task.technicianName = rs.getString("technician_name");
                task.customerName = rs.getString("customer_name");

                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * DTO for declined task display
     */
    public static class DeclinedTaskDTO {
        private int assignmentId;
        private String taskType;
        private String plannedStart;
        private String plannedEnd;
        private String assignedDate;
        private String declinedAt;
        private String declineReason;
        private String taskDescription;
        private String vehicleInfo;
        private String technicianName;
        private String customerName;

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

        public String getDeclinedAt() {
            return declinedAt;
        }

        public String getDeclineReason() {
            return declineReason;
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
    }
}

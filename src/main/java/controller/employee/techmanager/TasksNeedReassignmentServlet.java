package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import common.DbContext;
import dao.employee.technician.TechnicianDAO;
import model.employee.Employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Tasks Need Reassignment - Unified Workflow
 * Displays ALL cancelled tasks that need reassignment:
 * 1. Overdue tasks (cancelled by TM due to SLA violation)
 * 2. Declined tasks (cancelled by technician proactively)
 * 
 * Allows TechManager to reassign to different technician with new scheduling
 */
@WebServlet("/techmanager/reassign-tasks")
public class TasksNeedReassignmentServlet extends HttpServlet {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private TechnicianDAO technicianDAO;

    @Override
    public void init() throws ServletException {
        this.technicianDAO = new TechnicianDAO();
    }

    /**
     * GET: Display list of tasks needing reassignment + available technicians
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get tasks needing reassignment
            List<CancelledTaskDTO> cancelledTasks = getCancelledTasks();

            // Get available technicians
            List<Employee> technicians = technicianDAO.getAllTechnicians();

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

            // Validate: planned_end must be after planned_start
            if (plannedStart != null && plannedEnd != null) {
                if (!plannedEnd.isAfter(plannedStart)) {
                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/reassign-tasks?message=Planned end time must be after planned start time&type=error");
                    return;
                }
            }

            // Reassign task
            boolean success = reassignTask(assignmentId, newTechnicianId, plannedStart, plannedEnd);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/reassign-tasks?message=Task reassigned successfully&type=success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/reassign-tasks?message=Failed to reassign task&type=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/reassign-tasks?message=Error: " + e.getMessage() + "&type=error");
        }
    }

    /**
     * Query all cancelled tasks from database
     * Includes both overdue tasks and declined tasks
     */
    private List<CancelledTaskDTO> getCancelledTasks() throws Exception {
        List<CancelledTaskDTO> tasks = new ArrayList<>();

        String sql = "SELECT ta.AssignmentID, ta.task_type, ta.planned_start, ta.planned_end, " +
                "ta.TaskDescription, ta.AssignedDate, ta.declined_at, ta.decline_reason, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS vehicle_info, " +
                "CONCAT(e.FirstName, ' ', e.LastName) AS technician_name, " +
                "u.FullName AS customer_name, " +
                "CASE " +
                "  WHEN ta.declined_at IS NOT NULL THEN 'DECLINED' " +
                "  WHEN ta.planned_start < NOW() THEN 'OVERDUE' " +
                "  ELSE 'OTHER' " +
                "END AS cancel_reason_type " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN User u ON sr.CustomerID = u.UserID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "WHERE ta.Status = 'CANCELLED' " +
                "AND (ta.declined_at IS NOT NULL OR " +
                "     (ta.planned_start IS NOT NULL AND ta.planned_start < NOW())) " +
                "ORDER BY ta.AssignedDate DESC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CancelledTaskDTO task = new CancelledTaskDTO();
                task.assignmentId = rs.getInt("AssignmentID");
                task.taskType = rs.getString("task_type");

                Timestamp plannedStartTs = rs.getTimestamp("planned_start");
                if (plannedStartTs != null) {
                    task.plannedStart = plannedStartTs.toLocalDateTime().format(DATETIME_FORMATTER);
                    task.plannedStartRaw = plannedStartTs.toLocalDateTime(); // For form default
                }

                Timestamp plannedEndTs = rs.getTimestamp("planned_end");
                if (plannedEndTs != null) {
                    task.plannedEnd = plannedEndTs.toLocalDateTime().format(DATETIME_FORMATTER);
                    task.plannedEndRaw = plannedEndTs.toLocalDateTime(); // For form default
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
                task.cancelReasonType = rs.getString("cancel_reason_type");

                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Reassign task to new technician with new scheduling
     * Updates: AssignToTechID, Status='ASSIGNED', planned_start, planned_end
     * Clears: declined_at, decline_reason
     */
    private boolean reassignTask(int assignmentId, int newTechnicianId,
            LocalDateTime plannedStart, LocalDateTime plannedEnd) throws Exception {

        String sql = "UPDATE TaskAssignment SET " +
                "AssignToTechID = ?, " +
                "Status = 'ASSIGNED', " +
                "planned_start = ?, " +
                "planned_end = ?, " +
                "declined_at = NULL, " +
                "decline_reason = NULL, " +
                "AssignedDate = NOW() " +
                "WHERE AssignmentID = ?";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newTechnicianId);

            if (plannedStart != null) {
                ps.setTimestamp(2, Timestamp.valueOf(plannedStart));
            } else {
                ps.setNull(2, java.sql.Types.TIMESTAMP);
            }

            if (plannedEnd != null) {
                ps.setTimestamp(3, Timestamp.valueOf(plannedEnd));
            } else {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            }

            ps.setInt(4, assignmentId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * DTO for cancelled task display
     */
    public static class CancelledTaskDTO {
        private int assignmentId;
        private String taskType;
        private String plannedStart;
        private String plannedEnd;
        private LocalDateTime plannedStartRaw; // For form default value
        private LocalDateTime plannedEndRaw; // For form default value
        private String assignedDate;
        private String declinedAt;
        private String declineReason;
        private String taskDescription;
        private String vehicleInfo;
        private String technicianName;
        private String customerName;
        private String cancelReasonType; // 'DECLINED' or 'OVERDUE'

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

        public LocalDateTime getPlannedStartRaw() {
            return plannedStartRaw;
        }

        public LocalDateTime getPlannedEndRaw() {
            return plannedEndRaw;
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

        public String getCancelReasonType() {
            return cancelReasonType;
        }
    }
}

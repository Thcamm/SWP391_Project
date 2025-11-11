package dao.employee.techmanager;

import common.DbContext;
import model.employee.techmanager.TaskReassignmentDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Task Reassignment operations.
 * Handles database queries for cancelled tasks that need reassignment.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class TaskReassignmentDAO {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Get all cancelled tasks that need reassignment.
     * Includes both overdue tasks and declined tasks.
     * 
     * @return list of cancelled tasks
     * @throws SQLException if database error occurs
     */
    public List<TaskReassignmentDTO> getCancelledTasks() throws SQLException {
        List<TaskReassignmentDTO> tasks = new ArrayList<>();

        String sql = "SELECT ta.AssignmentID, ta.task_type, ta.planned_start, ta.planned_end, " +
                "ta.TaskDescription, ta.AssignedDate, ta.declined_at, ta.decline_reason, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS vehicle_info, " +
                "tech_user.FullName AS technician_name, " +
                "cust_user.FullName AS customer_name, " +
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
                "JOIN User cust_user ON sr.CustomerID = cust_user.UserID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User tech_user ON e.UserID = tech_user.UserID " +
                "WHERE ta.Status = 'CANCELLED' " +
                "AND (ta.declined_at IS NOT NULL OR " +
                "     (ta.planned_start IS NOT NULL AND ta.planned_start < NOW())) " +
                "ORDER BY ta.AssignedDate DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaskReassignmentDTO task = new TaskReassignmentDTO();
                task.setAssignmentId(rs.getInt("AssignmentID"));
                task.setTaskType(rs.getString("task_type"));

                Timestamp plannedStartTs = rs.getTimestamp("planned_start");
                if (plannedStartTs != null) {
                    LocalDateTime plannedStartLdt = plannedStartTs.toLocalDateTime();
                    task.setPlannedStart(plannedStartLdt.format(DATETIME_FORMATTER));
                    task.setPlannedStartRaw(plannedStartLdt);
                }

                Timestamp plannedEndTs = rs.getTimestamp("planned_end");
                if (plannedEndTs != null) {
                    LocalDateTime plannedEndLdt = plannedEndTs.toLocalDateTime();
                    task.setPlannedEnd(plannedEndLdt.format(DATETIME_FORMATTER));
                    task.setPlannedEndRaw(plannedEndLdt);
                }

                Timestamp assignedDateTs = rs.getTimestamp("AssignedDate");
                if (assignedDateTs != null) {
                    task.setAssignedDate(assignedDateTs.toLocalDateTime().format(DATETIME_FORMATTER));
                }

                Timestamp declinedAtTs = rs.getTimestamp("declined_at");
                if (declinedAtTs != null) {
                    task.setDeclinedAt(declinedAtTs.toLocalDateTime().format(DATETIME_FORMATTER));
                }

                task.setDeclineReason(rs.getString("decline_reason"));
                task.setTaskDescription(rs.getString("TaskDescription"));
                task.setVehicleInfo(rs.getString("vehicle_info"));
                task.setTechnicianName(rs.getString("technician_name"));
                task.setCustomerName(rs.getString("customer_name"));
                task.setCancelReasonType(rs.getString("cancel_reason_type"));

                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Reassign task to new technician with new scheduling.
     * Updates: AssignToTechID, Status='ASSIGNED', planned_start, planned_end
     * Clears: declined_at, decline_reason
     * 
     * @param assignmentId task assignment ID
     * @param newTechnicianId new technician ID
     * @param plannedStart new planned start time (nullable)
     * @param plannedEnd new planned end time (nullable)
     * @return true if reassignment successful
     * @throws SQLException if database error occurs
     */
    public boolean reassignTask(int assignmentId, int newTechnicianId,
                                 LocalDateTime plannedStart, LocalDateTime plannedEnd) throws SQLException {

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
}

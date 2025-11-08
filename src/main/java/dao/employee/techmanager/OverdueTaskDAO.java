package dao.employee.techmanager;

import common.DbContext;
import model.employee.techmanager.OverdueTaskDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Overdue Task operations.
 * Handles database queries for tasks that violated SLA (past planned_start time).
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class OverdueTaskDAO {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Get all overdue tasks (SLA violations).
     * Criteria: Status='ASSIGNED', StartAt IS NULL, planned_start < NOW()
     * 
     * @return list of overdue tasks
     * @throws SQLException if database error occurs
     */
    public List<OverdueTaskDTO> getOverdueTasks() throws SQLException {
        List<OverdueTaskDTO> tasks = new ArrayList<>();

        String sql = "SELECT ta.AssignmentID, ta.task_type, ta.planned_start, ta.planned_end, " +
                "ta.TaskDescription, ta.AssignedDate, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS vehicle_info, " +
                "u_tech.FullName AS technician_name, " +
                "u_cust.FullName AS customer_name, " +
                "TIMESTAMPDIFF(HOUR, ta.planned_start, NOW()) AS hours_overdue " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN User u_cust ON sr.CustomerID = u_cust.UserID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User u_tech ON e.UserID = u_tech.UserID " +
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
                task.setAssignmentId(rs.getInt("AssignmentID"));
                task.setTaskType(rs.getString("task_type"));

                Timestamp plannedStartTs = rs.getTimestamp("planned_start");
                if (plannedStartTs != null) {
                    task.setPlannedStart(plannedStartTs.toLocalDateTime().format(DATETIME_FORMATTER));
                }

                Timestamp plannedEndTs = rs.getTimestamp("planned_end");
                if (plannedEndTs != null) {
                    task.setPlannedEnd(plannedEndTs.toLocalDateTime().format(DATETIME_FORMATTER));
                }

                Timestamp assignedDateTs = rs.getTimestamp("AssignedDate");
                if (assignedDateTs != null) {
                    task.setAssignedDate(assignedDateTs.toLocalDateTime().format(DATETIME_FORMATTER));
                }

                task.setTaskDescription(rs.getString("TaskDescription"));
                task.setVehicleInfo(rs.getString("vehicle_info"));
                task.setTechnicianName(rs.getString("technician_name"));
                task.setCustomerName(rs.getString("customer_name"));
                task.setHoursOverdue(rs.getInt("hours_overdue"));

                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Cancel a task by setting Status = 'CANCELLED'.
     * After cancellation, task will appear in reassignment list.
     * 
     * @param assignmentId task assignment ID
     * @return true if cancellation successful
     * @throws SQLException if database error occurs
     */
    public boolean cancelTask(int assignmentId) throws SQLException {
        String sql = "UPDATE TaskAssignment SET Status = 'CANCELLED' WHERE AssignmentID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
}

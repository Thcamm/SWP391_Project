package dao.employee.techmanager;

import common.DbContext;
import model.employee.techmanager.DeclinedTaskDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Declined Task operations.
 * Handles database queries for tasks declined by technicians.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class DeclinedTaskDAO {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Get all tasks declined by technicians.
     * Criteria: declined_at IS NOT NULL, Status='CANCELLED'
     * 
     * @return list of declined tasks
     * @throws SQLException if database error occurs
     */
    public List<DeclinedTaskDTO> getDeclinedTasks() throws SQLException {
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

                Timestamp declinedAtTs = rs.getTimestamp("declined_at");
                if (declinedAtTs != null) {
                    task.setDeclinedAt(declinedAtTs.toLocalDateTime().format(DATETIME_FORMATTER));
                }

                task.setDeclineReason(rs.getString("decline_reason"));
                task.setTaskDescription(rs.getString("TaskDescription"));
                task.setVehicleInfo(rs.getString("vehicle_info"));
                task.setTechnicianName(rs.getString("technician_name"));
                task.setCustomerName(rs.getString("customer_name"));

                tasks.add(task);
            }
        }

        return tasks;
    }
}

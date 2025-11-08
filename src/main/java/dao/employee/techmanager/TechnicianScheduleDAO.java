package dao.employee.techmanager;

import common.DbContext;
import model.employee.techmanager.ScheduledTaskDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Technician Schedule operations.
 * Handles database queries for technician's daily schedule.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class TechnicianScheduleDAO {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    /**
     * Get scheduled tasks for a specific technician on a specific date.
     * 
     * @param technicianId technician employee ID
     * @param targetDate target date
     * @return list of scheduled tasks
     * @throws SQLException if database error occurs
     */
    public List<ScheduledTaskDTO> getScheduledTasks(int technicianId, LocalDate targetDate) throws SQLException {
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
                    task.setAssignmentId(rs.getInt("AssignmentID"));
                    task.setTaskType(rs.getString("task_type"));
                    task.setStatus(rs.getString("Status"));

                    Timestamp plannedStartTs = rs.getTimestamp("planned_start");
                    Timestamp plannedEndTs = rs.getTimestamp("planned_end");

                    if (plannedStartTs != null) {
                        LocalDateTime startDt = plannedStartTs.toLocalDateTime();
                        task.setPlannedStart(startDt.format(DATETIME_FORMATTER));

                        // Check if overdue
                        boolean isOverdue = "ASSIGNED".equals(task.getStatus()) && LocalDateTime.now().isAfter(startDt);
                        task.setOverdue(isOverdue);
                    }

                    if (plannedEndTs != null) {
                        task.setPlannedEnd(plannedEndTs.toLocalDateTime().format(DATETIME_FORMATTER));
                    }

                    task.setTaskDescription(rs.getString("TaskDescription"));
                    task.setVehicleInfo(rs.getString("vehicle_info"));

                    tasks.add(task);
                }
            }
        }

        return tasks;
    }
}

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
     * Criteria: declined_at IS NOT NULL, Status='DECLINED'
     * Uses LEFT JOINs to avoid losing data if related records are missing.
     * 
     * @return list of declined tasks
     * @throws SQLException if database error occurs
     */
    public List<DeclinedTaskDTO> getDeclinedTasks() throws SQLException {
        List<DeclinedTaskDTO> tasks = new ArrayList<>();

        String sql = "SELECT ta.AssignmentID, ta.task_type, ta.planned_start, ta.planned_end, " +
                "ta.TaskDescription, ta.AssignedDate, ta.declined_at, ta.decline_reason, " +
                "COALESCE(CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate), 'N/A') AS vehicle_info, " +
                "tech_user.FullName AS technician_name, " +
                "COALESCE(cust_user.FullName, 'N/A') AS customer_name " +
                "FROM TaskAssignment ta " +
                "LEFT JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "LEFT JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "LEFT JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "LEFT JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "LEFT JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "LEFT JOIN User cust_user ON c.UserID = cust_user.UserID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User tech_user ON e.UserID = tech_user.UserID " +
                "WHERE ta.declined_at IS NOT NULL " +
                "AND ta.Status = 'DECLINED' " +
                "ORDER BY ta.declined_at DESC";

        System.out.println("[DeclinedTaskDAO] ===== Executing query =====");
        System.out.println("[DeclinedTaskDAO] SQL: " + sql);

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("[DeclinedTaskDAO] Connection established successfully");
            System.out.println("[DeclinedTaskDAO] Executing query...");

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("[DeclinedTaskDAO] Query executed successfully");

                int count = 0;
                while (rs.next()) {
                    count++;

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

                    System.out.println("[DeclinedTaskDAO] Row " + count + " - " +
                            "AssignmentID=" + task.getAssignmentId() +
                            ", Technician=" + task.getTechnicianName() +
                            ", Vehicle=" + task.getVehicleInfo() +
                            ", Reason="
                            + (task.getDeclineReason() != null
                                    ? task.getDeclineReason().substring(0,
                                            Math.min(50, task.getDeclineReason().length())) + "..."
                                    : "null"));

                    tasks.add(task);
                }

                if (count == 0) {
                    System.out.println("[DeclinedTaskDAO] ⚠️ WARNING: Query returned 0 rows!");
                    System.out.println(
                            "[DeclinedTaskDAO] This means NO records match: declined_at IS NOT NULL AND Status='DECLINED'");
                } else {
                    System.out.println("[DeclinedTaskDAO] ✓ Total declined tasks found: " + count);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DeclinedTaskDAO] ❌ SQL ERROR: " + e.getMessage());
            System.err.println("[DeclinedTaskDAO] SQL State: " + e.getSQLState());
            System.err.println("[DeclinedTaskDAO] Error Code: " + e.getErrorCode());
            e.printStackTrace();
            throw e;
        }

        System.out.println("[DeclinedTaskDAO] ===== Returning " + tasks.size() + " tasks =====");
        return tasks;
    }

    /**
     * Count number of declined tasks.
     * Used for dashboard statistics.
     * 
     * @return count of declined tasks
     * @throws SQLException if database error occurs
     */
    public int countDeclinedTasks() throws SQLException {
        String sql = "SELECT COUNT(*) " +
                "FROM TaskAssignment ta " +
                "WHERE ta.declined_at IS NOT NULL " +
                "AND ta.Status = 'DECLINED'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("[DeclinedTaskDAO] countDeclinedTasks() = " + count);
                return count;
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("[DeclinedTaskDAO] ERROR in countDeclinedTasks(): " + e.getMessage());
            throw e;
        }
    }
}

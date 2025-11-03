package dao.workorder;

import common.DbContext;
import model.employee.techmanager.RejectedTaskDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing rejected tasks
 */
public class RejectedTaskDAO {

    /**
     * Get all tasks that were rejected by technicians and not yet reassigned
     * 
     * @return List of RejectedTaskDTO
     * @throws SQLException if database error occurs
     */
    public List<RejectedTaskDTO> getRejectedTasks() throws SQLException {
        List<RejectedTaskDTO> tasks = new ArrayList<>();

        String sql = "SELECT " +
                "ta.AssignmentID, " +
                "ta.task_type, " +
                "ta.priority, " +
                "wod.TaskDescription, " +
                "wod.EstimateHours, " +
                "wod.EstimateAmount, " +
                "CONCAT(v.Brand, ' ', v.Model, ' (', v.LicensePlate, ')') as vehicleInfo, " +
                "CONCAT(u.FirstName, ' ', u.LastName) as customerName, " +
                "u.PhoneNumber as customerPhone, " +
                "CONCAT(tech.FirstName, ' ', tech.LastName) as technicianName, " +
                "tech.PhoneNumber as technicianPhone, " +
                "tal.ActivityTime as rejectedAt, " +
                "tal.Description as rejectionReason " +
                "FROM TechnicianActivityLog tal " +
                "JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "LEFT JOIN ServiceRequestDetail srd ON sr.RequestID = srd.RequestID " +
                "LEFT JOIN Vehicle v ON srd.VehicleID = v.VehicleID " +
                "JOIN User u ON sr.CustomerID = u.UserID " +
                "JOIN Employee emp ON ta.AssignedToEmployeeID = emp.EmployeeID " +
                "JOIN User tech ON emp.UserID = tech.UserID " +
                "WHERE tal.ActivityType = 'TASK_REJECTED' " +
                "AND ta.Status = 'ASSIGNED' " +
                "ORDER BY tal.ActivityTime DESC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tasks.add(extractRejectedTask(rs));
            }
        }

        return tasks;
    }

    /**
     * Count number of rejected tasks that need reassignment
     * 
     * @return count of rejected tasks
     * @throws SQLException if database error occurs
     */
    public int countRejectedTasks() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT ta.AssignmentID) " +
                "FROM TechnicianActivityLog tal " +
                "JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                "WHERE tal.ActivityType = 'TASK_REJECTED' " +
                "AND ta.Status = 'ASSIGNED'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Get rejected task details by assignment ID
     * 
     * @param assignmentId the task assignment ID
     * @return RejectedTaskDTO or null if not found
     * @throws SQLException if database error occurs
     */
    public RejectedTaskDTO getRejectedTaskById(int assignmentId) throws SQLException {
        String sql = "SELECT " +
                "ta.AssignmentID, " +
                "ta.task_type, " +
                "ta.priority, " +
                "wod.TaskDescription, " +
                "wod.EstimateHours, " +
                "wod.EstimateAmount, " +
                "CONCAT(v.Brand, ' ', v.Model, ' (', v.LicensePlate, ')') as vehicleInfo, " +
                "CONCAT(u.FirstName, ' ', u.LastName) as customerName, " +
                "u.PhoneNumber as customerPhone, " +
                "CONCAT(tech.FirstName, ' ', tech.LastName) as technicianName, " +
                "tech.PhoneNumber as technicianPhone, " +
                "tal.ActivityTime as rejectedAt, " +
                "tal.Description as rejectionReason " +
                "FROM TechnicianActivityLog tal " +
                "JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "LEFT JOIN ServiceRequestDetail srd ON sr.RequestID = srd.RequestID " +
                "LEFT JOIN Vehicle v ON srd.VehicleID = v.VehicleID " +
                "JOIN User u ON sr.CustomerID = u.UserID " +
                "JOIN Employee emp ON ta.AssignedToEmployeeID = emp.EmployeeID " +
                "JOIN User tech ON emp.UserID = tech.UserID " +
                "WHERE tal.ActivityType = 'TASK_REJECTED' " +
                "AND ta.Status = 'ASSIGNED' " +
                "AND ta.AssignmentID = ? " +
                "ORDER BY tal.ActivityTime DESC " +
                "LIMIT 1";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, assignmentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractRejectedTask(rs);
                }
            }
        }

        return null;
    }

    /**
     * Extract RejectedTaskDTO from ResultSet
     * Helper method to avoid code duplication
     * 
     * @param rs ResultSet positioned at a row
     * @return RejectedTaskDTO
     * @throws SQLException if error reading from ResultSet
     */
    private RejectedTaskDTO extractRejectedTask(ResultSet rs) throws SQLException {
        RejectedTaskDTO task = new RejectedTaskDTO();
        task.setAssignmentId(rs.getInt("AssignmentID"));
        task.setTaskType(rs.getString("task_type"));
        task.setPriority(rs.getString("priority"));
        task.setTaskDescription(rs.getString("TaskDescription"));
        task.setEstimateHours(rs.getBigDecimal("EstimateHours"));
        task.setEstimateAmount(rs.getBigDecimal("EstimateAmount"));
        task.setVehicleInfo(rs.getString("vehicleInfo"));
        task.setCustomerName(rs.getString("customerName"));
        task.setCustomerPhone(rs.getString("customerPhone"));
        task.setTechnicianName(rs.getString("technicianName"));
        task.setTechnicianPhone(rs.getString("technicianPhone"));
        task.setRejectedAt(rs.getTimestamp("rejectedAt"));
        task.setRejectionReason(rs.getString("rejectionReason"));
        return task;
    }
}

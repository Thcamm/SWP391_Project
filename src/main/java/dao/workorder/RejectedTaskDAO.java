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
                "ta.TaskDescription, " +
                "CONCAT(v.Brand, ' ', v.Model, ' (', v.LicensePlate, ')') as vehicleInfo, " +
                "cust_user.FullName as customerName, " +
                "cust_user.PhoneNumber as customerPhone, " +
                "tech_user.FullName as technicianName, " +
                "tech_user.PhoneNumber as technicianPhone, " +
                "tal.ActivityTime as rejectedAt, " +
                "tal.Description as rejectionReason " +
                "FROM TechnicianActivityLog tal " +
                "JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User cust_user ON c.UserID = cust_user.UserID " +
                "JOIN Employee emp ON ta.AssignToTechID = emp.EmployeeID " +
                "JOIN User tech_user ON emp.UserID = tech_user.UserID " +
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
                "ta.TaskDescription, " +
                "CONCAT(v.Brand, ' ', v.Model, ' (', v.LicensePlate, ')') as vehicleInfo, " +
                "cust_user.FullName as customerName, " +
                "cust_user.PhoneNumber as customerPhone, " +
                "tech_user.FullName as technicianName, " +
                "tech_user.PhoneNumber as technicianPhone, " +
                "tal.ActivityTime as rejectedAt, " +
                "tal.Description as rejectionReason " +
                "FROM TechnicianActivityLog tal " +
                "JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User cust_user ON c.UserID = cust_user.UserID " +
                "JOIN Employee emp ON ta.AssignToTechID = emp.EmployeeID " +
                "JOIN User tech_user ON emp.UserID = tech_user.UserID " +
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
        task.setTaskDescription(rs.getString("TaskDescription"));
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

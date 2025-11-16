package dao.employee.techmanager;

import common.DbContext;
import model.employee.techmanager.WorkOrderCloseDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Work Order Closure operations (GĐ7)
 * 
 * Business Rule: A WorkOrder can be closed when ALL tasks are either:
 * - COMPLETE (finished successfully)
 * - CANCELLED (declined/cancelled)
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored - Show ALL, validate on close)
 */
public class WorkOrderCloseDAO {

    /**
     * Get ALL IN_PROCESS work orders for this TechManager
     * Shows all work orders with task status breakdown
     * 
     * @param techManagerId TechManager's employee ID
     * @return List of ALL IN_PROCESS work orders
     * @throws SQLException if database error occurs
     */
    public List<WorkOrderCloseDTO> getAllWorkOrdersForClosure(int techManagerId) throws SQLException {
        List<WorkOrderCloseDTO> workOrders = new ArrayList<>();

        String sql = "SELECT wo.WorkOrderID, " +
                "CONCAT(v.LicensePlate, ' - ', v.Brand, ' ', v.Model) AS VehicleInfo, " +
                "u_cust.FullName AS CustomerName, " +
                "u_tm.FullName AS TechManagerName, " +
                "wo.CreatedAt, " +
                "DATEDIFF(NOW(), wo.CreatedAt) AS DaysOpen, " +
                "COUNT(ta.AssignmentID) AS TotalTasks, " +
                "SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) AS CompletedTasks, " +
                "SUM(CASE WHEN ta.Status = 'CANCELLED' THEN 1 ELSE 0 END) AS CancelledTasks, " +
                "SUM(CASE WHEN ta.Status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) AS ActiveTasks " +
                "FROM WorkOrder wo " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "JOIN Employee e_tm ON wo.TechManagerID = e_tm.EmployeeID " +
                "JOIN User u_tm ON e_tm.UserID = u_tm.UserID " +
                "JOIN WorkOrderDetail wod ON wo.WorkOrderID = wod.WorkOrderID " +
                "JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "WHERE wo.TechManagerID = ? " +
                "AND wo.Status = 'IN_PROCESS' " +
                "GROUP BY wo.WorkOrderID " +
                "HAVING COUNT(ta.AssignmentID) > 0 " +
                "ORDER BY wo.CreatedAt ASC";

        System.out.println("=== [WorkOrderCloseDAO] Querying ALL work orders for closure ===");
        System.out.println("TechManager ID: " + techManagerId);

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    WorkOrderCloseDTO dto = new WorkOrderCloseDTO();
                    dto.setWorkOrderID(rs.getInt("WorkOrderID"));
                    dto.setVehicleInfo(rs.getString("VehicleInfo"));
                    dto.setCustomerName(rs.getString("CustomerName"));
                    dto.setTechManagerName(rs.getString("TechManagerName"));
                    dto.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    dto.setDaysOpen(rs.getInt("DaysOpen"));
                    dto.setTotalTasks(rs.getInt("TotalTasks"));
                    dto.setCompletedTasks(rs.getInt("CompletedTasks"));
                    dto.setCancelledTasks(rs.getInt("CancelledTasks"));
                    dto.setActiveTasks(rs.getInt("ActiveTasks"));

                    // Can close if no active tasks
                    boolean canClose = dto.getActiveTasks() == 0;
                    dto.setAllTasksComplete(canClose);

                    workOrders.add(dto);
                    count++;

                    System.out.println("  [" + count + "] WO #" + dto.getWorkOrderID() +
                            ", Tasks: " + dto.getCompletedTasks() + "/" + dto.getTotalTasks() +
                            " (Active: " + dto.getActiveTasks() + ", Cancelled: " + dto.getCancelledTasks() + ")" +
                            ", Can Close: " + canClose);
                }
                System.out.println("✓ Total work orders: " + count);
            }
        }

        return workOrders;
    }

    /**
     * Check if a work order can be closed
     * Returns detailed status for error messages
     * 
     * @param workOrderId Work Order ID
     * @return WorkOrderCloseDTO with task breakdown, or null if not found
     * @throws SQLException if database error occurs
     */
    public WorkOrderCloseDTO getWorkOrderDetails(int workOrderId) throws SQLException {
        String sql = "SELECT wo.WorkOrderID, " +
                "CONCAT(v.LicensePlate, ' - ', v.Brand, ' ', v.Model) AS VehicleInfo, " +
                "u_cust.FullName AS CustomerName, " +
                "wo.Status, " +
                "COUNT(ta.AssignmentID) AS TotalTasks, " +
                "SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) AS CompletedTasks, " +
                "SUM(CASE WHEN ta.Status = 'CANCELLED' THEN 1 ELSE 0 END) AS CancelledTasks, " +
                "SUM(CASE WHEN ta.Status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) AS ActiveTasks " +
                "FROM WorkOrder wo " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "LEFT JOIN WorkOrderDetail wod ON wo.WorkOrderID = wod.WorkOrderID " +
                "LEFT JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "WHERE wo.WorkOrderID = ? " +
                "GROUP BY wo.WorkOrderID";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WorkOrderCloseDTO dto = new WorkOrderCloseDTO();
                    dto.setWorkOrderID(rs.getInt("WorkOrderID"));
                    dto.setVehicleInfo(rs.getString("VehicleInfo"));
                    dto.setCustomerName(rs.getString("CustomerName"));
                    dto.setTotalTasks(rs.getInt("TotalTasks"));
                    dto.setCompletedTasks(rs.getInt("CompletedTasks"));
                    dto.setCancelledTasks(rs.getInt("CancelledTasks"));
                    dto.setActiveTasks(rs.getInt("ActiveTasks"));
                    return dto;
                }
            }
        }

        return null;
    }

    /**
     * Close a work order
     * Updates status to COMPLETE
     * 
     * @param workOrderId Work Order ID
     * @return true if successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean closeWorkOrder(int workOrderId) throws SQLException {
        String sql = "UPDATE WorkOrder " +
                "SET Status = 'COMPLETE', " +
                "UpdatedAt = NOW() " +
                "WHERE WorkOrderID = ? " +
                "AND Status = 'IN_PROCESS'";

        System.out.println("[WorkOrderCloseDAO] Closing Work Order #" + workOrderId);

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderId);

            int rowsAffected = ps.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                System.out.println("✓ Work Order #" + workOrderId + " closed successfully");
            } else {
                System.out.println("✗ Failed to close Work Order #" + workOrderId);
            }

            return success;
        }
    }

    /**
     * Count work orders ready for closure
     * 
     * @param techManagerId TechManager's employee ID
     * @return count of work orders ready to close
     * @throws SQLException if database error occurs
     */
    public int countWorkOrdersReadyForClosure(int techManagerId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT wo.WorkOrderID) " +
                "FROM WorkOrder wo " +
                "JOIN WorkOrderDetail wod ON wo.WorkOrderID = wod.WorkOrderID " +
                "JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "WHERE wo.TechManagerID = ? " +
                "AND wo.Status = 'IN_PROCESS' " +
                "GROUP BY wo.WorkOrderID " +
                "HAVING SUM(CASE WHEN ta.Status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) = 0";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                }
                return count;
            }
        }
    }
}

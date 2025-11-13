package dao.employee.techmanager;

import common.DbContext;
import model.employee.techmanager.WorkOrderCloseDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for WorkOrder closure operations (GĐ7).
 * Handles database queries for closing work orders.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class WorkOrderCloseDAO {

    /**
     * Get all WorkOrders ready for closure.
     * A WorkOrder is ready when:
     * 1. Status = 'IN_PROCESS'
     * 2. All TaskAssignments are COMPLETE
     * 
     * @return List of WorkOrders ready to close
     * @throws SQLException if database error occurs
     */
    public List<WorkOrderCloseDTO> getWorkOrdersReadyForClosure() throws SQLException {
        String sql = "SELECT " +
                "    wo.WorkOrderID, " +
                "    wo.RequestID, " +
                "    wo.CreatedAt, " +
                "    CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "    u_cust.FullName AS CustomerName, " +
                "    u_tm.FullName AS TechManagerName, " +
                "    COUNT(DISTINCT ta.AssignmentID) AS TotalTasks, " +
                "    SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) AS CompletedTasks " +
                "FROM WorkOrder wo " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Employee e_tm ON wo.TechManagerID = e_tm.EmployeeID " +
                "JOIN User u_tm ON e_tm.UserID = u_tm.UserID " +
                "LEFT JOIN WorkOrderDetail wod ON wo.WorkOrderID = wod.WorkOrderID " +
                "LEFT JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "WHERE wo.Status = 'IN_PROCESS' " +
                "GROUP BY wo.WorkOrderID, wo.RequestID, wo.CreatedAt, VehicleInfo, CustomerName, TechManagerName " +
                "HAVING COUNT(DISTINCT ta.AssignmentID) > 0 " +
                "   AND TotalTasks = CompletedTasks " +
                "ORDER BY wo.CreatedAt ASC";

        List<WorkOrderCloseDTO> workOrders = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                WorkOrderCloseDTO dto = new WorkOrderCloseDTO();
                dto.setWorkOrderID(rs.getInt("WorkOrderID"));
                dto.setRequestID(rs.getInt("RequestID"));
                dto.setVehicleInfo(rs.getString("VehicleInfo"));
                dto.setCustomerName(rs.getString("CustomerName"));
                dto.setTechManagerName(rs.getString("TechManagerName"));
                dto.setTotalTasks(rs.getInt("TotalTasks"));
                dto.setCompletedTasks(rs.getInt("CompletedTasks"));
                dto.setCreatedAt(rs.getTimestamp("CreatedAt"));
                workOrders.add(dto);
            }
        }

        return workOrders;
    }

    /**
     * Close a WorkOrder by updating its status to COMPLETE.
     * This will trigger Invoice generation (if configured).
     * 
     * Only closes if:
     * 1. Current status is 'IN_PROCESS'
     * 2. All TaskAssignments are COMPLETE
     * 
     * @param workOrderID The ID of the WorkOrder to close
     * @return true if work order was closed successfully
     * @throws SQLException if database error occurs
     */
    public boolean closeWorkOrder(int workOrderID) throws SQLException {
        String sql = "UPDATE WorkOrder " +
                "SET Status = 'COMPLETE' " +
                "WHERE WorkOrderID = ? " +
                "AND Status = 'IN_PROCESS' " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM WorkOrderDetail wod " +
                "    LEFT JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "    WHERE wod.WorkOrderID = ? " +
                "    AND (ta.AssignmentID IS NULL OR ta.Status != 'COMPLETE')" +
                ")";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderID);
            ps.setInt(2, workOrderID);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Get a specific WorkOrder for closure verification.
     * 
     * @param workOrderID The ID of the WorkOrder
     * @return WorkOrderCloseDTO if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public WorkOrderCloseDTO getWorkOrderForClosure(int workOrderID) throws SQLException {
        String sql = "SELECT " +
                "    wo.WorkOrderID, " +
                "    wo.RequestID, " +
                "    wo.CreatedAt, " +
                "    CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "    u_cust.FullName AS CustomerName, " +
                "    u_tm.FullName AS TechManagerName, " +
                "    COUNT(DISTINCT ta.AssignmentID) AS TotalTasks, " +
                "    SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) AS CompletedTasks " +
                "FROM WorkOrder wo " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Employee e_tm ON wo.TechManagerID = e_tm.EmployeeID " +
                "JOIN User u_tm ON e_tm.UserID = u_tm.UserID " +
                "LEFT JOIN WorkOrderDetail wod ON wo.WorkOrderID = wod.WorkOrderID " +
                "LEFT JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "WHERE wo.WorkOrderID = ? " +
                "GROUP BY wo.WorkOrderID, wo.RequestID, wo.CreatedAt, VehicleInfo, CustomerName, TechManagerName";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WorkOrderCloseDTO dto = new WorkOrderCloseDTO();
                    dto.setWorkOrderID(rs.getInt("WorkOrderID"));
                    dto.setRequestID(rs.getInt("RequestID"));
                    dto.setVehicleInfo(rs.getString("VehicleInfo"));
                    dto.setCustomerName(rs.getString("CustomerName"));
                    dto.setTechManagerName(rs.getString("TechManagerName"));
                    dto.setTotalTasks(rs.getInt("TotalTasks"));
                    dto.setCompletedTasks(rs.getInt("CompletedTasks"));
                    dto.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    return dto;
                }
            }
        }

        return null;
    }
}

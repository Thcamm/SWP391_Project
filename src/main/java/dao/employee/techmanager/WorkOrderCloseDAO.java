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
 * [REFACTORED] Queries now correctly handle 'CANCELLED' tasks
 * and filter by TechManager.
 *
 * @author SWP391 Team
 * @version 2.0 (Logic Fixed)
 */
public class WorkOrderCloseDAO {

    /**
     * [FIXED] Get all WorkOrders ready for closure FOR A SPECIFIC MANAGER.
     * A WorkOrder is ready when:
     * 1. Status = 'IN_PROCESS'
     * 2. Has at least one TaskAssignment
     * 3. NO TaskAssignments are 'ASSIGNED' or 'IN_PROGRESS'
     * (This correctly includes WorkOrders with COMPLETE and CANCELLED tasks)
     *
     * @param techManagerId The EmployeeID of the logged-in Tech Manager.
     * @return List of WorkOrders ready to close
     * @throws SQLException if database error occurs
     */
    public List<WorkOrderCloseDTO> getWorkOrdersReadyForClosure(int techManagerId) throws SQLException {
        String sql = "SELECT " +
                "    wo.WorkOrderID, " +
                "    wo.RequestID, " +
                "    wo.CreatedAt, " +
                "    CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "    u_cust.FullName AS CustomerName, " +
                "    u_tm.FullName AS TechManagerName, " +
                "    COUNT(DISTINCT ta.AssignmentID) AS TotalTasks, " +
                "    SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) AS CompletedTasks, " +
                "    SUM(CASE WHEN ta.Status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) AS ActiveTasks " +
                "FROM WorkOrder wo " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Employee e_tm ON wo.TechManagerID = e_tm.EmployeeID " +
                "JOIN User u_tm ON e_tm.UserID = u_tm.UserID " +
                "JOIN WorkOrderDetail wod ON wo.WorkOrderID = wod.WorkOrderID " +
                "JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "WHERE wo.Status = 'IN_PROCESS' " +
                "  AND wo.TechManagerID = ? " + // Lọc theo TM
                "GROUP BY wo.WorkOrderID, wo.RequestID, wo.CreatedAt, VehicleInfo, CustomerName, TechManagerName " +
                // Điều kiện mới: KHÔNG TỒN TẠI BẤT KỲ TASK NÀO ĐANG CHẠY
                "HAVING SUM(CASE WHEN ta.Status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) = 0 " +
                "ORDER BY wo.CreatedAt ASC";

        List<WorkOrderCloseDTO> workOrders = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, techManagerId); // <-- Truyền tham số

            try (ResultSet rs = ps.executeQuery()) {
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
                    dto.setActiveTasks(rs.getInt("ActiveTasks")); // Lưu số task active
                    workOrders.add(dto);
                }
            }
        }
        return workOrders;
    }

    /**
     * [FIXED] Close a WorkOrder by updating its status to COMPLETE.
     * This query now validates that NO tasks are 'ASSIGNED' or 'IN_PROGRESS'
     * AND checks the TechManagerID for security.
     *
     * @param workOrderID   The ID of the WorkOrder to close
     * @param techManagerId The ID of the TM performing the action (for security)
     * @return true if work order was closed successfully
     * @throws SQLException if database error occurs
     */
    public boolean closeWorkOrder(int workOrderID, int techManagerId) throws SQLException {
        String sql = "UPDATE WorkOrder " +
                "SET Status = 'COMPLETE', UpdatedAt = NOW() " +
                "WHERE WorkOrderID = ? " +
                "  AND TechManagerID = ? " + // Thêm check bảo mật
                "  AND Status = 'IN_PROCESS' " +
                // VÀ KHÔNG TỒN TẠI task nào đang chạy
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM WorkOrderDetail wod " +
                "    JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "    WHERE wod.WorkOrderID = ? " +
                "    AND ta.Status IN ('ASSIGNED', 'IN_PROGRESS')" +
                ")";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderID);
            ps.setInt(2, techManagerId);
            ps.setInt(3, workOrderID); // Tham số thứ 3 cho subquery

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * [FIXED] Get a specific WorkOrder for closure verification.
     * (Đã thêm 'ActiveTasks' (task đang chạy) vào query)
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
                "    SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) AS CompletedTasks, " +
                "    SUM(CASE WHEN ta.Status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) AS ActiveTasks " + // (MỚI)
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

                    // THÊM DÒNG NÀY: Lấy số task đang chạy từ CSDL
                    dto.setActiveTasks(rs.getInt("ActiveTasks"));

                    return dto;
                }
            }
        }
        return null;
    }

    /**
     * [MỚI] Đếm số WorkOrder theo Status và Khoảng thời gian (Ngày/Tháng)
     * Dùng cho thống kê trên trang Close WorkOrder.
     */
    public int countWorkOrdersByStatusAndDate(int techManagerId, String status, String interval) throws SQLException {
        String dateCondition = "";

        // Giả sử CSDL của bạn có cột UpdatedAt trên bảng WorkOrder và nó tự động cập
        // nhật
        if ("DAY".equalsIgnoreCase(interval)) {
            dateCondition = " AND DATE(wo.UpdatedAt) = CURDATE()";
        } else if ("MONTH".equalsIgnoreCase(interval)) {
            dateCondition = " AND YEAR(wo.UpdatedAt) = YEAR(CURDATE()) AND MONTH(wo.UpdatedAt) = MONTH(CURDATE())";
        }

        String sql = "SELECT COUNT(wo.WorkOrderID) " +
                "FROM WorkOrder wo " +
                "WHERE wo.TechManagerID = ? " +
                "  AND wo.Status = ? " +
                dateCondition;

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, techManagerId);
            ps.setString(2, status);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
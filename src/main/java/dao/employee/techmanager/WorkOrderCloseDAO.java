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
 * DAO for Work Order Closure operations (GĐ7).
 * [REFACTORED] Fixed incorrect JOIN logic for Customer.
 *
 * @author SWP391 Team
 * @version 3.2 (JOIN Logic Fixed)
 */
public class WorkOrderCloseDAO {

    /**
     * [FIXED] Lấy TẤT CẢ WorkOrders đang 'IN_PROCESS' của 1 TM.
     * (Đã sửa lỗi JOIN Customer: Lấy Customer từ ServiceRequest thay vì Vehicle)
     */
    public List<WorkOrderCloseDTO> getAllInProgressWorkOrders(int techManagerId) throws SQLException {
        List<WorkOrderCloseDTO> workOrders = new ArrayList<>();

        String sql = "SELECT " +
                "    wo.WorkOrderID, " +
                "    sr.RequestID, " +
                "    CONCAT(v.LicensePlate, ' - ', v.Brand, ' ', v.Model) AS VehicleInfo, " +
                "    u_cust.FullName AS CustomerName, " + // Lấy từ u_cust
                "    u_tm.FullName AS TechManagerName, " +
                "    wo.CreatedAt, " +
                "    DATEDIFF(NOW(), wo.CreatedAt) AS DaysOpen, " +
                "    COUNT(DISTINCT wod.DetailID) AS TotalWorkDetails, " +
                "    COUNT(DISTINCT ta.AssignmentID) AS TotalTasks, " +
                "    SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) AS CompletedTasks, " +
                "    SUM(CASE WHEN ta.Status IN ('CANCELLED', 'DECLINED') THEN 1 ELSE 0 END) AS CancelledTasks, " +
                "    SUM(CASE WHEN ta.Status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) AS ActiveTasks, " +
                "    SUM(CASE WHEN ta.AssignmentID IS NULL THEN 1 ELSE 0 END) AS UnassignedDetails " +
                "FROM WorkOrder wo " +
                "LEFT JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +

                // [FIX] Sửa logic JOIN: Lấy Customer từ ServiceRequest
                "LEFT JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "LEFT JOIN User u_cust ON c.UserID = u_cust.UserID " +

                "LEFT JOIN Vehicle v ON sr.VehicleID = v.VehicleID " + // (Join Vehicle riêng)
                "LEFT JOIN Employee e_tm ON wo.TechManagerID = e_tm.EmployeeID " +
                "LEFT JOIN User u_tm ON e_tm.UserID = u_tm.UserID " +
                "LEFT JOIN WorkOrderDetail wod ON wo.WorkOrderID = wod.WorkOrderID " +
                "LEFT JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "WHERE wo.TechManagerID = ? " +
                "  AND wo.Status IN ('IN_PROCESS') " +
                "GROUP BY wo.WorkOrderID, sr.RequestID, VehicleInfo, CustomerName, TechManagerName, wo.CreatedAt, DaysOpen "
                +
                "ORDER BY wo.CreatedAt ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WorkOrderCloseDTO dto = new WorkOrderCloseDTO();
                    dto.setWorkOrderID(rs.getInt("WorkOrderID"));
                    dto.setRequestID(rs.getInt("RequestID"));
                    dto.setVehicleInfo(rs.getString("VehicleInfo"));
                    dto.setCustomerName(rs.getString("CustomerName"));
                    dto.setTechManagerName(rs.getString("TechManagerName"));
                    dto.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    dto.setDaysOpen(rs.getInt("DaysOpen"));
                    dto.setTotalTasks(rs.getInt("TotalTasks"));
                    dto.setCompletedTasks(rs.getInt("CompletedTasks"));
                    dto.setCancelledTasks(rs.getInt("CancelledTasks"));
                    dto.setActiveTasks(rs.getInt("ActiveTasks"));

                    if (rs.getInt("TotalWorkDetails") > 0 && rs.getInt("TotalTasks") == 0) {
                        dto.setActiveTasks(rs.getInt("TotalWorkDetails"));
                    }

                    workOrders.add(dto);
                }
            }
        }
        return workOrders;
    }

    /**
     * [FIXED] Close a WorkOrder by updating its status to COMPLETE.
     */
    public boolean closeWorkOrder(int workOrderID, int techManagerId) throws SQLException {
        String sql = "UPDATE WorkOrder " +
                "SET Status = 'COMPLETE', UpdatedAt = NOW() " +
                "WHERE WorkOrderID = ? " +
                "  AND TechManagerID = ? " +
                "  AND Status = 'IN_PROCESS' " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM WorkOrderDetail wod " +
                "    JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "    WHERE wod.WorkOrderID = WorkOrder.WorkOrderID " +
                "    AND ta.Status IN ('ASSIGNED', 'IN_PROGRESS')" +
                ") " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM WorkOrderDetail wod " +
                "    LEFT JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "    WHERE wod.WorkOrderID = WorkOrder.WorkOrderID " +
                "    AND ta.AssignmentID IS NULL" +
                ")";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderID);
            ps.setInt(2, techManagerId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * [FIXED] Lấy thông tin chi tiết của 1 WorkOrder để xác thực (Verify).
     * (Đã sửa lỗi JOIN Customer)
     */
    public WorkOrderCloseDTO getWorkOrderForVerification(int workOrderId, int techManagerId) throws SQLException {
        String sql = "SELECT wo.WorkOrderID, " +
                "    CONCAT(v.LicensePlate, ' - ', v.Brand, ' ', v.Model) AS VehicleInfo, " +
                "    u_cust.FullName AS CustomerName, " +
                "    wo.Status, " +
                "    COUNT(DISTINCT wod.DetailID) AS TotalWorkDetails, " +
                "    COUNT(DISTINCT ta.AssignmentID) AS TotalTasks, " +
                "    SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) AS CompletedTasks, " +
                "    SUM(CASE WHEN ta.Status IN ('CANCELLED', 'DECLINED') THEN 1 ELSE 0 END) AS CancelledTasks, " +
                "    SUM(CASE WHEN ta.Status IN ('ASSIGNED', 'IN_PROGRESS') THEN 1 ELSE 0 END) AS ActiveTasks, " +
                "    SUM(CASE WHEN ta.AssignmentID IS NULL THEN 1 ELSE 0 END) AS UnassignedDetails " +
                "FROM WorkOrder wo " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +

                // [FIX] Sửa logic JOIN
                "JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +

                "LEFT JOIN WorkOrderDetail wod ON wo.WorkOrderID = wod.WorkOrderID " +
                "LEFT JOIN TaskAssignment ta ON wod.DetailID = ta.DetailID " +
                "WHERE wo.WorkOrderID = ? " +
                "  AND wo.TechManagerID = ? " +
                "GROUP BY wo.WorkOrderID";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderId);
            ps.setInt(2, techManagerId);

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

                    if (rs.getInt("TotalWorkDetails") > 0 && rs.getInt("TotalTasks") == 0) {
                        dto.setActiveTasks(rs.getInt("TotalWorkDetails"));
                    }

                    return dto;
                }
            }
        }
        return null;
    }

    /**
     * [FIXED] Đếm số WorkOrder Sẵn sàng để đóng (cho Dashboard).
     */
    public int countWorkOrdersReadyForClosure(int techManagerId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT wo.WorkOrderID) AS ReadyCount " +
                "FROM WorkOrder wo " +
                "WHERE wo.TechManagerID = ? " +
                "AND wo.Status = 'IN_PROCESS' " +
                // Phải có ít nhất 1 WOD (nếu không thì không thể đóng)
                "AND EXISTS (SELECT 1 FROM WorkOrderDetail wod WHERE wod.WorkOrderID = wo.WorkOrderID) " +

                // VÀ KHÔNG TỒN TẠI BẤT KỲ TASK NÀO ĐANG CHẠY
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM WorkOrderDetail wod_sub " +
                "    JOIN TaskAssignment ta_sub ON wod_sub.DetailID = ta_sub.DetailID " +
                "    WHERE wod_sub.WorkOrderID = wo.WorkOrderID " +
                "    AND ta_sub.Status IN ('ASSIGNED', 'IN_PROGRESS')" +
                ") " +
                // VÀ KHÔNG TỒN TẠI BẤT KỲ WOD NÀO CHƯA GÁN
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM WorkOrderDetail wod_sub " +
                "    LEFT JOIN TaskAssignment ta_sub ON wod_sub.DetailID = ta_sub.DetailID " +
                "    WHERE wod_sub.WorkOrderID = wo.WorkOrderID " +
                "    AND ta_sub.AssignmentID IS NULL" +
                ")";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ReadyCount");
                }
                return 0;
            }
        }
    }

    /**
     * [MỚI] Đếm số WorkOrder theo Status và Khoảng thời gian (Ngày/Tháng)
     */
    public int countWorkOrdersByStatusAndDate(int techManagerId, String status, String interval) throws SQLException {
        String dateCondition = "";

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
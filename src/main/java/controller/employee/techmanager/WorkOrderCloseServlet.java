package controller.employee.techmanager;

import common.DbContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet for closing WorkOrders (GĐ7 - Final Phase).
 * TechManager reviews completed work orders and closes them.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
@WebServlet("/techmanager/close-workorders")
public class WorkOrderCloseServlet extends HttpServlet {

    /**
     * DTO for WorkOrder ready to close.
     */
    public static class WorkOrderCloseDTO {
        private int workOrderID;
        private int requestID;
        private String vehicleInfo;
        private String customerName;
        private int totalTasks;
        private int completedTasks;
        private java.sql.Timestamp createdAt;
        private String techManagerName;

        // Getters and Setters
        public int getWorkOrderID() { return workOrderID; }
        public void setWorkOrderID(int workOrderID) { this.workOrderID = workOrderID; }

        public int getRequestID() { return requestID; }
        public void setRequestID(int requestID) { this.requestID = requestID; }

        public String getVehicleInfo() { return vehicleInfo; }
        public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public int getTotalTasks() { return totalTasks; }
        public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }

        public int getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }

        public java.sql.Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

        public String getTechManagerName() { return techManagerName; }
        public void setTechManagerName(String techManagerName) { this.techManagerName = techManagerName; }

        public boolean isAllTasksComplete() {
            return totalTasks > 0 && totalTasks == completedTasks;
        }

        public int getDaysOpen() {
            if (createdAt == null) return 0;
            long diff = System.currentTimeMillis() - createdAt.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<WorkOrderCloseDTO> readyToClose = getWorkOrdersReadyForClosure();
            request.setAttribute("workOrders", readyToClose);
            request.setAttribute("totalReady", readyToClose.size());

            request.getRequestDispatcher("/view/techmanager/close-workorders.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Failed to load work orders: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("close".equals(action)) {
            handleClose(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/techmanager/close-workorders");
        }
    }

    /**
     * Handle closing a WorkOrder.
     */
    private void handleClose(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            int workOrderID = Integer.parseInt(request.getParameter("workOrderID"));

            // Verify session user is TechManager
            HttpSession session = request.getSession();
            String userName = (String) session.getAttribute("userName");
            if (userName == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Close the work order
            boolean success = closeWorkOrder(workOrderID);

            if (success) {
                response.sendRedirect(request.getContextPath() + 
                    "/techmanager/close-workorders?message=Work Order #" + workOrderID + 
                    " closed successfully&type=success");
            } else {
                response.sendRedirect(request.getContextPath() + 
                    "/techmanager/close-workorders?message=Failed to close Work Order. " +
                    "Please verify all tasks are complete.&type=danger");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + 
                "/techmanager/close-workorders?message=Invalid Work Order ID&type=danger");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + 
                "/techmanager/close-workorders?message=Database error: " + 
                e.getMessage() + "&type=danger");
        }
    }

    /**
     * Get all WorkOrders ready for closure.
     * A WorkOrder is ready when:
     * 1. Status = 'IN_PROCESS'
     * 2. All TaskAssignments are COMPLETE
     */
    private List<WorkOrderCloseDTO> getWorkOrdersReadyForClosure() throws SQLException {
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
     */
    private boolean closeWorkOrder(int workOrderID) throws SQLException {
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
}

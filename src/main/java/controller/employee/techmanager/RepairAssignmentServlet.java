package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import common.DbContext;

/**
 * Phase 3: Repair Assignment
 * - Show approved quotes (customer approved)
 * - Assign repair tasks to technicians
 */
@WebServlet("/techmanager/assign-repair")
public class RepairAssignmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = DbContext.getConnection()) {

            // Get approved quotes waiting for repair assignment
            List<ApprovedQuoteDTO> approvedQuotes = getApprovedQuotes(conn);

            // Get available technicians for repair
            List<TechnicianDTO> availableTechnicians = getAvailableTechnicians(conn);

            request.setAttribute("approvedQuotes", approvedQuotes);
            request.setAttribute("availableTechnicians", availableTechnicians);

            // Handle messages
            String message = request.getParameter("message");
            String type = request.getParameter("type");
            if (message != null) {
                request.setAttribute("message", message);
                request.setAttribute("messageType", type != null ? type : "info");
            }

            request.getRequestDispatcher("/view/techmanager/assign-repair.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading repair assignments: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String detailIdStr = request.getParameter("detailId");
        String technicianIdStr = request.getParameter("technicianId");
        String priority = request.getParameter("priority");
        String notes = request.getParameter("notes");

        if (detailIdStr == null || technicianIdStr == null || priority == null) {
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/assign-repair?message=Missing required fields&type=error");
            return;
        }

        try (Connection conn = DbContext.getConnection()) {
            int detailId = Integer.parseInt(detailIdStr);
            int technicianId = Integer.parseInt(technicianIdStr);

            // Create repair task assignment
            boolean success = createRepairTask(conn, detailId, technicianId, priority, notes);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-repair?message=Repair task assigned successfully&type=success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-repair?message=Failed to assign repair task&type=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/assign-repair?message=Error: " + e.getMessage() + "&type=error");
        }
    }

    /**
     * Get approved quotes (customer approved) that need repair assignment
     */
    private List<ApprovedQuoteDTO> getApprovedQuotes(Connection conn) throws Exception {
        List<ApprovedQuoteDTO> quotes = new ArrayList<>();

        String sql = "SELECT wod.DetailID as detailId, wod.WorkOrderID as workOrderId, wod.TaskDescription as taskDescription, "
                +
                "wod.EstimateAmount as estimateAmount, wod.approved_at as approvedAt, wod.diagnostic_id as diagnosticId, "
                +
                "wo.VehicleID as vehicleId, v.LicensePlate as licensePlate, v.VehicleModel as vehicleModel, " +
                "u.FullName as customerName, u.PhoneNumber as phoneNumber " +
                "FROM WorkOrderDetail wod " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN Vehicle v ON wo.VehicleID = v.VehicleID " +
                "JOIN User u ON v.CustomerID = u.UserID " +
                "WHERE wod.approval_status = 'APPROVED' " +
                "AND wod.source = 'DIAGNOSTIC' " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM TaskAssignment ta " +
                "    WHERE ta.DetailID = wod.DetailID AND ta.task_type = 'REPAIR' " +
                ") " +
                "ORDER BY wod.approved_at DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ApprovedQuoteDTO quote = new ApprovedQuoteDTO();
                quote.detailId = rs.getInt("detailId");
                quote.workOrderId = rs.getInt("workOrderId");
                quote.taskDescription = rs.getString("taskDescription");
                quote.estimateAmount = rs.getDouble("estimateAmount");
                quote.approvedAt = rs.getTimestamp("approvedAt");
                quote.diagnosticId = rs.getInt("diagnosticId");
                quote.vehicleId = rs.getInt("vehicleId");
                quote.licensePlate = rs.getString("licensePlate");
                quote.vehicleModel = rs.getString("vehicleModel");
                quote.customerName = rs.getString("customerName");
                quote.phoneNumber = rs.getString("phoneNumber");
                quotes.add(quote);
            }
        }

        return quotes;
    }

    /**
     * Get available technicians for repair tasks
     */
    private List<TechnicianDTO> getAvailableTechnicians(Connection conn) throws Exception {
        List<TechnicianDTO> technicians = new ArrayList<>();

        String sql = "SELECT e.EmployeeID as employeeId, u.FullName as fullName, u.PhoneNumber as phoneNumber, " +
                "(SELECT COUNT(*) FROM TaskAssignment ta " +
                "  WHERE ta.AssignedToEmployeeID = e.EmployeeID " +
                "  AND ta.status IN ('ASSIGNED', 'IN_PROGRESS')) as activeTasks " +
                "FROM Employee e " +
                "JOIN User u ON e.UserID = u.UserID " +
                "WHERE e.RoleID = ( " +
                "    SELECT RoleID FROM Role WHERE RoleName = 'Technician' " +
                ") " +
                "AND u.ActiveStatus = 1 " +
                "ORDER BY activeTasks ASC, u.FullName ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TechnicianDTO tech = new TechnicianDTO();
                tech.employeeId = rs.getInt("employeeId");
                tech.fullName = rs.getString("fullName");
                tech.phoneNumber = rs.getString("phoneNumber");
                tech.activeTasks = rs.getInt("activeTasks");
                technicians.add(tech);
            }
        }

        return technicians;
    }

    /**
     * Create repair task assignment
     */
    private boolean createRepairTask(Connection conn, int detailId, int technicianId,
            String priority, String notes) throws Exception {

        String sql = "INSERT INTO TaskAssignment " +
                "(DetailID, AssignedToEmployeeID, AssignedByEmployeeID, " +
                "task_type, status, priority, AssignedAt, notes) " +
                "VALUES (?, ?, ?, 'REPAIR', 'ASSIGNED', ?, NOW(), ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            ps.setInt(2, technicianId);
            ps.setInt(3, 1); // TODO: Get current tech manager employee ID from session
            ps.setString(4, priority);
            ps.setString(5, notes);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    // ===== DTOs =====

    public static class ApprovedQuoteDTO {
        public int detailId;
        public int workOrderId;
        public String taskDescription;
        public double estimateAmount;
        public java.sql.Timestamp approvedAt;
        public int diagnosticId;
        public int vehicleId;
        public String licensePlate;
        public String vehicleModel;
        public String customerName;
        public String phoneNumber;
    }

    public static class TechnicianDTO {
        public int employeeId;
        public String fullName;
        public String phoneNumber;
        public int activeTasks;
    }
}

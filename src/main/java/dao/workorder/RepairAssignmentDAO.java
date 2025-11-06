package dao.workorder;

import common.DbContext;
import model.employee.techmanager.ApprovedRepairDTO;
import model.employee.techmanager.TechnicianDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Repair Assignment operations
 * Handles approved Repairs and technician assignments
 */
public class RepairAssignmentDAO {

    /**
     * Get approved Repairs (customer approved) that need repair assignment
     */
    public List<ApprovedRepairDTO> getApprovedRepairs() throws SQLException {
        List<ApprovedRepairDTO> Repairs = new ArrayList<>();

        String sql = "SELECT wod.DetailID as detailId, wod.WorkOrderID as workOrderId, " +
                "wod.TaskDescription as taskDescription, " +
                "wod.EstimateAmount as estimateAmount, wod.approved_at as approvedAt, " +
                "wod.diagnostic_id as diagnosticId, " +
                "v.VehicleID as vehicleId, v.LicensePlate as licensePlate, v.Model as vehicleModel, " +
                "u.FullName as customerName, u.PhoneNumber as phoneNumber " +
                "FROM WorkOrderDetail wod " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "WHERE wod.approval_status = 'APPROVED' " +
                "AND wod.source = 'DIAGNOSTIC' " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM TaskAssignment ta " +
                "    WHERE ta.DetailID = wod.DetailID AND ta.task_type = 'REPAIR' " +
                ") " +
                "ORDER BY wod.approved_at DESC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ApprovedRepairDTO Repair = new ApprovedRepairDTO();
                Repair.setDetailId(rs.getInt("detailId"));
                Repair.setWorkOrderId(rs.getInt("workOrderId"));
                Repair.setTaskDescription(rs.getString("taskDescription"));
                Repair.setEstimateAmount(rs.getDouble("estimateAmount"));
                Repair.setApprovedAt(rs.getTimestamp("approvedAt"));
                Repair.setDiagnosticId(rs.getInt("diagnosticId"));
                Repair.setVehicleId(rs.getInt("vehicleId"));
                Repair.setLicensePlate(rs.getString("licensePlate"));
                Repair.setVehicleModel(rs.getString("vehicleModel"));
                Repair.setCustomerName(rs.getString("customerName"));
                Repair.setPhoneNumber(rs.getString("phoneNumber"));
                Repairs.add(Repair);
            }
        }

        return Repairs;
    }

    /**
     * Get available technicians for repair tasks
     */
    public List<TechnicianDTO> getAvailableTechnicians() throws SQLException {
        List<TechnicianDTO> technicians = new ArrayList<>();

        String sql = "SELECT e.EmployeeID as employeeId, u.FullName as fullName, " +
                "u.PhoneNumber as phoneNumber, " +
                "(SELECT COUNT(*) FROM TaskAssignment ta " +
                "  WHERE ta.AssignToTechID = e.EmployeeID " +
                "  AND ta.Status IN ('ASSIGNED', 'IN_PROGRESS')) as activeTasks " +
                "FROM Employee e " +
                "JOIN User u ON e.UserID = u.UserID " +
                "WHERE u.RoleID = ( " +
                "    SELECT RoleID FROM RoleInfo WHERE RoleName = 'Technician' " +
                ") " +
                "AND u.ActiveStatus = 1 " +
                "ORDER BY activeTasks ASC, u.FullName ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TechnicianDTO tech = new TechnicianDTO();
                tech.setEmployeeId(rs.getInt("employeeId"));
                tech.setFullName(rs.getString("fullName"));
                tech.setPhoneNumber(rs.getString("phoneNumber"));
                tech.setActiveTasks(rs.getInt("activeTasks"));
                technicians.add(tech);
            }
        }

        return technicians;
    }

    /**
     * Create repair task assignment
     */
    public boolean createRepairTask(int detailId, int technicianId) throws SQLException {
        String sql = "INSERT INTO TaskAssignment " +
                "(DetailID, AssignToTechID, task_type, Status, AssignedDate) " +
                "VALUES (?, ?, 'REPAIR', 'ASSIGNED', NOW())";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detailId);
            ps.setInt(2, technicianId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Check if a detail already has a repair task assigned
     */
    public boolean hasRepairTaskAssigned(int detailId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                "WHERE DetailID = ? AND task_type = 'REPAIR'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detailId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}

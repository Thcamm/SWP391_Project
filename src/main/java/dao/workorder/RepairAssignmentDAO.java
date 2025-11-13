package dao.workorder;

import common.DbContext;
import model.employee.techmanager.ApprovedRepairDTO;
import model.dto.TechnicianDTO;

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
     * LUỒNG MỚI - GĐ 5: Get approved Repairs that need repair assignment
     * 
     * REFACTORED: Changed from only DIAGNOSTIC to BOTH REQUEST and DIAGNOSTIC
     * - REQUEST: Services classified as "Làm luôn" in Triage (skip diagnosis)
     * - DIAGNOSTIC: Services that went through diagnosis and were approved
     * 
     * Returns WorkOrderDetails from BOTH sources that don't have a REPAIR
     * TaskAssignment yet
     */
    public List<ApprovedRepairDTO> getApprovedRepairs() throws SQLException {
        List<ApprovedRepairDTO> Repairs = new ArrayList<>();

        System.out.println("\n=== [RepairAssignmentDAO.getApprovedRepairs] START ===");

        // STEP 1: Test simple query without JOINs
        String testSql = "SELECT wod.DetailID, wod.WorkOrderID, wod.approval_status, wod.source " +
                "FROM WorkOrderDetail wod " +
                "WHERE wod.approval_status = 'APPROVED' " +
                "AND (wod.source = 'REQUEST' OR wod.source = 'DIAGNOSTIC')";

        try (Connection conn = DbContext.getConnection()) {
            System.out.println("STEP 1: Testing simple query (WOD only)...");
            try (PreparedStatement testPs = conn.prepareStatement(testSql);
                    ResultSet testRs = testPs.executeQuery()) {
                int simpleCount = 0;
                while (testRs.next()) {
                    simpleCount++;
                    System.out.println("  WOD #" + testRs.getInt("DetailID") +
                            ", WO=" + testRs.getInt("WorkOrderID") +
                            ", source=" + testRs.getString("source"));
                }
                System.out.println("Simple query found: " + simpleCount + " WODs");
            }

            // STEP 2: Test with WorkOrder JOIN
            String woJoinSql = testSql.replace("FROM WorkOrderDetail wod",
                    "FROM WorkOrderDetail wod JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID");
            System.out.println("\nSTEP 2: Testing with WorkOrder JOIN...");
            try (PreparedStatement woPs = conn.prepareStatement(woJoinSql);
                    ResultSet woRs = woPs.executeQuery()) {
                int woCount = 0;
                while (woRs.next())
                    woCount++;
                System.out.println("After WorkOrder JOIN: " + woCount + " records");
            }

            // STEP 3: Test with ServiceRequest JOIN
            String srJoinSql = woJoinSql.replace("JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID",
                    "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                            "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID");
            System.out.println("\nSTEP 3: Testing with ServiceRequest JOIN...");
            try (PreparedStatement srPs = conn.prepareStatement(srJoinSql);
                    ResultSet srRs = srPs.executeQuery()) {
                int srCount = 0;
                while (srRs.next())
                    srCount++;
                System.out.println("After ServiceRequest JOIN: " + srCount + " records");
            } catch (SQLException e) {
                System.err.println("ERROR at ServiceRequest JOIN: " + e.getMessage());
            }

            // STEP 4: Full query with all JOINs
            String sql = "SELECT wod.DetailID as detailId, wod.WorkOrderID as workOrderId, " +
                    "wod.TaskDescription as taskDescription, " +
                    "wod.EstimateAmount as estimateAmount, wod.approved_at as approvedAt, " +
                    "wod.diagnostic_id as diagnosticId, wod.source as source, " +
                    "v.VehicleID as vehicleId, v.LicensePlate as licensePlate, v.Model as vehicleModel, " +
                    "u.FullName as customerName, u.PhoneNumber as phoneNumber " +
                    "FROM WorkOrderDetail wod " +
                    "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                    "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                    "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                    "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                    "JOIN User u ON c.UserID = u.UserID " +
                    "WHERE wod.approval_status = 'APPROVED' " +
                    "AND (wod.source = 'REQUEST' OR wod.source = 'DIAGNOSTIC') " +
                    "AND NOT EXISTS ( " +
                    "    SELECT 1 FROM TaskAssignment ta " +
                    "    WHERE ta.DetailID = wod.DetailID AND ta.task_type = 'REPAIR' " +
                    ") " +
                    "ORDER BY wod.approved_at DESC";

            System.out.println("\nSTEP 4: Executing full query with all JOINs...");
            try (PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {

                int count = 0;
                while (rs.next()) {
                    count++;
                    ApprovedRepairDTO Repair = new ApprovedRepairDTO();
                    int detailId = rs.getInt("detailId");
                    String source = rs.getString("source");
                    System.out.println("  [" + count + "] DetailID=" + detailId + ", source=" + source +
                            ", vehicle=" + rs.getString("licensePlate"));

                    Repair.setDetailId(detailId);
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
                System.out.println("Total repairs found: " + count);
            } catch (SQLException e) {
                System.err.println("ERROR in full query: " + e.getMessage());
                e.printStackTrace();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("ERROR in getApprovedRepairs: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        System.out.println("=== [RepairAssignmentDAO] END ===\n");
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
                tech.setEmployeeID(rs.getInt("employeeId")); // model.dto uses setEmployeeID (uppercase ID)
                tech.setFullName(rs.getString("fullName"));
                tech.setPhoneNumber(rs.getString("phoneNumber"));
                // activeTasks không có trong model.dto.TechnicianDTO - bỏ qua
                technicians.add(tech);
            }
        }

        return technicians;
    }

    /**
     * Create repair task assignment (without scheduling)
     * 
     * @deprecated Use createRepairTask(int, int, LocalDateTime, LocalDateTime) for
     *             new workflow
     */
    @Deprecated
    public boolean createRepairTask(int detailId, int technicianId) throws SQLException {
        return createRepairTask(detailId, technicianId, null, null);
    }

    /**
     * Create repair task assignment with scheduling support
     * 
     * @param detailId     WorkOrderDetail ID
     * @param technicianId Technician Employee ID
     * @param plannedStart Scheduled start time (optional)
     * @param plannedEnd   Scheduled end time (optional)
     */
    public boolean createRepairTask(int detailId, int technicianId,
            java.time.LocalDateTime plannedStart,
            java.time.LocalDateTime plannedEnd) throws SQLException {
        String sql = "INSERT INTO TaskAssignment " +
                "(DetailID, AssignToTechID, task_type, Status, AssignedDate, planned_start, planned_end) " +
                "VALUES (?, ?, 'REPAIR', 'ASSIGNED', NOW(), ?, ?)";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detailId);
            ps.setInt(2, technicianId);

            // Set scheduling times (nullable)
            if (plannedStart != null) {
                ps.setObject(3, plannedStart);
            } else {
                ps.setNull(3, java.sql.Types.TIMESTAMP);
            }

            if (plannedEnd != null) {
                ps.setObject(4, plannedEnd);
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }

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

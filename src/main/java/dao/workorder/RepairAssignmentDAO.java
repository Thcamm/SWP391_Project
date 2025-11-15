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
                "AND wod.source = 'REQUEST'"; // Only REQUEST (direct repair), not DIAGNOSTIC

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
            // LUỒNG MỚI: Allow multiple TaskAssignments per WorkOrderDetail
            // Removed NOT EXISTS check - TechManager can assign same detail multiple times
            String sql = "SELECT wod.DetailID as detailId, wod.WorkOrderID as workOrderId, " +
                    "wod.TaskDescription as taskDescription, " +
                    "wod.EstimateAmount as estimateAmount, wod.approved_at as approvedAt, " +
                    "wod.diagnostic_id as diagnosticId, wod.source as source, " +
                    "v.VehicleID as vehicleId, v.LicensePlate as licensePlate, v.Model as vehicleModel, " +
                    "u.FullName as customerName, u.PhoneNumber as phoneNumber, " +
                    "COALESCE((SELECT COUNT(*) FROM TaskAssignment ta WHERE ta.DetailID = wod.DetailID AND ta.task_type = 'REPAIR'), 0) as totalAssignments, "
                    +
                    "COALESCE((SELECT COUNT(*) FROM TaskAssignment ta WHERE ta.DetailID = wod.DetailID AND ta.task_type = 'REPAIR' AND ta.Status IN ('ASSIGNED', 'IN_PROGRESS')), 0) as activeTasks "
                    +
                    "FROM WorkOrderDetail wod " +
                    "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                    "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                    "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                    "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                    "JOIN User u ON c.UserID = u.UserID " +
                    "WHERE wod.approval_status = 'APPROVED' " +
                    "AND wod.source = 'REQUEST' " + // Only REQUEST source for direct repair assignment
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
                    Repair.setTotalAssignments(rs.getInt("totalAssignments"));
                    Repair.setActiveTasks(rs.getInt("activeTasks"));

                    // Load existing assignments for this detail
                    TaskAssignmentDAO taskDAO = new TaskAssignmentDAO();
                    Repair.setExistingAssignments(taskDAO.getTaskAssignmentSummaryByDetailId(detailId));

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
     * @deprecated Use createRepairTask(int, int, String, LocalDateTime,
     *             LocalDateTime) for
     *             new workflow
     */
    @Deprecated
    public boolean createRepairTask(int detailId, int technicianId) throws SQLException {
        return createRepairTask(detailId, technicianId, "Task assignment", null, null);
    }

    /**
     * Create repair task assignment with scheduling support
     * 
     * @param detailId        WorkOrderDetail ID
     * @param technicianId    Technician Employee ID
     * @param taskDescription Specific task description for this technician
     * @param plannedStart    Scheduled start time (optional)
     * @param plannedEnd      Scheduled end time (optional)
     */
    public boolean createRepairTask(int detailId, int technicianId, String taskDescription,
            java.time.LocalDateTime plannedStart,
            java.time.LocalDateTime plannedEnd) throws SQLException {
        String sql = "INSERT INTO TaskAssignment " +
                "(DetailID, AssignToTechID, TaskDescription, task_type, Status, AssignedDate, planned_start, planned_end) "
                +
                "VALUES (?, ?, ?, 'REPAIR', 'ASSIGNED', NOW(), ?, ?)";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detailId);
            ps.setInt(2, technicianId);
            ps.setString(3, taskDescription);
            ps.setString(3, taskDescription);

            // Set scheduling times (nullable)
            if (plannedStart != null) {
                ps.setObject(4, plannedStart);
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }

            if (plannedEnd != null) {
                ps.setObject(5, plannedEnd);
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
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

    /**
     * Get repair tasks that are currently IN_PROGRESS
     * Similar to diagnosis in-progress tasks
     */
    public List<InProgressRepairTask> getInProgressRepairTasks(int techManagerId) throws SQLException {
        List<InProgressRepairTask> tasks = new ArrayList<>();
        String sql = "SELECT ta.AssignmentID, ta.DetailID, ta.AssignToTechID, ta.AssignedDate, ta.StartAt, " +
                "ta.TaskDescription, ta.Status, ta.priority, " +
                "wd.WorkOrderID, wd.TaskDescription AS WorkOrderDetailDescription, " +
                "wo.Status AS WorkOrderStatus, wo.CreatedAt AS WorkOrderCreatedAt, " +
                "v.LicensePlate, v.Brand, v.Model, " +
                "u_cust.FullName AS CustomerName, " +
                "tech_emp.EmployeeCode AS TechnicianCode, tech_user.FullName AS TechnicianName " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "JOIN Employee tech_emp ON ta.AssignToTechID = tech_emp.EmployeeID " +
                "JOIN User tech_user ON tech_emp.UserID = tech_user.UserID " +
                "WHERE wo.TechManagerID = ? " +
                "AND ta.task_type = 'REPAIR' " +
                "AND ta.Status = 'IN_PROGRESS' " +
                "ORDER BY ta.StartAt ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InProgressRepairTask task = new InProgressRepairTask();
                    task.setAssignmentId(rs.getInt("AssignmentID"));
                    task.setDetailId(rs.getInt("DetailID"));
                    task.setWorkOrderId(rs.getInt("WorkOrderID"));
                    task.setTechnicianId(rs.getInt("AssignToTechID"));
                    task.setTechnicianCode(rs.getString("TechnicianCode"));
                    task.setTechnicianName(rs.getString("TechnicianName"));
                    task.setAssignedDate(rs.getTimestamp("AssignedDate"));
                    task.setStartAt(rs.getTimestamp("StartAt"));
                    task.setTaskDescription(rs.getString("TaskDescription"));
                    task.setStatus(rs.getString("Status"));
                    task.setPriority(rs.getString("priority"));
                    task.setWorkOrderDetailDescription(rs.getString("WorkOrderDetailDescription"));
                    task.setVehicleInfo(rs.getString("LicensePlate") + " - " +
                            rs.getString("Brand") + " " + rs.getString("Model"));
                    task.setCustomerName(rs.getString("CustomerName"));
                    task.setWorkOrderCreatedAt(rs.getTimestamp("WorkOrderCreatedAt"));
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    /**
     * DTO for In-Progress Repair Tasks
     */
    public static class InProgressRepairTask {
        private int assignmentId;
        private int detailId;
        private int workOrderId;
        private int technicianId;
        private String technicianCode;
        private String technicianName;
        private java.sql.Timestamp assignedDate;
        private java.sql.Timestamp startAt;
        private String taskDescription;
        private String status;
        private String priority;
        private String workOrderDetailDescription;
        private String vehicleInfo;
        private String customerName;
        private java.sql.Timestamp workOrderCreatedAt;

        public int getAssignmentId() {
            return assignmentId;
        }

        public void setAssignmentId(int assignmentId) {
            this.assignmentId = assignmentId;
        }

        public int getDetailId() {
            return detailId;
        }

        public void setDetailId(int detailId) {
            this.detailId = detailId;
        }

        public int getWorkOrderId() {
            return workOrderId;
        }

        public void setWorkOrderId(int workOrderId) {
            this.workOrderId = workOrderId;
        }

        public int getTechnicianId() {
            return technicianId;
        }

        public void setTechnicianId(int technicianId) {
            this.technicianId = technicianId;
        }

        public String getTechnicianCode() {
            return technicianCode;
        }

        public void setTechnicianCode(String technicianCode) {
            this.technicianCode = technicianCode;
        }

        public String getTechnicianName() {
            return technicianName;
        }

        public void setTechnicianName(String technicianName) {
            this.technicianName = technicianName;
        }

        public java.sql.Timestamp getAssignedDate() {
            return assignedDate;
        }

        public void setAssignedDate(java.sql.Timestamp assignedDate) {
            this.assignedDate = assignedDate;
        }

        public java.sql.Timestamp getStartAt() {
            return startAt;
        }

        public void setStartAt(java.sql.Timestamp startAt) {
            this.startAt = startAt;
        }

        public String getTaskDescription() {
            return taskDescription;
        }

        public void setTaskDescription(String taskDescription) {
            this.taskDescription = taskDescription;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getWorkOrderDetailDescription() {
            return workOrderDetailDescription;
        }

        public void setWorkOrderDetailDescription(String workOrderDetailDescription) {
            this.workOrderDetailDescription = workOrderDetailDescription;
        }

        public String getVehicleInfo() {
            return vehicleInfo;
        }

        public void setVehicleInfo(String vehicleInfo) {
            this.vehicleInfo = vehicleInfo;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public java.sql.Timestamp getWorkOrderCreatedAt() {
            return workOrderCreatedAt;
        }

        public void setWorkOrderCreatedAt(java.sql.Timestamp workOrderCreatedAt) {
            this.workOrderCreatedAt = workOrderCreatedAt;
        }
    }
}

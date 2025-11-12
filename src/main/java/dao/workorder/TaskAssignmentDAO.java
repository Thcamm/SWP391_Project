package dao.workorder;

import common.DbContext;
import model.employee.technician.TaskAssignment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing TaskAssignment (phân công công việc cho Technician)
 * 
 * SHARED DAO - Used by BOTH TechManager and Technician actors
 * 
 * Methods marked [TECH_MANAGER ONLY] - Only TechManager should call
 * Methods marked [SHARED] - Both TechManager and Technician can use
 * 
 * @see TechnicianDAO for Technician-specific operations
 */
public class TaskAssignmentDAO extends DbContext {

    // ===== TECH MANAGER SPECIFIC METHODS =====

    /**
     * [TECH_MANAGER ONLY]
     * Create a new TaskAssignment
     * Used when TechManager assigns a task to a Technician
     * 
     * @param task TaskAssignment object with details
     * @return Generated AssignmentID, or -1 if failed
     */
    public int createTaskAssignment(TaskAssignment task) throws SQLException {
        String sql = "INSERT INTO TaskAssignment (DetailID, AssignToTechID, AssignedDate, TaskDescription, task_type, Status, priority, notes, planned_start, planned_end) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, task.getDetailID());
            ps.setInt(2, task.getAssignToTechID());

            // AssignedDate
            if (task.getAssignedDate() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(task.getAssignedDate()));
            } else {
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            }

            ps.setString(4, task.getTaskDescription());
            ps.setString(5, task.getTaskType().name());
            ps.setString(6, task.getStatus().name());

            // Priority
            if (task.getPriority() != null) {
                ps.setString(7, task.getPriority().name());
            } else {
                ps.setString(7, TaskAssignment.Priority.MEDIUM.name());
            }

            ps.setString(8, task.getNotes());

            // NEW: Scheduling fields (nullable)
            if (task.getPlannedStart() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(task.getPlannedStart()));
            } else {
                ps.setNull(9, java.sql.Types.TIMESTAMP);
            }

            if (task.getPlannedEnd() != null) {
                ps.setTimestamp(10, Timestamp.valueOf(task.getPlannedEnd()));
            } else {
                ps.setNull(10, java.sql.Types.TIMESTAMP);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;
        }
    }

    // ===== SHARED METHODS - Both TechManager & Technician use =====

    /**
     * [SHARED]
     * Get TaskAssignments by WorkOrder (via WorkOrderDetail)
     * Used by: TechManager (review all tasks), Technician (view workorder tasks)
     */
    public List<TaskAssignment> getTaskAssignmentsByWorkOrder(int workOrderId) throws SQLException {
        List<TaskAssignment> tasks = new ArrayList<>();
        String sql = "SELECT ta.*, " +
                "wd.TaskDescription AS WorkOrderDetailDesc, " +
                "wd.EstimateHours, " +
                "e.EmployeeCode, u.FullName AS TechnicianName " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User u ON e.UserID = u.UserID " +
                "WHERE wd.WorkOrderID = ? " +
                "ORDER BY ta.priority DESC, ta.AssignedDate ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workOrderId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    /**
     * [SHARED]
     * Get TaskAssignments by type (DIAGNOSIS or REPAIR)
     * Used by: TechManager (filter by task type), Technician (view specific type)
     */
    public List<TaskAssignment> getTaskAssignmentsByType(int workOrderId, String taskType) throws SQLException {
        List<TaskAssignment> tasks = new ArrayList<>();
        String sql = "SELECT ta.*, " +
                "wd.TaskDescription AS WorkOrderDetailDesc, " +
                "wd.EstimateHours, " +
                "e.EmployeeCode, u.FullName AS TechnicianName " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User u ON e.UserID = u.UserID " +
                "WHERE wd.WorkOrderID = ? AND ta.task_type = ? " +
                "ORDER BY ta.priority DESC, ta.AssignedDate ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workOrderId);
            ps.setString(2, taskType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    /**
     * [SHARED]
     * Get TaskAssignment by ID with full details (Vehicle, Customer, Services)
     * Used by: TechManager (review task detail), Technician (view task detail)
     * 
     * @return TaskAssignment with vehicleInfo, customerName, serviceInfo populated
     */
    public TaskAssignment getTaskById(int assignmentId) throws SQLException {
        String sql = "SELECT ta.*, " +
                "wd.TaskDescription AS WorkOrderDetailDesc, " +
                "wd.EstimateHours, " +
                "CONCAT(v.LicensePlate, ' - ', v.Brand, ' ', v.Model) AS VehicleInfo, " +
                "GROUP_CONCAT(DISTINCT st.ServiceName SEPARATOR ', ') AS ServiceNames, " +
                "u.FullName as CustomerName " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "LEFT JOIN ServiceRequestDetail srd ON srd.RequestID = sr.RequestID " +
                "LEFT JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN `User` u ON c.UserID = u.UserID " +
                "WHERE ta.AssignmentID = ? " +
                "GROUP BY ta.AssignmentID";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);
                    task.setServiceInfo(rs.getString("ServiceNames"));
                    task.setVehicleInfo(rs.getString("VehicleInfo"));
                    task.setCustomerName(rs.getString("CustomerName"));
                    return task;
                }
            }
        }
        return null;
    }

    /**
     * [SHARED]
     * Update task status
     * Used by: TechManager (approve/reject), Technician (start/complete)
     * 
     * Auto-sets timestamps:
     * - StartAt when status = IN_PROGRESS
     * - CompleteAt when status = COMPLETE
     */
    public boolean updateTaskStatus(int assignmentId, TaskAssignment.TaskStatus status) throws SQLException {
        String sql = "UPDATE TaskAssignment SET Status = ?, " +
                "StartAt = CASE WHEN ? = 'IN_PROGRESS' AND StartAt IS NULL THEN NOW() ELSE StartAt END, " +
                "CompleteAt = CASE WHEN ? = 'COMPLETE' THEN NOW() ELSE CompleteAt END " +
                "WHERE AssignmentID = ?";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, status.name());
            ps.setString(3, status.name());
            ps.setInt(4, assignmentId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * [TECH_MANAGER ONLY]
     * LUỒNG MỚI - GĐ 3: Get WorkOrderDetails that need diagnosis assignment
     * 
     * REFACTORED: Changed from source='REQUEST' to source='DIAGNOSTIC'
     * - After Triage, only DIAGNOSTIC services need diagnosis
     * - REQUEST services skip diagnosis and go directly to repair
     * 
     * These are details from DIAGNOSTIC source that don't have a DIAGNOSIS task yet
     */
    public List<WorkOrderDetailWithInfo> getWorkOrderDetailsNeedingDiagnosisAssignment(int techManagerId)
            throws SQLException {
        List<WorkOrderDetailWithInfo> details = new ArrayList<>();
        String sql = "SELECT wd.*, wo.WorkOrderID, wo.Status AS WorkOrderStatus, wo.CreatedAt AS WorkOrderCreatedAt, " +
                "v.LicensePlate, v.Brand, v.Model, " +
                "u.FullName AS CustomerName, " +
                "COUNT(ta.AssignmentID) AS AssignmentCount " +
                "FROM WorkOrderDetail wd " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "LEFT JOIN TaskAssignment ta ON wd.DetailID = ta.DetailID AND ta.task_type = 'DIAGNOSIS' " +
                "WHERE wo.TechManagerID = ? " +
                "AND wd.source = 'DIAGNOSTIC' " + // CHANGED: Only DIAGNOSTIC needs diagnosis
                "AND (wo.Status = 'PENDING' OR wo.Status = 'IN_PROCESS') " +
                "GROUP BY wd.DetailID " +
                "HAVING AssignmentCount = 0 " +
                "ORDER BY wo.CreatedAt ASC";

        System.out.println(
                "=== [TaskAssignmentDAO] Querying pending diagnosis tasks for TechManager #" + techManagerId + " ===");

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    WorkOrderDetailWithInfo detail = new WorkOrderDetailWithInfo();
                    detail.setDetailId(rs.getInt("DetailID"));
                    detail.setWorkOrderId(rs.getInt("WorkOrderID"));
                    detail.setTaskDescription(rs.getString("TaskDescription"));
                    detail.setEstimateHours(rs.getBigDecimal("EstimateHours"));

                    // Vehicle info
                    String vehicleInfo = String.format("%s - %s %s",
                            rs.getString("LicensePlate"),
                            rs.getString("Brand"),
                            rs.getString("Model"));
                    detail.setVehicleInfo(vehicleInfo);
                    detail.setCustomerName(rs.getString("CustomerName"));
                    detail.setWorkOrderCreatedAt(rs.getTimestamp("WorkOrderCreatedAt"));

                    count++;
                    System.out.println("[Result #" + count + "] DetailID=" + detail.getDetailId() +
                            ", WorkOrderID=" + detail.getWorkOrderId() +
                            ", Status=" + rs.getString("WorkOrderStatus") +
                            ", Vehicle=" + vehicleInfo);

                    details.add(detail);
                }
                System.out.println("✓ Total pending diagnosis tasks found: " + count);
            }
        }
        return details;
    }

    // =========================================================================
    // UC-TM-05, UC-TM-06, UC-TM-07: OVERDUE, DECLINED, REASSIGN MANAGEMENT
    // =========================================================================

    /**
     * [UC-TM-05] Get overdue tasks (SLA violations)
     * Tasks where planned_start < NOW() AND StartAt IS NULL AND Status = 'ASSIGNED'
     * 
     * @return List of overdue TaskAssignment with vehicle/customer info
     */
    public List<TaskAssignment> getOverdueTasks() throws SQLException {
        String sql = "SELECT ta.*, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "u.FullName AS TechnicianName, " +
                "cust_u.FullName AS CustomerName, " +
                "wo.WorkOrderID, " +
                "TIMESTAMPDIFF(HOUR, ta.planned_start, NOW()) AS HoursOverdue " +
                "FROM TaskAssignment ta " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User u ON e.UserID = u.UserID " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User cust_u ON c.UserID = cust_u.UserID " +
                "WHERE ta.Status = 'ASSIGNED' " +
                "  AND ta.planned_start < NOW() " +
                "  AND ta.StartAt IS NULL " +
                "ORDER BY ta.planned_start ASC";

        List<TaskAssignment> tasks = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaskAssignment task = mapResultSetToTask(rs);
                task.setVehicleInfo(rs.getString("VehicleInfo"));
                task.setCustomerName(rs.getString("CustomerName"));
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * [UC-TM-06] Get tasks declined by technicians
     * Tasks where Status = 'CANCELLED' AND decline_reason IS NOT NULL
     * 
     * @return List of declined TaskAssignment with decline info
     */
    public List<TaskAssignment> getDeclinedTasks() throws SQLException {
        String sql = "SELECT ta.*, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "u.FullName AS TechnicianName, " +
                "cust_u.FullName AS CustomerName, " +
                "wo.WorkOrderID " +
                "FROM TaskAssignment ta " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User u ON e.UserID = u.UserID " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User cust_u ON c.UserID = cust_u.UserID " +
                "WHERE ta.Status = 'CANCELLED' " +
                "  AND ta.decline_reason IS NOT NULL " +
                "ORDER BY ta.declined_at DESC";

        List<TaskAssignment> tasks = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaskAssignment task = mapResultSetToTask(rs);
                task.setVehicleInfo(rs.getString("VehicleInfo"));
                task.setCustomerName(rs.getString("CustomerName"));
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * [UC-TM-07] Get all cancelled tasks that need reassignment
     * Tasks where Status = 'CANCELLED' (both overdue and declined)
     * 
     * @return List of cancelled TaskAssignment
     */
    public List<TaskAssignment> getCancelledTasksForReassignment() throws SQLException {
        String sql = "SELECT ta.*, " +
                "CONCAT(v.Brand, ' ', v.Model, ' - ', v.LicensePlate) AS VehicleInfo, " +
                "u.FullName AS PreviousTechnicianName, " +
                "cust_u.FullName AS CustomerName, " +
                "wo.WorkOrderID, " +
                "CASE " +
                "  WHEN ta.decline_reason IS NOT NULL THEN 'DECLINED' " +
                "  ELSE 'OVERDUE' " +
                "END AS CancelReason " +
                "FROM TaskAssignment ta " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User u ON e.UserID = u.UserID " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User cust_u ON c.UserID = cust_u.UserID " +
                "WHERE ta.Status = 'CANCELLED' " +
                "ORDER BY ta.AssignedDate DESC";

        List<TaskAssignment> tasks = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaskAssignment task = mapResultSetToTask(rs);
                task.setVehicleInfo(rs.getString("VehicleInfo"));
                task.setCustomerName(rs.getString("CustomerName"));
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * [UC-TM-05] Cancel an overdue task
     * Updates Status to 'CANCELLED' and sets cancel note
     * 
     * @param assignmentId Task to cancel
     * @param cancelNote   Reason for cancellation
     * @return true if successful
     */
    public boolean cancelOverdueTask(int assignmentId, String cancelNote) throws SQLException {
        String sql = "UPDATE TaskAssignment " +
                "SET Status = 'CANCELLED', " +
                "    notes = CONCAT(IFNULL(notes, ''), '\n[CANCELLED BY TM]: ', ?) " +
                "WHERE AssignmentID = ? " +
                "  AND Status = 'ASSIGNED'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cancelNote);
            ps.setInt(2, assignmentId);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * [UC-TM-07] Reassign a cancelled task to a new technician
     * Updates assignee, resets status to ASSIGNED, clears decline info, sets new
     * schedule
     * 
     * @param assignmentId    Task to reassign
     * @param newTechnicianId New technician ID
     * @param plannedStart    New planned start time
     * @param plannedEnd      New planned end time
     * @return true if successful
     */
    public boolean reassignTask(int assignmentId, int newTechnicianId,
            LocalDateTime plannedStart, LocalDateTime plannedEnd) throws SQLException {
        String sql = "UPDATE TaskAssignment " +
                "SET AssignToTechID = ?, " +
                "    Status = 'ASSIGNED', " +
                "    declined_at = NULL, " +
                "    decline_reason = NULL, " +
                "    planned_start = ?, " +
                "    planned_end = ?, " +
                "    AssignedDate = NOW() " +
                "WHERE AssignmentID = ? " +
                "  AND Status = 'CANCELLED'";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newTechnicianId);
            ps.setTimestamp(2, Timestamp.valueOf(plannedStart));
            ps.setTimestamp(3, Timestamp.valueOf(plannedEnd));
            ps.setInt(4, assignmentId);

            return ps.executeUpdate() > 0;
        }
    }

    // Helper method to map ResultSet to TaskAssignment
    private TaskAssignment mapResultSetToTask(ResultSet rs) throws SQLException {
        TaskAssignment task = new TaskAssignment();

        task.setAssignmentID(rs.getInt("AssignmentID"));
        task.setDetailID(rs.getInt("DetailID"));
        task.setAssignToTechID(rs.getInt("AssignToTechID"));

        Timestamp assignedDate = rs.getTimestamp("AssignedDate");
        if (assignedDate != null) {
            task.setAssignedDate(assignedDate.toLocalDateTime());
        }

        Timestamp startAt = rs.getTimestamp("StartAt");
        if (startAt != null) {
            task.setStartAt(startAt.toLocalDateTime());
        }

        Timestamp completeAt = rs.getTimestamp("CompleteAt");
        if (completeAt != null) {
            task.setCompleteAt(completeAt.toLocalDateTime());
        }

        task.setTaskDescription(rs.getString("TaskDescription"));
        task.setTaskType(rs.getString("task_type"));
        task.setPriority(rs.getString("priority"));
        task.setStatus(rs.getString("Status"));

        int progressPercentage = rs.getInt("progress_percentage");
        if (!rs.wasNull()) {
            task.setProgressPercentage(progressPercentage);
        }

        task.setNotes(rs.getString("notes"));

        return task;
    }

    /**
     * Inner class for WorkOrderDetail with additional info
     * Used for diagnosis assignment screen
     */
    public static class WorkOrderDetailWithInfo {
        private int detailId;
        private int workOrderId;
        private String taskDescription;
        private java.math.BigDecimal estimateHours;
        private String vehicleInfo;
        private String customerName;
        private Timestamp workOrderCreatedAt;

        // Getters and Setters
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

        public String getTaskDescription() {
            return taskDescription;
        }

        public void setTaskDescription(String taskDescription) {
            this.taskDescription = taskDescription;
        }

        public java.math.BigDecimal getEstimateHours() {
            return estimateHours;
        }

        public void setEstimateHours(java.math.BigDecimal estimateHours) {
            this.estimateHours = estimateHours;
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

        public Timestamp getWorkOrderCreatedAt() {
            return workOrderCreatedAt;
        }

        public void setWorkOrderCreatedAt(Timestamp workOrderCreatedAt) {
            this.workOrderCreatedAt = workOrderCreatedAt;
        }
    }

    // =========================================================================
    // GET IN-PROGRESS DIAGNOSIS TASKS
    // =========================================================================

    /**
     * Get diagnosis tasks that are currently IN_PROGRESS for a specific
     * TechManager.
     * These are tasks that have been assigned and technicians are actively working
     * on.
     * 
     * @param techManagerId the Tech Manager's employee ID
     * @return list of in-progress diagnosis tasks with full details
     * @throws SQLException if database error occurs
     */
    public List<InProgressDiagnosisTask> getInProgressDiagnosisTasks(int techManagerId) throws SQLException {
        List<InProgressDiagnosisTask> tasks = new ArrayList<>();
        String sql = "SELECT ta.AssignmentID, ta.DetailID, ta.AssignToTechID, ta.AssignedDate, ta.StartAt, " +
                "ta.TaskDescription, ta.Status, " +
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
                "AND ta.task_type = 'DIAGNOSIS' " +
                "AND ta.Status = 'IN_PROGRESS' " +
                "ORDER BY ta.StartAt ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InProgressDiagnosisTask task = new InProgressDiagnosisTask();
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
     * DTO for In-Progress Diagnosis Tasks
     */
    public static class InProgressDiagnosisTask {
        private int assignmentId;
        private int detailId;
        private int workOrderId;
        private int technicianId;
        private String technicianCode;
        private String technicianName;
        private Timestamp assignedDate;
        private Timestamp startAt;
        private String taskDescription;
        private String status;
        private String workOrderDetailDescription;
        private String vehicleInfo;
        private String customerName;
        private Timestamp workOrderCreatedAt;

        // Getters and Setters
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

        public Timestamp getAssignedDate() {
            return assignedDate;
        }

        public void setAssignedDate(Timestamp assignedDate) {
            this.assignedDate = assignedDate;
        }

        public Timestamp getStartAt() {
            return startAt;
        }

        public void setStartAt(Timestamp startAt) {
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

        public String getWorkOrderDetailDescription() {
            return workOrderDetailDescription;
        }

        public void setWorkOrderDetailDescription(String desc) {
            this.workOrderDetailDescription = desc;
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

        public Timestamp getWorkOrderCreatedAt() {
            return workOrderCreatedAt;
        }

        public void setWorkOrderCreatedAt(Timestamp workOrderCreatedAt) {
            this.workOrderCreatedAt = workOrderCreatedAt;
        }
    }
}

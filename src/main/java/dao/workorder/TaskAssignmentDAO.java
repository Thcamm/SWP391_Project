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
// * @see TechnicianDAO for Technician-specific operations
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
        String sql = "INSERT INTO TaskAssignment (DetailID, AssignToTechID, AssignedDate, TaskDescription, task_type, Status, priority, notes) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
    public List<TaskAssignment> getTasksByDetailId(int detailID) throws SQLException {
        List<TaskAssignment> tasks = new ArrayList<>();
        String sql = "SELECT * FROM TaskAssignment WHERE DetailID = ? ORDER BY priority DESC, AssignedDate ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, detailID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);
                    tasks.add(task);
                }
            }
        }
        return tasks;
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
     * [TECH_MANAGER ONLY]
     * Get completed DIAGNOSIS tasks for TechManager to review
     * Returns tasks that need to be reviewed and converted to quotes
     */
    public List<TaskAssignment> getCompletedDiagnosisTasksForTechManager(int techManagerId) throws SQLException {
        List<TaskAssignment> tasks = new ArrayList<>();
        String sql = "SELECT ta.*, " +
                "wd.TaskDescription AS WorkOrderDetailDesc, " +
                "wd.EstimateHours, " +
                "v.LicensePlate, v.Brand, v.Model, " +
                "u.FullName AS CustomerName, " +
                "tech_user.FullName AS TechnicianName " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN User u ON c.UserID = u.UserID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User tech_user ON e.UserID = tech_user.UserID " +
                "WHERE wo.TechManagerID = ? " +
                "AND ta.task_type = 'DIAGNOSIS' " +
                "AND ta.Status = 'COMPLETE' " +
                "ORDER BY ta.CompleteAt DESC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);

                    // Add vehicle info
                    String vehicleInfo = String.format("%s - %s %s",
                            rs.getString("LicensePlate"),
                            rs.getString("Brand"),
                            rs.getString("Model"));
                    task.setVehicleInfo(vehicleInfo);
                    task.setCustomerName(rs.getString("CustomerName"));

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
     * Get WorkOrderDetails that need diagnosis assignment
     * These are details from REQUEST source that don't have a DIAGNOSIS task yet
     */
    public List<WorkOrderDetailWithInfo> getWorkOrderDetailsNeedingDiagnosisAssignment(int techManagerId)
            throws SQLException {
        List<WorkOrderDetailWithInfo> details = new ArrayList<>();
        String sql = "SELECT wd.*, wo.WorkOrderID, wo.CreatedAt AS WorkOrderCreatedAt, " +
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
                "AND wd.source = 'REQUEST' " +
                "AND wo.Status = 'IN_PROCESS' " +
                "GROUP BY wd.DetailID " +
                "HAVING AssignmentCount = 0 " +
                "ORDER BY wo.CreatedAt ASC";

        try (Connection conn = DbContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, techManagerId);

            try (ResultSet rs = ps.executeQuery()) {
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

                    details.add(detail);
                }
            }
        }
        return details;
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
}

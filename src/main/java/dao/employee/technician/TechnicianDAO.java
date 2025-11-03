package dao.employee.technician;

import common.DbContext;
import model.employee.Employee;
import model.employee.technician.TaskAssignment;
import model.employee.technician.TaskStatistics;
import model.employee.technician.TechnicianActivity;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TechnicianDAO {

    public Employee getTechnicianByUserId(int userId) {
        String sql = "SELECT e.*, u.FullName, u.UserName, u.Email, u.PhoneNumber, u.Gender " +
                "FROM Employee e " +
                "JOIN `User` u ON e.UserID = u.UserID " +
                "WHERE e.UserID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("EmployeeID"));
        employee.setUserId(rs.getInt("UserID"));
        employee.setEmployeeCode(rs.getString("EmployeeCode"));

        int managerBy = rs.getInt("ManagedBy");
        if (!rs.wasNull()) {
            employee.setManagedBy(managerBy);
        }

        int createdBy = rs.getInt("CreatedBy");
        if (!rs.wasNull()) {
            employee.setCreateBy(createdBy);
        }

        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            employee.setCreatedAt(Timestamp.valueOf(createdAt.toLocalDateTime()));
        }

        employee.setFullName(rs.getString("FullName"));
        employee.setUserName(rs.getString("UserName"));
        employee.setEmail(rs.getString("Email"));
        employee.setPhoneNumber(rs.getString("PhoneNumber"));
        employee.setGender(rs.getString("Gender"));

        return employee;
    }

    public TaskStatistics getTaskStatistics(int technicianId) {
        TaskStatistics stats = new TaskStatistics();

        String sql = "SELECT " +
                "COUNT(*) as total_tasks, " +
                "SUM(CASE WHEN ta.Status = 'ASSIGNED' THEN 1 ELSE 0 END) as new_tasks, " +
                "SUM(CASE WHEN ta.Status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress, " +
                "SUM(CASE WHEN ta.Status = 'COMPLETE' AND DATE(ta.CompleteAt) = CURDATE() THEN 1 ELSE 0 END) as completed_today " +
                "FROM TaskAssignment ta " +
                "WHERE ta.AssignToTechID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalTasksCount(rs.getInt("total_tasks"));
                    stats.setNewTasksCount(rs.getInt("new_tasks"));
                    stats.setInProgressCount(rs.getInt("in_progress"));
                    stats.setCompletedTodayCount(rs.getInt("completed_today"));
                }
            }

            // pending parts (separate query)
            stats.setPendingPartsCount(countPendingParts(technicianId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    private int countPendingParts(int technicianId) {
        String sql = "SELECT COUNT(DISTINCT wop.WorkOrderPartID) as pending_parts " +
                "FROM WorkOrderPart wop " +
                "WHERE wop.RequestedByID = ? AND wop.request_status = 'PENDING'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("pending_parts");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * NOTE: adjusted to join ServiceRequestDetail (srd) because ServiceRequest no longer has ServiceID
     * and a request may have multiple services. We GROUP_CONCAT service names and GROUP BY ta.AssignmentID.
     */
    public List<TaskAssignment> getNewAssignedTasks(int technicianId) {
        List<TaskAssignment> tasks = new ArrayList<>();
        String sql = "SELECT ta.*, " +
                "wd.TaskDescription AS WorkOrderDetailDesc, " +
                "wd.EstimateHours, " +
                "CONCAT(v.LicensePlate, ' - ', v.Brand, ' ', v.Model) AS VehicleInfo, " +
                "GROUP_CONCAT(DISTINCT st.ServiceName SEPARATOR ', ') AS ServiceNames, " +
                "u.FullName AS CustomerName " +
                "FROM TaskAssignment ta " +
                "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "LEFT JOIN ServiceRequestDetail srd ON srd.RequestID = sr.RequestID " +
                "LEFT JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "JOIN `User` u ON c.UserID = u.UserID " +
                "WHERE ta.AssignToTechID = ? AND ta.Status = 'ASSIGNED' " +
                "GROUP BY ta.AssignmentID " +
                "ORDER BY ta.priority DESC, ta.AssignedDate DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);
                    // set service info from aggregated column
                    task.setServiceInfo(rs.getString("ServiceNames"));
                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    public TaskAssignment getTaskById(int assignmentId) {
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
                    return task;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }

        return null;
    }

    public List<TaskAssignment> getAllTasksWithFilter(
            int technicianId,
            String status,
            String priority,
            String search
    ) {
        StringBuilder sql = new StringBuilder(
                "SELECT ta.*, " +
                        "wd.TaskDescription AS WorkOrderDetailDesc,  " +
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
                        "WHERE ta.AssignToTechID = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(technicianId);
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND ta.Status = ? ");
            params.add(status);
        }

        if (priority != null && !priority.trim().isEmpty()) {
            sql.append("AND ta.priority = ? ");
            params.add(priority);
        }

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (v.LicensePlate LIKE ? OR v.Brand LIKE ? OR v.Model LIKE ? OR u.FullName LIKE ? OR st.ServiceName LIKE ?) ");
            String searchPattern = "%" + search + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        sql.append("GROUP BY ta.AssignmentID ");
        sql.append("ORDER BY ta.priority DESC, ta.AssignedDate DESC");

        List<TaskAssignment> tasks = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);
                    task.setServiceInfo(rs.getString("ServiceNames"));
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return tasks;

    }

    public int countAllTasksWithFilter(
            int technicianId,
            String status,
            String priority,
            String search
    ) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT ta.AssignmentID) AS total " +
                        "FROM TaskAssignment ta " +
                        "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                        "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "LEFT JOIN ServiceRequestDetail srd ON srd.RequestID = sr.RequestID " +
                        "LEFT JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                        "JOIN `User` u ON c.UserID = u.UserID " +
                        "WHERE ta.AssignToTechID = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(technicianId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND ta.Status = ? ");
            params.add(status);
        }
        if (priority != null && !priority.trim().isEmpty()) {
            sql.append("AND ta.Priority = ? ");
            params.add(priority);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (v.LicensePlate LIKE ? OR v.Brand LIKE ? OR v.Model LIKE ? OR u.FullName LIKE ? OR st.ServiceName LIKE ?) ");
            String pattern = "%" + search + "%";
            for (int i = 0; i < 5; i++) params.add(pattern);
        }

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<TaskAssignment> getAllTasksWithFilterPaged(
            int technicianId,
            String status,
            String priority,
            String search,
            int offset,
            int limit
    ) {
        StringBuilder sql = new StringBuilder(
                "SELECT ta.*, " +
                        "wd.TaskDescription AS WorkOrderDetailDesc, " +
                        "wd.EstimateHours, " +
                        "CONCAT(v.LicensePlate, ' - ', v.Brand, ' ', v.Model) AS VehicleInfo, " +
                        "GROUP_CONCAT(DISTINCT st.ServiceName SEPARATOR ', ') AS ServiceNames, " +
                        "u.FullName AS CustomerName " +
                        "FROM TaskAssignment ta " +
                        "JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                        "JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "LEFT JOIN ServiceRequestDetail srd ON srd.RequestID = sr.RequestID " +
                        "LEFT JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                        "JOIN `User` u ON c.UserID = u.UserID " +
                        "WHERE ta.AssignToTechID = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(technicianId);

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND ta.Status = ? ");
            params.add(status);
        }
        if (priority != null && !priority.trim().isEmpty()) {
            sql.append("AND ta.Priority = ? ");
            params.add(priority);
        }
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (v.LicensePlate LIKE ? OR v.Brand LIKE ? OR v.Model LIKE ? OR u.FullName LIKE ? OR st.ServiceName LIKE ?) ");
            String pattern = "%" + search + "%";
            for (int i = 0; i < 5; i++) params.add(pattern);
        }

        sql.append("GROUP BY ta.AssignmentID ");
        sql.append("ORDER BY ta.Priority DESC, ta.AssignedDate DESC ");
        sql.append("LIMIT ? OFFSET ?");

        params.add(limit);
        params.add(offset);

        List<TaskAssignment> tasks = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);
                    task.setServiceInfo(rs.getString("ServiceNames"));
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }


    public TaskStatistics getAllTasksStatistics(int technicianId) {
        TaskStatistics stats = new TaskStatistics();
        String sql = "SELECT " +
                "COUNT(*) as total_tasks, " +
                "SUM(CASE WHEN ta.Status = 'ASSIGNED' THEN 1 ELSE 0 END) as assigned_count, " +
                "SUM(CASE WHEN ta.Status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress_count, " +
                "SUM(CASE WHEN ta.Status = 'COMPLETE' THEN 1 ELSE 0 END) as completed_count " +
                "FROM TaskAssignment ta " +
                "WHERE ta.AssignToTechID = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalTasksCount(rs.getInt("total_tasks"));
                    stats.setNewTasksCount(rs.getInt("assigned_count"));
                    stats.setInProgressCount(rs.getInt("in_progress_count"));
                    stats.setCompletedTodayCount(rs.getInt("completed_count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    public List<TaskAssignment> getInProgressTasks(int technicianId) {
        List<TaskAssignment> tasks = new ArrayList<>();

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
                "WHERE ta.AssignToTechID = ? AND ta.Status = 'IN_PROGRESS' " +
                "GROUP BY ta.AssignmentID " +
                "ORDER BY ta.StartAt ASC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TaskAssignment task = mapResultSetToTask(rs);
                    task.setServiceInfo(rs.getString("ServiceNames"));
                    tasks.add(task);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }

        return tasks;
    }

    private TaskAssignment mapResultSetToTask(ResultSet rs) throws SQLException {
        TaskAssignment task = new TaskAssignment();

        task.setAssignmentID(rs.getInt("AssignmentID"));
        task.setDetailID(rs.getInt("DetailID"));
        task.setAssignToTechID(rs.getInt("AssignToTechID"));

        Timestamp assignedDate = rs.getTimestamp("AssignedDate");
        if (assignedDate != null) {
            task.setAssignedDate(assignedDate.toLocalDateTime());
        }

        Timestamp startAtTs = rs.getTimestamp("StartAt");
        if (startAtTs != null) {
            task.setStartAt(startAtTs.toLocalDateTime());
        }

        Timestamp completeAtTs = rs.getTimestamp("CompleteAt");
        if (completeAtTs != null) {
            task.setCompleteAt(completeAtTs.toLocalDateTime());
        }

        task.setTaskDescription(rs.getString("TaskDescription"));
        task.setTaskType(rs.getString("task_type"));
        task.setPriority(rs.getString("priority"));
        task.setStatus(rs.getString("Status"));
        task.setProgressPercentage(rs.getInt("progress_percentage"));
        task.setNotes(rs.getString("notes"));

        // Additional display info
        task.setVehicleInfo(rs.getString("VehicleInfo"));
        // ServiceInfo is aggregated into ServiceNames alias
        task.setServiceInfo(rs.getString("ServiceNames"));
        task.setCustomerName(rs.getString("CustomerName"));
        task.setEstimateHours(rs.getDouble("EstimateHours"));

        return task;
    }

    public boolean updateTaskStatus(int assignmentId, TaskAssignment.TaskStatus status, LocalDateTime startTime) {
        String sql = "UPDATE TaskAssignment SET Status = ?, StartAt = ? WHERE AssignmentID = ? ";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());

            if (startTime != null) {
                ps.setTimestamp(2, Timestamp.valueOf(startTime));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }

            ps.setInt(3, assignmentId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTaskProgress(int assignmentId, int progressPercentage, String notes) {
        String sql = "UPDATE TaskAssignment SET progress_percentage = ?, notes = ? WHERE AssignmentID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progressPercentage);
            ps.setString(2, notes);
            ps.setInt(3, assignmentId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean completeTask(int assignmentId, String notes) {
        String sql = "UPDATE TaskAssignment SET Status = 'COMPLETE', CompleteAt = ?, " +
                "progress_percentage = 100, notes = ? WHERE AssignmentID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, notes);
            ps.setInt(3, assignmentId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Activity log
    public boolean logActivity(int technicianId, TechnicianActivity.ActivityType activityType,
                               Integer taskAssignmentId, String description) {
        String sql = "INSERT INTO TechnicianActivityLog (TechnicianID, ActivityType, TaskAssignmentID, Description) " +
                "VALUES (?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setString(2, activityType.name());
            if (taskAssignmentId != null) {
                ps.setInt(3, taskAssignmentId);
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, description);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<TechnicianActivity> getRecentActivities(int technicianId, int limit) {
        List<TechnicianActivity> activities = new ArrayList<>();
        String sql = "SELECT tal.*, " +
                "CONCAT (v.LicensePlate, ' - ', v.Brand, ' ', v.Model) AS VehicleInfo, " +
                "GROUP_CONCAT(DISTINCT st.ServiceName SEPARATOR ', ') AS ServiceNames " +
                "FROM TechnicianActivityLog tal " +
                "LEFT JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                "LEFT JOIN WorkOrderDetail wd ON ta.DetailID = wd.DetailID " +
                "LEFT JOIN WorkOrder wo ON wd.WorkOrderID = wo.WorkOrderID " +
                "LEFT JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "LEFT JOIN ServiceRequestDetail srd ON srd.RequestID = sr.RequestID " +
                "LEFT JOIN Service_Type st ON srd.ServiceID = st.ServiceID " +
                "LEFT JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "WHERE tal.TechnicianID = ? " +
                "GROUP BY tal.ActivityID " +
                "ORDER BY tal.ActivityTime DESC " +
                "LIMIT ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicianActivity activity = mapResultSetToActivity(rs);
                    activity.setTaskInfo(rs.getString("ServiceNames"));
                    activities.add(activity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }

        return activities;
    }

    private TechnicianActivity mapResultSetToActivity(ResultSet rs) throws SQLException {
        TechnicianActivity activity = new TechnicianActivity();

        activity.setActivityID(rs.getInt("ActivityID"));
        activity.setTechnicianID(rs.getInt("TechnicianID"));
        activity.setActivityType(rs.getString("ActivityType"));

        int taskId = rs.getInt("TaskAssignmentID");
        if (!rs.wasNull()) {
            activity.setTaskAssignmentID(taskId);
        }

        activity.setDescription(rs.getString("Description"));

        Timestamp activityTimeTs = rs.getTimestamp("ActivityTime");
        if (activityTimeTs != null) {
            activity.setActivityTime(activityTimeTs.toLocalDateTime());
        }

        activity.setVehicleInfo(rs.getString("VehicleInfo"));
        activity.setTaskInfo(rs.getString("ServiceNames"));

        return activity;
    }

}
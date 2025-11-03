package dao.employee.technician;

import model.employee.technician.TechnicianActivity;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TechnicianActivityDAO {


    public int logActivity(Connection conn, int technicianId, String activityType,
                           Integer taskAssignmentId, String description) throws SQLException {
        String sql = "INSERT INTO TechnicianActivityLog " +
                "(TechnicianID, ActivityType, TaskAssignmentID, Description, ActivityTime) " +
                "VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, technicianId);
            ps.setString(2, activityType);

            if (taskAssignmentId != null && taskAssignmentId > 0) {
                ps.setInt(3, taskAssignmentId);
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setString(4, description);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }


    public int logActivity(Connection conn, int technicianId, String activityType,
                           Integer taskAssignmentId) throws SQLException {
        return logActivity(conn, technicianId, activityType, taskAssignmentId, null);
    }


    public int logActivitiesBatch(Connection conn, List<TechnicianActivity> activities)
            throws SQLException {
        String sql = "INSERT INTO TechnicianActivityLog " +
                "(TechnicianID, ActivityType, TaskAssignmentID, Description, ActivityTime) " +
                "VALUES (?, ?, ?, ?, NOW())";

        int count = 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (TechnicianActivity activity : activities) {
                ps.setInt(1, activity.getTechnicianID());
                ps.setString(2, activity.getActivityType().name());

                if (activity.getTaskAssignmentID() != null) {
                    ps.setInt(3, activity.getTaskAssignmentID());
                } else {
                    ps.setNull(3, Types.INTEGER);
                }

                ps.setString(4, activity.getDescription());
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            for (int result : results) {
                if (result > 0) count++;
            }
        }
        return count;
    }


    public TechnicianActivity getActivityById(Connection conn, int activityId)
            throws SQLException {
        String sql = "SELECT * FROM TechnicianActivityLog WHERE ActivityID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapActivity(rs);
                }
            }
        }
        return null;
    }


    public List<TechnicianActivity> getRecentActivities(Connection conn, int technicianId,
                                                        int page, int pageSize)
            throws SQLException {
        int offset = (page - 1) * pageSize;

        String sql =
                "SELECT " +
                        "    tal.*, " +
                        "    ta.TaskDescription, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model " +
                        "FROM TechnicianActivityLog tal " +
                        "LEFT JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                        "LEFT JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "LEFT JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "LEFT JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "LEFT JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "WHERE tal.TechnicianID = ? " +
                        "ORDER BY tal.ActivityTime DESC " +
                        "LIMIT ? OFFSET ?";

        List<TechnicianActivity> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setInt(2, pageSize);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicianActivity activity = mapActivity(rs);

                    // Set vehicle info (nếu có)
                    String licensePlate = rs.getString("LicensePlate");
                    if (licensePlate != null) {
                        String vehicleInfo = String.format("%s - %s %s",
                                licensePlate,
                                rs.getString("Brand"),
                                rs.getString("Model")
                        );
                        activity.setVehicleInfo(vehicleInfo);
                    }

                    // Set task info
                    activity.setTaskInfo(rs.getString("TaskDescription"));

                    list.add(activity);
                }
            }
        }
        return list;
    }


    public List<TechnicianActivity> getActivitiesByTask(Connection conn, int technicianId,
                                                        int taskAssignmentId)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    tal.*, " +
                        "    ta.TaskDescription, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model " +
                        "FROM TechnicianActivityLog tal " +
                        "LEFT JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                        "LEFT JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "LEFT JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "LEFT JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "LEFT JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "WHERE tal.TechnicianID = ? AND tal.TaskAssignmentID = ? " +
                        "ORDER BY tal.ActivityTime ASC";

        List<TechnicianActivity> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setInt(2, taskAssignmentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicianActivity activity = mapActivity(rs);

                    String licensePlate = rs.getString("LicensePlate");
                    if (licensePlate != null) {
                        String vehicleInfo = String.format("%s - %s %s",
                                licensePlate,
                                rs.getString("Brand"),
                                rs.getString("Model")
                        );
                        activity.setVehicleInfo(vehicleInfo);
                    }

                    activity.setTaskInfo(rs.getString("TaskDescription"));
                    list.add(activity);
                }
            }
        }
        return list;
    }

    public List<TechnicianActivity> getActivitiesByType(Connection conn, int technicianId,
                                                        String activityType, int limit)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    tal.*, " +
                        "    ta.TaskDescription, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model " +
                        "FROM TechnicianActivityLog tal " +
                        "LEFT JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                        "LEFT JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "LEFT JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "LEFT JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "LEFT JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "WHERE tal.TechnicianID = ? AND tal.ActivityType = ? " +
                        "ORDER BY tal.ActivityTime DESC " +
                        (limit > 0 ? "LIMIT ?" : "");

        List<TechnicianActivity> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setString(2, activityType);
            if (limit > 0) {
                ps.setInt(3, limit);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicianActivity activity = mapActivity(rs);

                    String licensePlate = rs.getString("LicensePlate");
                    if (licensePlate != null) {
                        String vehicleInfo = String.format("%s - %s %s",
                                licensePlate,
                                rs.getString("Brand"),
                                rs.getString("Model")
                        );
                        activity.setVehicleInfo(vehicleInfo);
                    }

                    activity.setTaskInfo(rs.getString("TaskDescription"));
                    list.add(activity);
                }
            }
        }
        return list;
    }

    public List<TechnicianActivity> getActivitiesByDateRange(Connection conn, int technicianId,
                                                             java.time.LocalDate fromDate,
                                                             java.time.LocalDate toDate)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    tal.*, " +
                        "    ta.TaskDescription, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model " +
                        "FROM TechnicianActivityLog tal " +
                        "LEFT JOIN TaskAssignment ta ON tal.TaskAssignmentID = ta.AssignmentID " +
                        "LEFT JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "LEFT JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "LEFT JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "LEFT JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "WHERE tal.TechnicianID = ? " +
                        "  AND DATE(tal.ActivityTime) >= ? " +
                        "  AND DATE(tal.ActivityTime) <= ? " +
                        "ORDER BY tal.ActivityTime DESC";

        List<TechnicianActivity> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(toDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TechnicianActivity activity = mapActivity(rs);

                    String licensePlate = rs.getString("LicensePlate");
                    if (licensePlate != null) {
                        String vehicleInfo = String.format("%s - %s %s",
                                licensePlate,
                                rs.getString("Brand"),
                                rs.getString("Model")
                        );
                        activity.setVehicleInfo(vehicleInfo);
                    }

                    activity.setTaskInfo(rs.getString("TaskDescription"));
                    list.add(activity);
                }
            }
        }
        return list;
    }

    // ================================================================
    // STATISTICS & COUNT OPERATIONS
    // ================================================================


    public int countActivities(Connection conn, int technicianId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM TechnicianActivityLog " +
                "WHERE TechnicianID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }


    public int countActivitiesByType(Connection conn, int technicianId, String activityType)
            throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM TechnicianActivityLog " +
                "WHERE TechnicianID = ? AND ActivityType = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setString(2, activityType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }


    public int countActivitiesByDateRange(Connection conn, int technicianId,
                                          java.time.LocalDate fromDate,
                                          java.time.LocalDate toDate)
            throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM TechnicianActivityLog " +
                "WHERE TechnicianID = ? " +
                "  AND DATE(ActivityTime) >= ? " +
                "  AND DATE(ActivityTime) <= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }


    public java.util.Map<String, Integer> getActivityStatsByType(Connection conn,
                                                                 int technicianId,
                                                                 int days)
            throws SQLException {
        String sql =
                "SELECT ActivityType, COUNT(*) AS cnt " +
                        "FROM TechnicianActivityLog " +
                        "WHERE TechnicianID = ? " +
                        "  AND ActivityTime >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                        "GROUP BY ActivityType " +
                        "ORDER BY cnt DESC";

        java.util.Map<String, Integer> stats = new java.util.LinkedHashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            ps.setInt(2, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getString("ActivityType"), rs.getInt("cnt"));
                }
            }
        }
        return stats;
    }


    public int deleteOldActivities(Connection conn, int olderThanDays) throws SQLException {
        String sql = "DELETE FROM TechnicianActivityLog " +
                "WHERE ActivityTime < DATE_SUB(NOW(), INTERVAL ? DAY)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, olderThanDays);
            return ps.executeUpdate();
        }
    }


    public int deleteActivitiesByTechnician(Connection conn, int technicianId)
            throws SQLException {
        String sql = "DELETE FROM TechnicianActivityLog WHERE TechnicianID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            return ps.executeUpdate();
        }
    }


    private TechnicianActivity mapActivity(ResultSet rs) throws SQLException {
        TechnicianActivity activity = new TechnicianActivity();

        activity.setActivityID(rs.getInt("ActivityID"));
        activity.setTechnicianID(rs.getInt("TechnicianID"));
        activity.setActivityType(rs.getString("ActivityType"));

        int taskId = rs.getInt("TaskAssignmentID");
        if (!rs.wasNull()) {
            activity.setTaskAssignmentID(taskId);
        }

        activity.setDescription(rs.getString("Description"));

        Timestamp activityTime = rs.getTimestamp("ActivityTime");
        if (activityTime != null) {
            activity.setActivityTime(activityTime.toLocalDateTime());
        }

        return activity;
    }
}
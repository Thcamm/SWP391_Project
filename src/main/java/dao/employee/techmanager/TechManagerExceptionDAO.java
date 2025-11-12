package dao.employee.techmanager;

import common.DbContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for Tech Manager Exception Handling operations.
 * Handles database queries for SLA violations, declined tasks, and reassignments.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class TechManagerExceptionDAO {

    /**
     * Count 'ASSIGNED' tasks that are past their planned_start time
     * but have not been started (StartAt is NULL).
     * This indicates an SLA violation that TM needs to address.
     * 
     * @return count of overdue tasks
     * @throws SQLException if database error occurs
     */
    public int countOverdueTasks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                     "WHERE Status = 'ASSIGNED' " +
                     "AND StartAt IS NULL " +
                     "AND planned_start IS NOT NULL " +
                     "AND planned_start < NOW()";
        
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count tasks that were proactively 'DECLINED' by a Technician.
     * These tasks are marked as 'CANCELLED' and have a non-null declined_at timestamp.
     * They require TM attention for reassignment.
     * 
     * @return count of declined tasks
     * @throws SQLException if database error occurs
     */
    public int countDeclinedTasks() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                     "WHERE declined_at IS NOT NULL " +
                     "AND Status = 'CANCELLED'";
        
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /**
     * Count all tasks currently in 'CANCELLED' state that need reassignment.
     * This aggregates:
     * 1. Tasks proactively declined by Technicians
     * 2. Tasks manually cancelled by TMs (potentially due to being overdue)
     * 
     * @return total count of tasks needing reassignment
     * @throws SQLException if database error occurs
     */
    public int countTasksNeedReassignment() throws SQLException {
        String sql = "SELECT COUNT(*) FROM TaskAssignment " +
                     "WHERE Status = 'CANCELLED' " +
                     "AND (declined_at IS NOT NULL OR " +
                     "     (planned_start IS NOT NULL AND planned_start < NOW()))";
        
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}

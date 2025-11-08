package service.employee.techmanager;

import dao.employee.techmanager.TechManagerExceptionDAO;

import java.sql.SQLException;

/**
 * Service layer for Tech Manager Exception Handling operations.
 * Handles business logic for SLA violations, declined tasks, and reassignments.
 * 
 * @author An
 * @version 1.0
 */
public class TechManagerService {

    private final TechManagerExceptionDAO exceptionDAO;

    public TechManagerService() {
        this.exceptionDAO = new TechManagerExceptionDAO();
    }

    // =========================================================================
    // EXCEPTION HANDLING - PHASE 6
    // =========================================================================

    /**
     * Count tasks that are overdue (SLA violation).
     * Tasks are 'ASSIGNED' but past planned_start time without being started.
     * 
     * @return count of overdue tasks
     * @throws SQLException if database error occurs
     */
    public int countOverdueTasks() throws SQLException {
        return exceptionDAO.countOverdueTasks();
    }

    /**
     * Count tasks declined by technicians.
     * Tasks have declined_at timestamp and Status = 'CANCELLED'.
     * 
     * @return count of declined tasks
     * @throws SQLException if database error occurs
     */
    public int countDeclinedTasks() throws SQLException {
        return exceptionDAO.countDeclinedTasks();
    }

    /**
     * Count all tasks needing reassignment.
     * Includes:
     * 1. Tasks declined by technicians
     * 2. Tasks cancelled by TM (e.g., due to being overdue)
     * 
     * @return count of tasks needing reassignment
     * @throws SQLException if database error occurs
     */
    public int countTasksNeedReassignment() throws SQLException {
        return exceptionDAO.countTasksNeedReassignment();
    }
}

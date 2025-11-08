package service.employee.techmanager;

import dao.employee.techmanager.OverdueTaskDAO;
import model.employee.techmanager.OverdueTaskDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for Overdue Task business logic.
 * Manages SLA violation monitoring and task cancellation.
 * 
 * @author An
 * @version 1.0
 */
public class OverdueTaskService {

    private final OverdueTaskDAO overdueTaskDAO;

    public OverdueTaskService() {
        this.overdueTaskDAO = new OverdueTaskDAO();
    }

    /**
     * Get all overdue tasks (SLA violations).
     * 
     * @return list of overdue tasks
     * @throws SQLException if database error occurs
     */
    public List<OverdueTaskDTO> getOverdueTasks() throws SQLException {
        return overdueTaskDAO.getOverdueTasks();
    }

    /**
     * Cancel an overdue task.
     * After cancellation, task will appear in reassignment list.
     * 
     * @param assignmentId task assignment ID
     * @return success message or error message
     * @throws SQLException if database error occurs
     */
    public String cancelOverdueTask(int assignmentId) throws SQLException {
        boolean success = overdueTaskDAO.cancelTask(assignmentId);

        if (success) {
            return "Task cancelled successfully. It will appear in the reassignment list.";
        } else {
            return "Failed to cancel task";
        }
    }
}

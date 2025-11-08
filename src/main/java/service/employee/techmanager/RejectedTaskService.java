package service.employee.techmanager;

import dao.workorder.RejectedTaskDAO;
import model.employee.techmanager.RejectedTaskDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for Rejected Tasks business logic.
 * Manages rejected task monitoring (read-only view).
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class RejectedTaskService {

    private final RejectedTaskDAO rejectedTaskDAO;

    public RejectedTaskService() {
        this.rejectedTaskDAO = new RejectedTaskDAO();
    }

    /**
     * Get all rejected tasks that need attention.
     * 
     * @return list of rejected tasks
     * @throws SQLException if database error occurs
     */
    public List<RejectedTaskDTO> getRejectedTasks() throws SQLException {
        return rejectedTaskDAO.getRejectedTasks();
    }
}

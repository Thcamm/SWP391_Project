package service.employee.techmanager;

import dao.employee.techmanager.DeclinedTaskDAO;
import model.employee.techmanager.DeclinedTaskDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for Declined Task business logic.
 * Manages monitoring of tasks declined by technicians.
 * 
 * @author An
 * @version 1.0
 */
public class DeclinedTaskService {

    private final DeclinedTaskDAO declinedTaskDAO;

    public DeclinedTaskService() {
        this.declinedTaskDAO = new DeclinedTaskDAO();
    }

    /**
     * Get all tasks declined by technicians.
     * 
     * @return list of declined tasks
     * @throws SQLException if database error occurs
     */
    public List<DeclinedTaskDTO> getDeclinedTasks() throws SQLException {
        System.out.println("[DeclinedTaskService] ===== getDeclinedTasks() called =====");
        
        try {
            List<DeclinedTaskDTO> result = declinedTaskDAO.getDeclinedTasks();
            System.out.println("[DeclinedTaskService] DAO returned " + result.size() + " declined tasks");
            return result;
            
        } catch (SQLException e) {
            System.err.println("[DeclinedTaskService] SQL ERROR: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Count number of declined tasks.
     * Used for dashboard statistics.
     * 
     * @return count of declined tasks
     * @throws SQLException if database error occurs
     */
    public int countDeclinedTasks() throws SQLException {
        return declinedTaskDAO.countDeclinedTasks();
    }
}

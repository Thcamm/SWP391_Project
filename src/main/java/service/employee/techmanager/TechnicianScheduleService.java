package service.employee.techmanager;

import dao.employee.techmanager.TechnicianScheduleDAO;
import model.employee.techmanager.ScheduledTaskDTO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for Technician Schedule business logic.
 * Manages technician availability checking for task assignment.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class TechnicianScheduleService {

    private final TechnicianScheduleDAO technicianScheduleDAO;

    public TechnicianScheduleService() {
        this.technicianScheduleDAO = new TechnicianScheduleDAO();
    }

    /**
     * Get scheduled tasks for a specific technician on a specific date.
     * Used to check technician availability before assigning new tasks.
     * 
     * @param technicianId technician employee ID
     * @param targetDate target date
     * @return list of scheduled tasks
     * @throws SQLException if database error occurs
     */
    public List<ScheduledTaskDTO> getScheduledTasks(int technicianId, LocalDate targetDate) throws SQLException {
        return technicianScheduleDAO.getScheduledTasks(technicianId, targetDate);
    }
}

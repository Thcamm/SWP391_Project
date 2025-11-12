package service.employee.techmanager;

import dao.employee.techmanager.TaskReassignmentDAO;
import dao.employee.technician.TechnicianDAO;
import model.employee.Employee;
import model.employee.techmanager.TaskReassignmentDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Task Reassignment business logic.
 * Manages cancelled task reassignment workflow.
 * 
 * @author An
 * @version 1.0
 */
public class TaskReassignmentService {

    private final TaskReassignmentDAO taskReassignmentDAO;
    private final TechnicianDAO technicianDAO;

    public TaskReassignmentService() {
        this.taskReassignmentDAO = new TaskReassignmentDAO();
        this.technicianDAO = new TechnicianDAO();
    }

    /**
     * Get all cancelled tasks that need reassignment.
     * 
     * @return list of cancelled tasks
     * @throws SQLException if database error occurs
     */
    public List<TaskReassignmentDTO> getCancelledTasks() throws SQLException {
        return taskReassignmentDAO.getCancelledTasks();
    }

    /**
     * Reassign task to new technician with validation.
     * 
     * @param assignmentId task assignment ID
     * @param newTechnicianId new technician ID
     * @param plannedStart new planned start time (nullable)
     * @param plannedEnd new planned end time (nullable)
     * @return success message or error message
     * @throws SQLException if database error occurs
     */
    public String reassignTask(int assignmentId, int newTechnicianId,
                                LocalDateTime plannedStart, LocalDateTime plannedEnd) throws SQLException {

        // Validate technician exists
        Employee technician = technicianDAO.getTechnicianById(newTechnicianId);
        if (technician == null) {
            return "Technician not found";
        }

        // Validate time range if provided
        if (plannedStart != null && plannedEnd != null) {
            if (plannedEnd.isBefore(plannedStart)) {
                return "Planned end time must be after planned start time";
            }
        }

        // Perform reassignment
        boolean success = taskReassignmentDAO.reassignTask(assignmentId, newTechnicianId, plannedStart, plannedEnd);

        if (success) {
            return "Task reassigned successfully to " + technician.getFullName();
        } else {
            return "Failed to reassign task";
        }
    }

    /**
     * Get list of available technicians for reassignment.
     * 
     * @return list of active technicians
     * @throws SQLException if database error occurs
     */
    public List<Employee> getAvailableTechnicians() throws SQLException {
        return technicianDAO.getAllTechnicians();
    }
}

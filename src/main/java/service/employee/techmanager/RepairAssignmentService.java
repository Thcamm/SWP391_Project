package service.employee.techmanager;

import dao.workorder.RepairAssignmentDAO;
import model.employee.techmanager.ApprovedRepairDTO;
import model.employee.techmanager.TechnicianDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Repair Assignment business logic.
 * Manages repair task assignment workflow (Phase 3/GĐ4).
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class RepairAssignmentService {

    private final RepairAssignmentDAO repairAssignmentDAO;

    public RepairAssignmentService() {
        this.repairAssignmentDAO = new RepairAssignmentDAO();
    }

    /**
     * Get all approved repairs waiting for technician assignment.
     * 
     * @return list of approved repairs
     * @throws SQLException if database error occurs
     */
    public List<ApprovedRepairDTO> getApprovedRepairs() throws SQLException {
        return repairAssignmentDAO.getApprovedRepairs();
    }

    /**
     * Get list of available technicians for repair tasks.
     * 
     * @return list of available technicians
     * @throws SQLException if database error occurs
     */
    public List<TechnicianDTO> getAvailableTechnicians() throws SQLException {
        return repairAssignmentDAO.getAvailableTechnicians();
    }

    /**
     * Assign repair task to technician with validation.
     * 
     * @param detailId     work order detail ID
     * @param technicianId technician ID
     * @param plannedStart planned start time (nullable)
     * @param plannedEnd   planned end time (nullable)
     * @return success message or error message
     * @throws SQLException if database error occurs
     */
    public String assignRepairTask(int detailId, int technicianId,
            LocalDateTime plannedStart, LocalDateTime plannedEnd) throws SQLException {

        // Validate time range if provided
        if (plannedStart != null && plannedEnd != null) {
            if (!plannedEnd.isAfter(plannedStart)) {
                return "Planned end time must be after planned start time";
            }
        }

        // Check if already assigned
        if (repairAssignmentDAO.hasRepairTaskAssigned(detailId)) {
            return "This task has already been assigned";
        }

        // Create repair task assignment
        boolean success = repairAssignmentDAO.createRepairTask(detailId, technicianId, plannedStart, plannedEnd);

        if (success) {
            return "Repair task assigned successfully";
        } else {
            return "Failed to assign repair task";
        }
    }
}

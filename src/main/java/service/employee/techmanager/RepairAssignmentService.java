package service.employee.techmanager;

import dao.workorder.RepairAssignmentDAO;
import model.employee.techmanager.ApprovedRepairDTO;
import model.dto.TechnicianDTO;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Repair Assignment business logic.
 * Manages repair task assignment workflow (GĐ5).
 * 
 * LUỒNG MỚI (Triage Workflow):
 * - Handles WODs from BOTH sources:
 * 1. REQUEST: Direct from GĐ2 Triage (skip diagnosis)
 * 2. DIAGNOSTIC: From GĐ4 Quote Approval (customer approved)
 * 
 * @author SWP391 Team
 * @version 2.0 (Updated for LUỒNG MỚI)
 */
public class RepairAssignmentService {

    private final RepairAssignmentDAO repairAssignmentDAO;

    public RepairAssignmentService() {
        this.repairAssignmentDAO = new RepairAssignmentDAO();
    }

    /**
     * Get all approved repairs waiting for technician assignment.
     * 
     * LUỒNG MỚI:
     * - Returns WODs from BOTH sources (REQUEST + DIAGNOSTIC)
     * - REQUEST: Classified by GĐ2 Triage as direct repair (skip diagnosis)
     * - DIAGNOSTIC: Approved by customer after GĐ4 Quote
     * 
     * @return list of approved repairs from both sources
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
     * @param detailId        work order detail ID
     * @param technicianId    technician ID
     * @param taskDescription specific task description for this technician
     * @param plannedStart    planned start time (nullable)
     * @param plannedEnd      planned end time (nullable)
     * @return success message or error message
     * @throws SQLException if database error occurs
     */
    public String assignRepairTask(int detailId, int technicianId, String taskDescription,
            LocalDateTime plannedStart, LocalDateTime plannedEnd) throws SQLException {

        // Validate task description
        if (taskDescription == null || taskDescription.trim().isEmpty()) {
            return "Task description is required for each assignment";
        }

        // Validate time range if provided
        if (plannedStart != null && plannedEnd != null) {
            if (!plannedEnd.isAfter(plannedStart)) {
                return "Planned end time must be after planned start time";
            }
        }

        // LUỒNG MỚI: Allow multiple assignments per WorkOrderDetail
        // Removed hasRepairTaskAssigned check - TechManager can assign same detail to
        // multiple technicians
        // Each assignment represents a separate task (e.g., "Thay phanh" vs "Vệ sinh
        // ga")

        // Create repair task assignment
        boolean success = repairAssignmentDAO.createRepairTask(
                detailId, technicianId, taskDescription, plannedStart, plannedEnd);

        if (success) {
            return "Repair task assigned successfully";
        } else {
            return "Failed to assign repair task";
        }
    }
}

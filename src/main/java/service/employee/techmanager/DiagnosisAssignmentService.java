package service.employee.techmanager;

import dao.employee.admin.AdminDAO;
import dao.employee.technician.TechnicianDAO;
import dao.workorder.TaskAssignmentDAO;
import dao.misc.NotificationDAO;
import model.employee.Employee;
import model.employee.technician.TaskAssignment;
import model.misc.Notification;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Diagnosis Assignment business logic.
 * Manages diagnosis task assignment workflow (GĐ3).
 * 
 * LUỒNG MỚI (Triage Workflow):
 * - Only handles WorkOrderDetails with source='DIAGNOSTIC' (from GĐ2 Triage)
 * - REQUEST WODs skip this step and go directly to Repair Assignment (GĐ5)
 * 
 * @author SWP391 Team
 * @version 2.0 (Updated for LUỒNG MỚI)
 */
public class DiagnosisAssignmentService {

    private final TaskAssignmentDAO taskAssignmentDAO;
    private final TechnicianDAO technicianDAO;
    private final AdminDAO adminDAO;
    private final NotificationDAO notificationDAO;

    public DiagnosisAssignmentService() {
        this.taskAssignmentDAO = new TaskAssignmentDAO();
        this.technicianDAO = new TechnicianDAO();
        this.adminDAO = new AdminDAO();
        this.notificationDAO = new NotificationDAO();
    }

    /**
     * Get TechManager's employee ID by username.
     * 
     * @param userName username from session
     * @return employee ID or null if not found
     * @throws SQLException if database error occurs
     */
    public Integer getTechManagerEmployeeId(String userName) throws SQLException {
        return adminDAO.getEmployeeIdByUsername(userName);
    }

    /**
     * Get WorkOrderDetails that need diagnosis assignment.
     * 
     * LUỒNG MỚI:
     * - Only returns WODs with source='DIAGNOSTIC' (classified by GĐ2 Triage)
     * - REQUEST WODs are excluded (they go to Repair Assignment directly)
     * 
     * @param techManagerEmployeeId TechManager's employee ID
     * @return list of DIAGNOSTIC work order details awaiting assignment
     * @throws SQLException if database error occurs
     */
    public List<TaskAssignmentDAO.WorkOrderDetailWithInfo> getPendingDiagnosisTasks(int techManagerEmployeeId)
            throws SQLException {
        return taskAssignmentDAO.getWorkOrderDetailsNeedingDiagnosisAssignment(techManagerEmployeeId);
    }

    /**
     * Get list of available technicians.
     * 
     * @return list of available technicians
     * @throws SQLException if database error occurs
     */
    public List<Employee> getAvailableTechnicians() throws SQLException {
        return technicianDAO.getAllTechnicians();
    }

    /**
     * Assign diagnosis task to technician with validation.
     * 
     * @param detailId        work order detail ID
     * @param technicianId    technician ID
     * @param taskDescription specific task description for this technician
     * @param priority        task priority
     * @param notes           task notes (nullable)
     * @param plannedStart    planned start time (nullable)
     * @param plannedEnd      planned end time (nullable)
     * @return assignment ID if successful, -1 if failed
     * @throws SQLException if database error occurs
     */
    public int assignDiagnosisTask(int detailId, int technicianId, String taskDescription,
            String priority, String notes, LocalDateTime plannedStart, LocalDateTime plannedEnd) throws SQLException {

        // Validate task description
        if (taskDescription == null || taskDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description is required for each assignment");
        }

        // Validate time range if provided
        if (plannedStart != null && plannedEnd != null) {
            if (!plannedEnd.isAfter(plannedStart)) {
                throw new IllegalArgumentException("Planned end time must be after planned start time");
            }
        }

        // Create TaskAssignment
        TaskAssignment task = new TaskAssignment();
        task.setDetailID(detailId);
        task.setAssignToTechID(technicianId);
        task.setAssignedDate(LocalDateTime.now());
        task.setTaskDescription(taskDescription); // Use user-provided description
        task.setTaskType(TaskAssignment.TaskType.DIAGNOSIS);
        task.setStatus(TaskAssignment.TaskStatus.ASSIGNED);

        // Set priority
        if (priority != null && !priority.trim().isEmpty()) {
            task.setPriority(TaskAssignment.Priority.valueOf(priority.toUpperCase()));
        } else {
            task.setPriority(TaskAssignment.Priority.MEDIUM);
        }

        task.setNotes(notes);
        task.setPlannedStart(plannedStart);
        task.setPlannedEnd(plannedEnd);

        // Save to database
        int assignmentId = taskAssignmentDAO.createTaskAssignment(task);

        if (assignmentId > 0) {
            // Create notification for Technician
            Employee technician = technicianDAO.getTechnicianById(technicianId);
            if (technician != null) {
                Notification notif = new Notification();
                notif.setUserId(technician.getUserId());
                notif.setTitle("New Diagnosis Task Assigned");
                notif.setBody("You have been assigned a diagnosis task. Priority: " + task.getPriority());
                notif.setEntityType("WORK_ORDER");
                notif.setEntityId(assignmentId);
                notificationDAO.createNotification(notif);
            }
        }

        return assignmentId;
    }

    /**
     * Get list of diagnosis tasks currently in progress for a specific TechManager.
     * These are tasks that have been assigned and technicians are actively working
     * on.
     * 
     * @param techManagerEmployeeId TechManager's employee ID
     * @return list of in-progress diagnosis tasks
     * @throws SQLException if database error occurs
     */
    public List<TaskAssignmentDAO.InProgressDiagnosisTask> getInProgressDiagnosisTasks(int techManagerEmployeeId)
            throws SQLException {
        return taskAssignmentDAO.getInProgressDiagnosisTasks(techManagerEmployeeId);
    }
}

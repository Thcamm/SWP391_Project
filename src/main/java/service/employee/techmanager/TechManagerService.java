package service.employee.techmanager;

import common.DbContext;
import dao.carservice.ServiceRequestDAO;
import dao.employee.EmployeeDAO;
import dao.employee.techmanager.TechManagerExceptionDAO;
import dao.workorder.TaskAssignmentDAO;
import dao.workorder.WorkOrderDAO;
import dao.workorder.WorkOrderDetailDAO;
import model.employee.Employee;
import model.employee.technician.TaskAssignment;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;
import service.notification.NotificationService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Business Logic Service for Technical Manager operations
 * Consolidates all Tech Manager workflows in one place
 * 
 * Main Responsibilities:
 * 1. Service Request Approval → WorkOrder creation (TODO: implement when DAOs
 * ready)
 * 2. Task Assignment (Diagnosis & Repair) (TODO: implement when DAOs ready)
 * 3. Task Reassignment & Overdue Management
 * 4. Exception Handling (counts for dashboard) ✅
 * 
 * Design Pattern: Service Layer (Business Logic)
 * Transaction Management: Handled in Service methods
 * 
 * @author An
 * @version 2.0 - Refactored to centralize all business logic
 * 
 *          NOTE: Phase 2 - Skeleton created. Full implementations pending DAO
 *          method additions.
 */
public class TechManagerService {

    private final ServiceRequestDAO requestDAO;
    private final WorkOrderDAO workOrderDAO;
    private final WorkOrderDetailDAO detailDAO;
    private final TaskAssignmentDAO taskDAO;
    private final EmployeeDAO employeeDAO;
    private final TechManagerExceptionDAO exceptionDAO;
    private final NotificationService notificationService;

    // === CONSTRUCTOR ===
    public TechManagerService() {
        this.requestDAO = new ServiceRequestDAO();
        this.workOrderDAO = new WorkOrderDAO();
        this.detailDAO = new WorkOrderDetailDAO();
        this.taskDAO = new TaskAssignmentDAO();
        this.employeeDAO = new EmployeeDAO();
        this.exceptionDAO = new TechManagerExceptionDAO();
        this.notificationService = new NotificationService();
    }

    // =========================================================================
    // MAIN BUSINESS LOGIC - UC-TM-01 to UC-TM-07
    // =========================================================================

    /**
     * UC-TM-01: Approve Service Request and Create WorkOrder
     * 
     * Business Flow:
     * 1. Update ServiceRequest status = "APPROVED"
     * 2. Create WorkOrder (TechManager assigned)
     * 3. Create WorkOrderDetail from ServicePackage (if exists)
     * 
     * Transaction: ALL-or-NOTHING
     * 
     * @param requestId     Service Request ID to approve
     * @param techManagerId ID of the approving Technical Manager
     * @param notes         Optional approval notes (not used currently)
     * @return WorkOrderID if success, -1 if failed
     */
    public int approveServiceRequestAndCreateWorkOrder(
            int requestId,
            int techManagerId,
            String notes) throws SQLException {

        Connection conn = null;
        try {
            // === START TRANSACTION ===
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);
            System.out.println("=== [TechManagerService] START Transaction for ServiceRequest #" + requestId + " ===");

            // === STEP 1: Validate ServiceRequest ===
            ServiceRequest request = requestDAO.getServiceRequestForUpdate(conn, requestId);
            if (request == null) {
                throw new IllegalArgumentException("ServiceRequest not found: " + requestId);
            }
            System.out.println("[STEP 1] ✓ ServiceRequest found: ID=" + requestId + ", Status=" + request.getStatus());

            // === STEP 2: Update ServiceRequest Status ===
            // Note: Database uses 'APPROVE' (not 'APPROVED')
            boolean updated = requestDAO.updateServiceRequestStatus(conn, requestId, "APPROVE");
            if (!updated) {
                throw new SQLException("Failed to update ServiceRequest status");
            }
            System.out.println("[STEP 2] ✓ ServiceRequest status updated: PENDING → APPROVE");

            // === STEP 3: Create WorkOrder ===
            WorkOrder workOrder = new WorkOrder();
            workOrder.setRequestId(requestId);
            workOrder.setTechManagerId(techManagerId);
            workOrder.setStatus(WorkOrder.Status.PENDING); // Initial status - waiting for diagnosis
            workOrder.setEstimateAmount(BigDecimal.ZERO); // Updated later

            int workOrderId = workOrderDAO.createWorkOrder(conn, workOrder);
            if (workOrderId <= 0) {
                throw new SQLException("Failed to create WorkOrder");
            }
            System.out.println("[STEP 3] ✓ WorkOrder created: ID=" + workOrderId + ", TechManager=" + techManagerId
                    + ", Status=PENDING");

            // === STEP 4: Create initial DIAGNOSIS WorkOrderDetail ===
            // This is required for TechManager to assign diagnosis tasks
            WorkOrderDetail diagnosisDetail = new WorkOrderDetail();
            diagnosisDetail.setWorkOrderId(workOrderId);
            diagnosisDetail.setSource(WorkOrderDetail.Source.REQUEST);
            diagnosisDetail.setTaskDescription(
                    notes != null && !notes.trim().isEmpty()
                            ? notes
                            : "Chẩn đoán tổng quát tình trạng xe");
            diagnosisDetail.setApprovalStatus(WorkOrderDetail.ApprovalStatus.APPROVED);
            diagnosisDetail.setEstimateHours(BigDecimal.valueOf(1.0));
            diagnosisDetail.setEstimateAmount(BigDecimal.ZERO);

            System.out.println("[STEP 4] Creating WorkOrderDetail: WorkOrderID=" + workOrderId +
                    ", Source=REQUEST, ApprovalStatus=APPROVED");

            int detailId = detailDAO.createWorkOrderDetail(conn, diagnosisDetail);
            if (detailId <= 0) {
                throw new SQLException("Failed to create initial WorkOrderDetail");
            }
            System.out.println("[STEP 4] ✓ WorkOrderDetail created: ID=" + detailId +
                    ", TaskDescription='" + diagnosisDetail.getTaskDescription() + "'");

            // === COMMIT TRANSACTION ===
            conn.commit();
            System.out.println("=== [TechManagerService] ✓ COMMIT SUCCESS: WorkOrder #" + workOrderId +
                    " with Detail #" + detailId + " ===");
            return workOrderId;

        } catch (Exception e) {
            // === ROLLBACK on Error ===
            System.err.println("=== [TechManagerService] ✗ TRANSACTION FAILED ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✓ Transaction rolled back");
                } catch (SQLException rollbackEx) {
                    System.err.println("✗ Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new SQLException("Transaction failed: " + e.getMessage(), e);

        } finally {
            // === RESTORE AUTO-COMMIT ===
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Connection close failed: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * UC-TM-02: Assign Diagnosis Task to Technician
     * 
     * Business Flow:
     * 1. Validate WorkOrder exists
     * 2. Create TaskAssignment (type=DIAGNOSIS)
     * 3. Notify technician (TODO: implement when NotificationService ready)
     * 
     * @param workOrderId  WorkOrder ID
     * @param detailId     WorkOrderDetail ID
     * @param technicianId Technician Employee ID
     * @param instructions Special instructions for technician
     * @return TaskAssignmentID if success
     */
    public int assignDiagnosisTask(
            int workOrderId,
            int detailId,
            int technicianId,
            String instructions) throws SQLException {

        // === Validate WorkOrder exists ===
        WorkOrder workOrder = workOrderDAO.getWorkOrderById(workOrderId);
        if (workOrder == null) {
            throw new IllegalArgumentException("WorkOrder not found: " + workOrderId);
        }

        // === Create TaskAssignment ===
        TaskAssignment task = new TaskAssignment();
        task.setDetailID(detailId);
        task.setAssignToTechID(technicianId);
        task.setTaskType(TaskAssignment.TaskType.DIAGNOSIS);
        task.setStatus(TaskAssignment.TaskStatus.ASSIGNED);
        task.setNotes(instructions);
        // Set planned_start with buffer time (2 hours from now) to avoid immediate overdue
        task.setPlannedStart(LocalDateTime.now().plusHours(2));

        System.out.println("[assignDiagnosisTask] Creating task: DetailID=" + detailId + 
                         ", TechID=" + technicianId + ", PlannedStart=" + task.getPlannedStart());

        int taskId = taskDAO.createTaskAssignment(task);
        if (taskId <= 0) {
            throw new SQLException("Failed to create TaskAssignment");
        }

        // TODO: Send notification to technician when NotificationService supports
        // transactions
        // notificationService.createNotification(...);

        return taskId;
    }

    /**
     * UC-TM-03: Assign Repair Task to Technician
     * 
     * Similar to assignDiagnosisTask but for REPAIR type
     */
    public int assignRepairTask(
            int workOrderId,
            int detailId,
            int technicianId,
            String instructions) throws SQLException {

        // === Validate WorkOrder exists ===
        WorkOrder workOrder = workOrderDAO.getWorkOrderById(workOrderId);
        if (workOrder == null) {
            throw new IllegalArgumentException("WorkOrder not found: " + workOrderId);
        }

        // === Create REPAIR TaskAssignment ===
        TaskAssignment task = new TaskAssignment();
        task.setDetailID(detailId);
        task.setAssignToTechID(technicianId);
        task.setTaskType(TaskAssignment.TaskType.REPAIR);
        task.setStatus(TaskAssignment.TaskStatus.ASSIGNED);
        task.setNotes(instructions);
        // Set planned_start with buffer time (2 hours from now) to avoid immediate overdue
        task.setPlannedStart(LocalDateTime.now().plusHours(2));

        System.out.println("[assignRepairTask] Creating task: DetailID=" + detailId + 
                         ", TechID=" + technicianId + ", PlannedStart=" + task.getPlannedStart());

        int taskId = taskDAO.createTaskAssignment(task);
        if (taskId <= 0) {
            throw new SQLException("Failed to create repair task");
        }

        // TODO: Notify technician
        return taskId;
    }

    /**
     * UC-TM-07: Reassign Task to Another Technician
     * 
     * Business Flow:
     * 1. Validate task exists
     * 2. Update task: AssignToTechID + Status=ASSIGNED
     * 3. Notify both old and new technician (TODO)
     * 
     * @param taskId          Task Assignment ID
     * @param newTechnicianId New technician Employee ID
     * @param reason          Reassignment reason (not used in DAO currently)
     * @return true if reassignment succeeded
     */
    public boolean reassignTask(
            int taskId,
            int newTechnicianId,
            String reason) throws SQLException {

        // Calculate new schedule (simplified - 24 hours from now)
        LocalDateTime plannedStart = LocalDateTime.now().plusHours(2);
        LocalDateTime plannedEnd = plannedStart.plusDays(1);

        // === Reassign task using DAO ===
        boolean updated = taskDAO.reassignTask(taskId, newTechnicianId, plannedStart, plannedEnd);
        if (!updated) {
            throw new SQLException("Failed to reassign task. Task may not be in CANCELLED status.");
        }

        // TODO: Send notifications to old & new technicians when NotificationService
        // ready
        return true;
    }

    /**
     * UC-TM-06: Cancel Overdue Task
     * 
     * ✅ Implemented - uses existing DAO method
     */
    public boolean cancelOverdueTask(int taskId, String reason) throws SQLException {
        // Note: reason parameter stored in Notes field (if DAO supports it)
        return taskDAO.updateTaskStatus(taskId, TaskAssignment.TaskStatus.CANCELLED);
    }

    // =========================================================================
    // EXCEPTION HANDLING COUNTS - PHASE 6 (EXISTING METHODS)
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

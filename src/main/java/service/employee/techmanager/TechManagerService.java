package service.employee.techmanager;

import common.DbContext;
import dao.carservice.ServiceRequestDAO;
import dao.employee.EmployeeDAO;
import dao.employee.techmanager.TechManagerExceptionDAO;
import dao.workorder.TaskAssignmentDAO;
import dao.workorder.WorkOrderDAO;
import dao.workorder.WorkOrderDetailDAO;
import model.employee.Employee;
import model.employee.techmanager.PendingServiceRequestDTO;
import model.employee.technician.TaskAssignment;
import model.workorder.ServiceRequest;
import model.workorder.ServiceRequestDetail;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;
import service.notification.NotificationService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REFACTORED - LUỒNG MỚI (Triage Workflow)
 * Business Logic Service for Technical Manager operations
 * 
 * NEW WORKFLOW (Luồng 4 - Phân loại):
 * GĐ 1: Approve ServiceRequest → Create 1 WorkOrder + N WorkOrderDetails
 * (source=NULL)
 * GĐ 2: Triage → TM classifies each WOD as REQUEST or DIAGNOSTIC
 * GĐ 3: Routing → WODs auto-route to assign-diagnosis or assign-repair
 * GĐ 4: Quote Flow → Unchanged (for DIAGNOSTIC WODs)
 * GĐ 5: Repair Assignment → Gets WODs from BOTH sources (REQUEST + DIAGNOSTIC)
 * 
 * DELETED OLD LOGIC:
 * - No more automatic "Chẩn đoán tổng quát" WorkOrderDetail creation
 * - TM now has full control over service classification
 * 
 * @author SWP391 Team - Refactored 2024
 */
public class TechManagerService {

    private final ServiceRequestDAO requestDAO; // For general ServiceRequest operations
    private final dao.employee.techmanager.ServiceRequestDAO techManagerRequestDAO; // For TechManager-specific queries
    private final WorkOrderDAO workOrderDAO;
    private final WorkOrderDetailDAO detailDAO;
    private final TaskAssignmentDAO taskDAO;
    private final EmployeeDAO employeeDAO;
    private final TechManagerExceptionDAO exceptionDAO;
    private final NotificationService notificationService;

    // === CONSTRUCTOR ===
    public TechManagerService() {
        this.requestDAO = new ServiceRequestDAO();
        this.techManagerRequestDAO = new dao.employee.techmanager.ServiceRequestDAO();
        this.workOrderDAO = new WorkOrderDAO();
        this.detailDAO = new WorkOrderDetailDAO();
        this.taskDAO = new TaskAssignmentDAO();
        this.employeeDAO = new EmployeeDAO();
        this.exceptionDAO = new TechManagerExceptionDAO();
        this.notificationService = new NotificationService();
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    /**
     * Get TechManager's Employee ID from username.
     * 
     * @param userName the username
     * @return Employee ID or null if not found
     * @throws SQLException if database error occurs
     */
    public Integer getTechManagerEmployeeId(String userName) throws SQLException {
        return employeeDAO.getEmployeeIdByUserName(userName);
    }

    /**
     * Get all pending service requests for TechManager review.
     * 
     * @return List of pending service requests
     * @throws SQLException if database error occurs
     */
    public List<PendingServiceRequestDTO> getPendingServiceRequests() throws SQLException {
        return techManagerRequestDAO.getPendingServiceRequests();
    }

    /**
     * Alias method for approveServiceRequestAndCreateWorkOrder (backward
     * compatibility).
     * 
     * @param requestId     Service Request ID
     * @param techManagerId TechManager Employee ID
     * @return WorkOrder ID if successful, -1 if failed
     * @throws SQLException if database error occurs
     * @deprecated Use approveServiceRequest(int, int, Map) with source
     *             classifications instead
     */
    @Deprecated
    public int approveServiceRequest(int requestId, int techManagerId) throws SQLException {
        throw new UnsupportedOperationException(
                "Use approveServiceRequest(requestId, techManagerId, sourceClassifications) instead");
    }

    /**
     * Approve ServiceRequest and create WorkOrder with source classification
     * 
     * @param requestId             ServiceRequest ID
     * @param techManagerId         TechManager's Employee ID
     * @param sourceClassifications Map of service detail IDs to their source
     *                              classification
     * @return WorkOrder ID
     */
    public int approveServiceRequest(int requestId, int techManagerId, Map<Integer, String> sourceClassifications)
            throws SQLException {
        try {
            return approveServiceRequestAndCreateWorkOrder(requestId, techManagerId, sourceClassifications);
        } catch (Exception e) {
            System.err.println("Error approving service request: " + e.getMessage());
            throw new SQLException("Failed to approve service request: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // GĐ 1: APPROVE & SPLIT - LUỒNG MỚI
    // =========================================================================

    /**
     * GĐ 1: Approve Service Request and Create N WorkOrderDetails with source
     * classification
     * 
     * NEW LOGIC - Direct Classification Workflow:
     * 1. Update ServiceRequest status = "APPROVE"
     * 2. Create 1 WorkOrder
     * 3. Get N ServiceRequestDetails
     * 4. Create N WorkOrderDetails (each with source=REQUEST|DIAGNOSTIC based on TM
     * classification)
     * 5. Return WorkOrderID
     * 
     * Transaction: ALL-or-NOTHING
     * 
     * @param requestId             Service Request ID to approve
     * @param techManagerId         ID of the approving Technical Manager
     * @param sourceClassifications Map of serviceDetailId to source ("REQUEST" or
     *                              "DIAGNOSTIC")
     * @return WorkOrderID if success, -1 if failed
     */
    private int approveServiceRequestAndCreateWorkOrder(
            int requestId,
            int techManagerId,
            Map<Integer, String> sourceClassifications) throws SQLException {

        Connection conn = null;
        try {
            // === START TRANSACTION ===
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);
            System.out.println("=== [LUỒNG MỚI - GĐ 1] START Transaction for ServiceRequest #" + requestId + " ===");

            // === STEP 1: Validate ServiceRequest ===
            ServiceRequest request = requestDAO.getServiceRequestForUpdate(conn, requestId);
            if (request == null) {
                throw new IllegalArgumentException("ServiceRequest not found: " + requestId);
            }
            System.out.println("[STEP 1] ✓ ServiceRequest found: ID=" + requestId + ", Status=" + request.getStatus());

            // === STEP 2: Get all ServiceRequestDetails (N services) ===
            List<ServiceRequestDetail> serviceDetails = requestDAO.getServiceRequestDetails(conn, requestId);
            if (serviceDetails == null || serviceDetails.isEmpty()) {
                throw new SQLException("No service details found for ServiceRequest #" + requestId);
            }
            System.out.println("[STEP 2] ✓ Found " + serviceDetails.size() + " service(s) to split");

            // === STEP 3: Update ServiceRequest Status ===
            boolean updated = requestDAO.updateServiceRequestStatus(conn, requestId, "APPROVE");
            if (!updated) {
                throw new SQLException("Failed to update ServiceRequest status");
            }
            System.out.println("[STEP 3] ✓ ServiceRequest status updated: PENDING → APPROVE");

            // === STEP 4: Create WorkOrder ===
            WorkOrder workOrder = new WorkOrder();
            workOrder.setRequestId(requestId);
            workOrder.setTechManagerId(techManagerId);
            workOrder.setStatus(WorkOrder.Status.PENDING);

            // Calculate total estimate amount from all services
            BigDecimal totalEstimate = serviceDetails.stream()
                    .map(ServiceRequestDetail::getServiceUnitPrice)
                    .filter(price -> price != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            workOrder.setEstimateAmount(totalEstimate);

            int workOrderId = workOrderDAO.createWorkOrder(conn, workOrder);
            if (workOrderId <= 0) {
                throw new SQLException("Failed to create WorkOrder");
            }
            System.out.println("[STEP 4] ✓ WorkOrder created: ID=" + workOrderId + ", EstimateAmount=" + totalEstimate);

            // === STEP 5: Create N WorkOrderDetails with source classification ===
            int createdCount = 0;
            for (ServiceRequestDetail serviceDetail : serviceDetails) {
                // Get source classification from map
                String source = sourceClassifications.get(serviceDetail.getDetailId());
                if (source == null || (!source.equals("REQUEST") && !source.equals("DIAGNOSTIC"))) {
                    throw new SQLException(
                            "Invalid source classification for service detail #" + serviceDetail.getDetailId());
                }

                WorkOrderDetail wod = new WorkOrderDetail();
                wod.setWorkOrderId(workOrderId);

                // Set source based on classification
                wod.setSource(WorkOrderDetail.Source.valueOf(source));
                wod.setDiagnosticId(null);
                // LUỒNG MỚI: Auto-approve when TM classifies source during approval
                wod.setApprovalStatus(WorkOrderDetail.ApprovalStatus.APPROVED);
                wod.setApprovedByUserId(techManagerId);
                wod.setApprovedAt(new Timestamp(System.currentTimeMillis()));
                wod.setTaskDescription(serviceDetail.getServiceName() +
                        (serviceDetail.getServiceDescription() != null ? " - " + serviceDetail.getServiceDescription()
                                : ""));
                wod.setEstimateHours(BigDecimal.valueOf(2.0)); // Default estimate
                wod.setEstimateAmount(serviceDetail.getServiceUnitPrice() != null ? serviceDetail.getServiceUnitPrice()
                        : BigDecimal.ZERO);

                int detailId = detailDAO.createWorkOrderDetail(conn, wod);
                if (detailId <= 0) {
                    throw new SQLException(
                            "Failed to create WorkOrderDetail for service: " + serviceDetail.getServiceName());
                }
                createdCount++;
                System.out.println("  [5." + createdCount + "] ✓ Created WOD #" + detailId + ": " +
                        serviceDetail.getServiceName() + " (source=" + source + ")");
            }

            System.out.println("[STEP 5] ✓ Created " + createdCount + " WorkOrderDetail(s) with source classification");

            // === COMMIT TRANSACTION ===
            conn.commit();
            System.out.println("=== [LUỒNG MỚI - GĐ 1] ✓ COMMIT SUCCESS: WorkOrder #" + workOrderId +
                    " with " + createdCount + " WOD(s) ready for Triage ===");
            return workOrderId;

        } catch (Exception e) {
            // === ROLLBACK on Error ===
            System.err.println("=== [LUỒNG MỚI - GĐ 1] ✗ TRANSACTION FAILED ===");
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

    // =========================================================================
    // GĐ 2: TRIAGE - LUỒNG MỚI
    // =========================================================================

    /**
     * GĐ 2: Triage WorkOrderDetails - Classify each as REQUEST or DIAGNOSTIC
     * 
     * NEW FUNCTION - Core of Triage Workflow:
     * - Receives Map<DetailID, Source> from TriageServlet
     * - Updates each WorkOrderDetail: source + approval_status=APPROVED
     * - After this, WODs auto-route to correct assignment screen (GĐ 3)
     * 
     * @param triageDecisions Map<DetailID, Source> - TM's classification decisions
     * @param techManagerId   TechManager making the decision
     * @return Number of WODs successfully triaged
     * @throws SQLException if update fails
     */
    public int triageWorkOrderDetails(
            java.util.Map<Integer, WorkOrderDetail.Source> triageDecisions,
            int techManagerId) throws SQLException {

        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);
            System.out.println("=== [LUỒNG MỚI - GĐ 2] START Triage for " + triageDecisions.size() + " WOD(s) ===");

            int updatedCount = 0;
            for (java.util.Map.Entry<Integer, WorkOrderDetail.Source> entry : triageDecisions.entrySet()) {
                int detailId = entry.getKey();
                WorkOrderDetail.Source source = entry.getValue();

                // Update WorkOrderDetail: set source and approval_status=APPROVED
                boolean updated = detailDAO.updateWorkOrderDetailSource(conn, detailId, source,
                        WorkOrderDetail.ApprovalStatus.APPROVED, techManagerId);

                if (updated) {
                    updatedCount++;
                    System.out.println("  [" + updatedCount + "] ✓ WOD #" + detailId + " → source=" + source);
                } else {
                    throw new SQLException("Failed to update WorkOrderDetail #" + detailId);
                }
            }

            conn.commit();
            System.out.println("=== [LUỒNG MỚI - GĐ 2] ✓ Triage COMPLETE: " + updatedCount + " WOD(s) classified ===");
            return updatedCount;

        } catch (Exception e) {
            System.err.println("=== [LUỒNG MỚI - GĐ 2] ✗ Triage FAILED ===");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("✗ Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new SQLException("Triage failed: " + e.getMessage(), e);

        } finally {
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

    // =========================================================================
    // GĐ 4 & GĐ 5: TASK ASSIGNMENT (Unchanged from old flow)
    // =========================================================================

    /**
     * UC-TM-02: Assign Diagnosis Task to Technician
     * 
     * NOTE: This method is unchanged - still used for WODs with source='DIAGNOSTIC'
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
        task.setPlannedStart(LocalDateTime.now());

        System.out.println("[assignDiagnosisTask] Creating task: DetailID=" + detailId +
                ", TechID=" + technicianId + ", PlannedStart=" + task.getPlannedStart());

        int taskId = taskDAO.createTaskAssignment(task);
        if (taskId <= 0) {
            throw new SQLException("Failed to create TaskAssignment");
        }

        return taskId;
    }

    /**
     * UC-TM-03: Assign Repair Task to Technician
     * 
     * NOTE: LUỒNG MỚI - This now handles BOTH sources:
     * - source='REQUEST' (from Triage - GĐ 2)
     * - source='DIAGNOSTIC' (from Quote Flow - GĐ 4)
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
        task.setPlannedStart(LocalDateTime.now());

        System.out.println("[assignRepairTask] Creating task: DetailID=" + detailId +
                ", TechID=" + technicianId + ", PlannedStart=" + task.getPlannedStart());

        int taskId = taskDAO.createTaskAssignment(task);
        if (taskId <= 0) {
            throw new SQLException("Failed to create repair task");
        }

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

    // =========================================================================
    // GĐ 3: GET PENDING TRIAGE WORKORDERDETAILS
    // =========================================================================

    /**
     * Get all WorkOrderDetails awaiting Triage classification
     * (source IS NULL and approval_status='PENDING')
     * 
     * DEPRECATED: Classification now happens during approval
     * 
     * @param workOrderId WorkOrder ID
     * @return List of WorkOrderDetail awaiting triage
     * @throws SQLException if database error
     */
    public List<WorkOrderDetail> getPendingTriageDetails(int workOrderId) throws SQLException {
        return detailDAO.getPendingTriageDetails(workOrderId);
    }
}

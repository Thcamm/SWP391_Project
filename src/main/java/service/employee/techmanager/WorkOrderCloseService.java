package service.employee.techmanager;

import dao.employee.techmanager.WorkOrderCloseDAO;
import model.employee.techmanager.WorkOrderCloseDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for WorkOrder closure operations (GĐ7).
 * Handles business logic for closing work orders.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class WorkOrderCloseService {

    private final WorkOrderCloseDAO workOrderCloseDAO;

    // Constructor
    public WorkOrderCloseService() {
        this.workOrderCloseDAO = new WorkOrderCloseDAO();
    }

    // Constructor for dependency injection (testing)
    public WorkOrderCloseService(WorkOrderCloseDAO workOrderCloseDAO) {
        this.workOrderCloseDAO = workOrderCloseDAO;
    }

    /**
     * Get all WorkOrders ready for closure.
     * 
     * Business rules:
     * - Status must be 'IN_PROCESS'
     * - All TaskAssignments must be 'COMPLETE'
     * - Sorted by creation date (oldest first)
     * 
     * @return List of WorkOrders ready to close
     * @throws SQLException if database error occurs
     */
    public List<WorkOrderCloseDTO> getWorkOrdersReadyForClosure() throws SQLException {
        return workOrderCloseDAO.getWorkOrdersReadyForClosure();
    }

    /**
     * Close a WorkOrder after verification.
     * 
     * Business rules:
     * 1. Verify work order exists and is IN_PROCESS
     * 2. Verify all tasks are complete
     * 3. Update status to COMPLETE
     * 4. (Future: Trigger invoice generation)
     * 
     * @param workOrderID The ID of the WorkOrder to close
     * @return true if work order was closed successfully
     * @throws SQLException          if database error occurs
     * @throws IllegalStateException if work order is not ready to close
     */
    public boolean closeWorkOrder(int workOrderID) throws SQLException {
        // Verify work order is ready to close
        WorkOrderCloseDTO workOrder = workOrderCloseDAO.getWorkOrderForClosure(workOrderID);

        if (workOrder == null) {
            throw new IllegalStateException("Work Order #" + workOrderID + " not found or not in IN_PROCESS status");
        }

        if (!workOrder.isAllTasksComplete()) {
            throw new IllegalStateException(
                    "Work Order #" + workOrderID + " cannot be closed. " +
                            "Completed: " + workOrder.getCompletedTasks() + "/" + workOrder.getTotalTasks() + " tasks");
        }

        // Close the work order
        boolean success = workOrderCloseDAO.closeWorkOrder(workOrderID);

        if (!success) {
            throw new IllegalStateException(
                    "Failed to close Work Order #" + workOrderID +
                            ". It may have been modified by another user.");
        }

        return true;
    }

    /**
     * Get total count of work orders ready for closure.
     * 
     * @return Count of work orders ready to close
     * @throws SQLException if database error occurs
     */
    public int getReadyForClosureCount() throws SQLException {
        return workOrderCloseDAO.getWorkOrdersReadyForClosure().size();
    }

    /**
     * Get a specific work order for verification before closing.
     * 
     * @param workOrderID The ID of the WorkOrder
     * @return WorkOrderCloseDTO if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public WorkOrderCloseDTO getWorkOrderForVerification(int workOrderID) throws SQLException {
        return workOrderCloseDAO.getWorkOrderForClosure(workOrderID);
    }
}

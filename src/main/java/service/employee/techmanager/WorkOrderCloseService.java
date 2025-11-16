package service.employee.techmanager;

import dao.employee.techmanager.WorkOrderCloseDAO;
import dao.employee.admin.AdminDAO;
import model.employee.techmanager.WorkOrderCloseDTO;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for Work Order Closure business logic (GĐ7)
 * 
 * @author SWP391 Team
 * @version 2.0
 */
public class WorkOrderCloseService {

    private final WorkOrderCloseDAO workOrderCloseDAO;
    private final AdminDAO adminDAO;

    public WorkOrderCloseService() {
        this.workOrderCloseDAO = new WorkOrderCloseDAO();
        this.adminDAO = new AdminDAO();
    }

    public Integer getTechManagerEmployeeId(String userName) throws SQLException {
        return adminDAO.getEmployeeIdByUsername(userName);
    }

    public List<WorkOrderCloseDTO> getAllWorkOrdersForClosure(int techManagerId) throws SQLException {
        return workOrderCloseDAO.getAllWorkOrdersForClosure(techManagerId);
    }

    public String closeWorkOrder(int workOrderId, int closedByEmployeeId) throws SQLException {
        WorkOrderCloseDTO details = workOrderCloseDAO.getWorkOrderDetails(workOrderId);
        if (details == null) {
            throw new IllegalStateException("Work Order #" + workOrderId + " not found");
        }

        if (details.getActiveTasks() > 0) {
            throw new IllegalStateException(
                    "Cannot close Work Order #" + workOrderId +
                            ". Still has " + details.getActiveTasks() + " active task(s).");
        }

        boolean success = workOrderCloseDAO.closeWorkOrder(workOrderId);
        if (!success) {
            throw new SQLException("Failed to update Work Order status");
        }

        return "Work Order #" + workOrderId + " closed successfully";
    }

    public int countWorkOrdersReadyForClosure(int techManagerId) throws SQLException {
        return workOrderCloseDAO.countWorkOrdersReadyForClosure(techManagerId);
    }
}

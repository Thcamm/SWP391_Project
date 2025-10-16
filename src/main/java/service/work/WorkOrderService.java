package service.work;

import dao.workorder.WorkOrderDAO;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class WorkOrderService {
    private WorkOrderDAO workOrderDAO;

    public WorkOrderService() {
        this.workOrderDAO = new WorkOrderDAO();
    }

    // Create WorkOrder from approved ServiceRequest
    public WorkOrder createWorkOrder(int techManagerId, int requestId, BigDecimal estimateAmount) throws SQLException {
        WorkOrder workOrder = new WorkOrder(techManagerId, requestId, estimateAmount);
        int workOrderId = workOrderDAO.createWorkOrder(workOrder);
        if (workOrderId > 0) {
            workOrder.setWorkOrderId(workOrderId);
            return workOrder;
        }
        return null;
    }

    // Get WorkOrder by ID
    public WorkOrder getWorkOrderById(int workOrderId) throws SQLException {
        return workOrderDAO.getWorkOrderById(workOrderId);
    }

    // Get WorkOrders by TechManager
    public List<WorkOrder> getWorkOrdersByTechManager(int techManagerId) throws SQLException {
        return workOrderDAO.getWorkOrdersByTechManager(techManagerId);
    }

    // Update WorkOrder status
    public boolean updateWorkOrderStatus(int workOrderId, WorkOrder.Status status) throws SQLException {
        return workOrderDAO.updateWorkOrderStatus(workOrderId, status);
    }

    // Add WorkOrderDetail
    public boolean addWorkOrderDetail(WorkOrderDetail detail) throws SQLException {
        return workOrderDAO.addWorkOrderDetail(detail);
    }

    // Calculate total estimate for WorkOrder
    public BigDecimal calculateTotalEstimate(int workOrderId) throws SQLException {
        WorkOrder workOrder = getWorkOrderById(workOrderId);
        if (workOrder != null) {
            return workOrder.calculateTotalEstimate();
        }
        return BigDecimal.ZERO;
    }
}

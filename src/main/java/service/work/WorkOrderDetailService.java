package service.work;

import dao.workorder.WorkOrderDetailDAO;
import model.workorder.WorkOrderDetail;
import model.workorder.WorkOrder;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class WorkOrderDetailService {
    private WorkOrderDetailDAO workOrderDetailDAO;

    public WorkOrderDetailService() {
        this.workOrderDetailDAO = new WorkOrderDetailDAO();
    }

    // Create WorkOrderDetail
    public WorkOrderDetail createWorkOrderDetail(WorkOrderDetail detail) throws SQLException {
        int detailId = workOrderDetailDAO.createWorkOrderDetail(detail);
        if (detailId > 0) {
            detail.setDetailId(detailId);
            return detail;
        }
        return null;
    }

    // Get WorkOrderDetail by ID
    public WorkOrderDetail getWorkOrderDetailById(int detailId) throws SQLException {
        return workOrderDetailDAO.getWorkOrderDetailById(detailId);
    }

    // Get WorkOrderDetails by WorkOrder
    public List<WorkOrderDetail> getWorkOrderDetailsByWorkOrder(int workOrderId) throws SQLException {
        return workOrderDetailDAO.getWorkOrderDetailsByWorkOrder(workOrderId);
    }

    // Update WorkOrderDetail
    public boolean updateWorkOrderDetail(WorkOrderDetail detail) throws SQLException {
        return workOrderDetailDAO.updateWorkOrderDetail(detail);
    }

    // Delete WorkOrderDetail
    public boolean deleteWorkOrderDetail(int detailId) throws SQLException {
        return workOrderDetailDAO.deleteWorkOrderDetail(detailId);
    }

    // Approve WorkOrderDetail
    public boolean approveWorkOrderDetail(int detailId, int approvedByUserId) throws SQLException {
        return workOrderDetailDAO.approveWorkOrderDetail(detailId, approvedByUserId);
    }

    // Decline WorkOrderDetail
    public boolean declineWorkOrderDetail(int detailId, int approvedByUserId) throws SQLException {
        return workOrderDetailDAO.declineWorkOrderDetail(detailId, approvedByUserId);
    }

    // Calculate total estimate for WorkOrderDetails
    public BigDecimal calculateTotalEstimateForWorkOrder(int workOrderId) throws SQLException {
        List<WorkOrderDetail> details = getWorkOrderDetailsByWorkOrder(workOrderId);
        BigDecimal total = BigDecimal.ZERO;
        for (WorkOrderDetail detail : details) {
            if (detail.getEstimateAmount() != null) {
                total = total.add(detail.getEstimateAmount());
            }
        }
        return total;
    }

    // Create WorkOrderDetail with validation
    public WorkOrderDetail createWorkOrderDetail(int workOrderId, WorkOrderDetail.Source source,
            String taskDescription, BigDecimal estimateHours, BigDecimal estimateAmount) throws SQLException {

        if (taskDescription == null || taskDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }

        if (estimateHours == null || estimateHours.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Estimate hours must be greater than 0");
        }

        if (estimateAmount == null || estimateAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Estimate amount cannot be negative");
        }

        WorkOrderDetail detail = new WorkOrderDetail(workOrderId, source, taskDescription.trim());
        detail.setEstimateHours(estimateHours);
        detail.setEstimateAmount(estimateAmount);

        return createWorkOrderDetail(detail);
    }
}
package model.workorder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class WorkOrder {
    private int workOrderId;
    private int techManagerId; // References Employee.EmployeeID
    private int requestId; // References ServiceRequest.RequestID
    private BigDecimal estimateAmount;
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Relationships (lazy loaded)
    private List<WorkOrderDetail> workOrderDetails;
    private model.employee.techmanager.TechManager techManager; // Re-use TechManager
    // private Object serviceRequest; // Re-use ServiceRequest (create when
    // available)

    public enum Status {
        PENDING, IN_PROCESS, COMPLETE
    }

    public WorkOrder() {
        this.workOrderDetails = new ArrayList<>();
    }

    // Constructor with required fields
    public WorkOrder(int techManagerId, int requestId, BigDecimal estimateAmount) {
        this();
        this.techManagerId = techManagerId;
        this.requestId = requestId;
        this.estimateAmount = estimateAmount;
        this.status = Status.PENDING;
    }



    // Getters and Setters
    public int getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }

    public int getTechManagerId() {
        return techManagerId;
    }

    public void setTechManagerId(int techManagerId) {
        this.techManagerId = techManagerId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public BigDecimal getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(BigDecimal estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    // Relationships
    public List<WorkOrderDetail> getWorkOrderDetails() {
        return workOrderDetails;
    }

    public void setWorkOrderDetails(List<WorkOrderDetail> workOrderDetails) {
        this.workOrderDetails = workOrderDetails;
    }

    public void addWorkOrderDetail(WorkOrderDetail detail) {
        this.workOrderDetails.add(detail);
        detail.setWorkOrderId(this.workOrderId);
    }

    public model.employee.techmanager.TechManager getTechManager() {
        return techManager;
    }

    public void setTechManager(model.employee.techmanager.TechManager techManager) {
        this.techManager = techManager;
    }

    // ServiceRequest getter/setter - implement when ServiceRequest model is created
    // public Object getServiceRequest() { return null; }
    // public void setServiceRequest(Object serviceRequest) { }

    // Business methods
    public BigDecimal calculateTotalEstimate() {
        BigDecimal total = this.estimateAmount != null ? this.estimateAmount : BigDecimal.ZERO;
        for (WorkOrderDetail detail : workOrderDetails) {
            if (detail.getEstimateAmount() != null) {
                total = total.add(detail.getEstimateAmount());
            }
        }
        return total;
    }

    public boolean isComplete() {
        return Status.COMPLETE.equals(this.status);
    }
}

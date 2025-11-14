package model.dto;

import model.employee.technician.TaskAssignment;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;
import model.inventory.WorkOrderPart;
import model.customer.Customer;
import model.vehicle.Vehicle;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO để hiển thị chi tiết Work Order cho Customer Service
 */
public class CSWorkOrderDetailView {
    private WorkOrder workOrder;
    private Customer customer;
    private Vehicle vehicle;

    // Danh sách các details (REQUEST và DIAGNOSTIC)
    private List<WorkOrderDetailInfo> details;

    // Danh sách các parts được sử dụng
    private List<PartUsageInfo> parts;

    // Tổng hợp thông tin
    private SummaryInfo summary;

    // Constructor
    public CSWorkOrderDetailView() {
    }

    // Getters and Setters
    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<WorkOrderDetailInfo> getDetails() {
        return details;
    }

    public void setDetails(List<WorkOrderDetailInfo> details) {
        this.details = details;
    }

    public List<PartUsageInfo> getParts() {
        return parts;
    }

    public void setParts(List<PartUsageInfo> parts) {
        this.parts = parts;
    }

    public SummaryInfo getSummary() {
        return summary;
    }

    public void setSummary(SummaryInfo summary) {
        this.summary = summary;
    }

    /**
     * Inner class: Thông tin chi tiết của từng detail
     */
    public static class WorkOrderDetailInfo {
        private WorkOrderDetail detail;
        private String sourceLabel; // "Yêu cầu" hoặc "Chẩn đoán"
        private String statusLabel;
        private String statusColor; // success, warning, danger, secondary
        private List<TaskAssignmentInfo> tasks;
        private String approvalStatusLabel;
        private String approvalStatusColor;
        private String approvedByName;

        // Getters and Setters
        public WorkOrderDetail getDetail() {
            return detail;
        }

        public void setDetail(WorkOrderDetail detail) {
            this.detail = detail;
        }

        public String getSourceLabel() {
            return sourceLabel;
        }

        public void setSourceLabel(String sourceLabel) {
            this.sourceLabel = sourceLabel;
        }

        public String getStatusLabel() {
            return statusLabel;
        }

        public void setStatusLabel(String statusLabel) {
            this.statusLabel = statusLabel;
        }

        public String getStatusColor() {
            return statusColor;
        }

        public void setStatusColor(String statusColor) {
            this.statusColor = statusColor;
        }

        public List<TaskAssignmentInfo> getTasks() {
            return tasks;
        }

        public void setTasks(List<TaskAssignmentInfo> tasks) {
            this.tasks = tasks;
        }

        public String getApprovalStatusLabel() {
            return approvalStatusLabel;
        }

        public void setApprovalStatusLabel(String approvalStatusLabel) {
            this.approvalStatusLabel = approvalStatusLabel;
        }

        public String getApprovalStatusColor() {
            return approvalStatusColor;
        }

        public void setApprovalStatusColor(String approvalStatusColor) {
            this.approvalStatusColor = approvalStatusColor;
        }

        public String getApprovedByName() {
            return approvedByName;
        }

        public void setApprovedByName(String approvedByName) {
            this.approvedByName = approvedByName;
        }
    }

    /**
     * Inner class: Thông tin task assignment
     */
    public static class TaskAssignmentInfo {
        private TaskAssignment task;
        private String technicianName;
        private String taskTypeLabel;
        private String priorityLabel;
        private String priorityColor;
        private String statusLabel;
        private String statusColor;

        // Getters and Setters
        public TaskAssignment getTask() {
            return task;
        }

        public void setTask(TaskAssignment task) {
            this.task = task;
        }

        public String getTechnicianName() {
            return technicianName;
        }

        public void setTechnicianName(String technicianName) {
            this.technicianName = technicianName;
        }

        public String getTaskTypeLabel() {
            return taskTypeLabel;
        }

        public void setTaskTypeLabel(String taskTypeLabel) {
            this.taskTypeLabel = taskTypeLabel;
        }

        public String getPriorityLabel() {
            return priorityLabel;
        }

        public void setPriorityLabel(String priorityLabel) {
            this.priorityLabel = priorityLabel;
        }

        public String getPriorityColor() {
            return priorityColor;
        }

        public void setPriorityColor(String priorityColor) {
            this.priorityColor = priorityColor;
        }

        public String getStatusLabel() {
            return statusLabel;
        }

        public void setStatusLabel(String statusLabel) {
            this.statusLabel = statusLabel;
        }

        public String getStatusColor() {
            return statusColor;
        }

        public void setStatusColor(String statusColor) {
            this.statusColor = statusColor;
        }
    }

    /**
     * Inner class: Thông tin part được sử dụng
     */
    public static class PartUsageInfo {
        private WorkOrderPart part;
        private String partName;
        private String statusLabel;
        private String statusColor;
        private String requestedByName;

        // Getters and Setters
        public WorkOrderPart getPart() {
            return part;
        }

        public void setPart(WorkOrderPart part) {
            this.part = part;
        }

        public String getPartName() {
            return partName;
        }

        public void setPartName(String partName) {
            this.partName = partName;
        }

        public String getStatusLabel() {
            return statusLabel;
        }

        public void setStatusLabel(String statusLabel) {
            this.statusLabel = statusLabel;
        }

        public String getStatusColor() {
            return statusColor;
        }

        public void setStatusColor(String statusColor) {
            this.statusColor = statusColor;
        }

        public String getRequestedByName() {
            return requestedByName;
        }

        public void setRequestedByName(String requestedByName) {
            this.requestedByName = requestedByName;
        }
    }

    /**
     * Inner class: Tổng hợp thông tin
     */
    public static class SummaryInfo {
        private int totalDetails;
        private int completedDetails;
        private int totalTasks;
        private int completedTasks;
        private int totalParts;
        private int deliveredParts;
        private BigDecimal totalEstimatedAmount;
        private BigDecimal totalActualAmount;
        private int pendingApprovals;

        // Getters and Setters
        public int getTotalDetails() {
            return totalDetails;
        }

        public void setTotalDetails(int totalDetails) {
            this.totalDetails = totalDetails;
        }

        public int getCompletedDetails() {
            return completedDetails;
        }

        public void setCompletedDetails(int completedDetails) {
            this.completedDetails = completedDetails;
        }

        public int getTotalTasks() {
            return totalTasks;
        }

        public void setTotalTasks(int totalTasks) {
            this.totalTasks = totalTasks;
        }

        public int getCompletedTasks() {
            return completedTasks;
        }

        public void setCompletedTasks(int completedTasks) {
            this.completedTasks = completedTasks;
        }

        public int getTotalParts() {
            return totalParts;
        }

        public void setTotalParts(int totalParts) {
            this.totalParts = totalParts;
        }

        public int getDeliveredParts() {
            return deliveredParts;
        }

        public void setDeliveredParts(int deliveredParts) {
            this.deliveredParts = deliveredParts;
        }

        public BigDecimal getTotalEstimatedAmount() {
            return totalEstimatedAmount;
        }

        public void setTotalEstimatedAmount(BigDecimal totalEstimatedAmount) {
            this.totalEstimatedAmount = totalEstimatedAmount;
        }

        public BigDecimal getTotalActualAmount() {
            return totalActualAmount;
        }

        public void setTotalActualAmount(BigDecimal totalActualAmount) {
            this.totalActualAmount = totalActualAmount;
        }

        public int getPendingApprovals() {
            return pendingApprovals;
        }

        public void setPendingApprovals(int pendingApprovals) {
            this.pendingApprovals = pendingApprovals;
        }
    }
}
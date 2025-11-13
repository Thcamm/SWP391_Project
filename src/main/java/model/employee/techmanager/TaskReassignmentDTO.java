package model.employee.techmanager;

import java.time.LocalDateTime;

/**
 * DTO for cancelled tasks that need reassignment.
 * Includes both overdue tasks and declined tasks.
 * 
 * @author An
 * @version 1.0
 */
public class TaskReassignmentDTO {
    private int assignmentId;
    private int workOrderId; // For grouping tasks by WorkOrder
    private int detailId; // WorkOrderDetail ID
    private String taskType;
    private String plannedStart;
    private String plannedEnd;
    private LocalDateTime plannedStartRaw; // For form default value
    private LocalDateTime plannedEndRaw; // For form default value
    private String assignedDate;
    private String declinedAt;
    private String declineReason;
    private String taskDescription;
    private String vehicleInfo;
    private String technicianName;
    private String customerName;
    private String cancelReasonType; // 'DECLINED' or 'OVERDUE'

    // Getters and Setters
    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(String plannedStart) {
        this.plannedStart = plannedStart;
    }

    public String getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(String plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    public LocalDateTime getPlannedStartRaw() {
        return plannedStartRaw;
    }

    public void setPlannedStartRaw(LocalDateTime plannedStartRaw) {
        this.plannedStartRaw = plannedStartRaw;
    }

    public LocalDateTime getPlannedEndRaw() {
        return plannedEndRaw;
    }

    public void setPlannedEndRaw(LocalDateTime plannedEndRaw) {
        this.plannedEndRaw = plannedEndRaw;
    }

    public String getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(String assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getDeclinedAt() {
        return declinedAt;
    }

    public void setDeclinedAt(String declinedAt) {
        this.declinedAt = declinedAt;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCancelReasonType() {
        return cancelReasonType;
    }

    public void setCancelReasonType(String cancelReasonType) {
        this.cancelReasonType = cancelReasonType;
    }
}

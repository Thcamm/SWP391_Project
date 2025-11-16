package model.employee.techmanager;

import java.sql.Timestamp;

/**
 * DTO for Work Orders in closure management (GĐ7)
 * Contains information about work orders and their task status
 * 
 * @author SWP391 Team
 * @version 2.0 (Added cancelled and active tasks tracking)
 */
public class WorkOrderCloseDTO {
    private int workOrderID;
    private int requestID;
    private String vehicleInfo;
    private String customerName;
    private String techManagerName;
    private int totalTasks;
    private int completedTasks;
    private int cancelledTasks;
    private int activeTasks; // ASSIGNED + IN_PROGRESS
    private int daysOpen;
    private Timestamp createdAt;
    private boolean isAllTasksComplete;

    // Constructors
    public WorkOrderCloseDTO() {
    }

    // Getters and Setters
    public int getWorkOrderID() {
        return workOrderID;
    }

    public void setWorkOrderID(int workOrderID) {
        this.workOrderID = workOrderID;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTechManagerName() {
        return techManagerName;
    }

    public void setTechManagerName(String techManagerName) {
        this.techManagerName = techManagerName;
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

    public int getCancelledTasks() {
        return cancelledTasks;
    }

    public void setCancelledTasks(int cancelledTasks) {
        this.cancelledTasks = cancelledTasks;
    }

    public int getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(int activeTasks) {
        this.activeTasks = activeTasks;
    }

    public int getDaysOpen() {
        return daysOpen;
    }

    public void setDaysOpen(int daysOpen) {
        this.daysOpen = daysOpen;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAllTasksComplete() {
        return isAllTasksComplete;
    }

    public void setAllTasksComplete(boolean allTasksComplete) {
        isAllTasksComplete = allTasksComplete;
    }

    @Override
    public String toString() {
        return "WorkOrderCloseDTO{" +
                "workOrderID=" + workOrderID +
                ", vehicleInfo='" + vehicleInfo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalTasks=" + totalTasks +
                ", completedTasks=" + completedTasks +
                ", cancelledTasks=" + cancelledTasks +
                ", activeTasks=" + activeTasks +
                ", isAllTasksComplete=" + isAllTasksComplete +
                '}';
    }
}

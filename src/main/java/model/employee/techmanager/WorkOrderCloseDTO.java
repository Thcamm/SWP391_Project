package model.employee.techmanager;

/**
 * DTO for WorkOrder ready to close (GĐ7).
 * Used by WorkOrderCloseService to display work orders that have all tasks
 * completed.
 * 
 * @author SWP391 Team
 * @version 1.0
 */
public class WorkOrderCloseDTO {
    private int workOrderID;
    private int requestID;
    private String vehicleInfo;
    private String customerName;
    private int totalTasks;
    private int completedTasks;
    private java.sql.Timestamp createdAt;
    private String techManagerName;

    // Constructors
    public WorkOrderCloseDTO() {
    }

    public WorkOrderCloseDTO(int workOrderID, int requestID, String vehicleInfo,
            String customerName, int totalTasks, int completedTasks,
            java.sql.Timestamp createdAt, String techManagerName) {
        this.workOrderID = workOrderID;
        this.requestID = requestID;
        this.vehicleInfo = vehicleInfo;
        this.customerName = customerName;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.createdAt = createdAt;
        this.techManagerName = techManagerName;
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

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getTechManagerName() {
        return techManagerName;
    }

    public void setTechManagerName(String techManagerName) {
        this.techManagerName = techManagerName;
    }

    // Business logic methods
    public boolean isAllTasksComplete() {
        return totalTasks > 0 && totalTasks == completedTasks;
    }

    public int getDaysOpen() {
        if (createdAt == null)
            return 0;
        long diff = System.currentTimeMillis() - createdAt.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    @Override
    public String toString() {
        return "WorkOrderCloseDTO{" +
                "workOrderID=" + workOrderID +
                ", requestID=" + requestID +
                ", vehicleInfo='" + vehicleInfo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalTasks=" + totalTasks +
                ", completedTasks=" + completedTasks +
                ", daysOpen=" + getDaysOpen() +
                '}';
    }
}

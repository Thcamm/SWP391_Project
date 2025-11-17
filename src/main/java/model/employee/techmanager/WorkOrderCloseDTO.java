package model.employee.techmanager;

import java.sql.Timestamp;

/**
 * DTO for WorkOrder ready to close (GĐ7).
 * [REFACTORED] Now uses 'activeTasks' for logic, not 'completedTasks'.
 *
 * @author SWP391 Team
 * @version 3.0 (Unified Logic)
 */
public class WorkOrderCloseDTO {
    private int workOrderID;
    private int requestID;
    private String vehicleInfo;
    private String customerName;
    private int totalTasks;
    private int completedTasks;
    private Timestamp createdAt;
    private String techManagerName;

    // --- TRƯỜNG MỚI (NEW FIELDS) ---
    private int activeTasks; // (ASSIGNED + IN_PROGRESS)
    private int cancelledTasks; // (CANCELLED + DECLINED)
    private int daysOpen;
    // -------------------------

    // Constructors
    public WorkOrderCloseDTO() {
    }

    // --- Getters and Setters ---

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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getTechManagerName() {
        return techManagerName;
    }

    public void setTechManagerName(String techManagerName) {
        this.techManagerName = techManagerName;
    }

    public int getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(int activeTasks) {
        this.activeTasks = activeTasks;
    }

    public int getCancelledTasks() {
        return cancelledTasks;
    }

    public void setCancelledTasks(int cancelledTasks) {
        this.cancelledTasks = cancelledTasks;
    }

    public int getDaysOpen() {
        return daysOpen;
    }

    public void setDaysOpen(int daysOpen) {
        this.daysOpen = daysOpen;
    }

    // --- LOGIC NGHIỆP VỤ ---

    /**
     * [LOGIC ĐÚNG]
     * Kiểm tra xem WorkOrder có Sẵn sàng để Đóng không.
     * Điều kiện: Phải có task (Total > 0) VÀ không còn task nào đang chạy (Active =
     * 0).
     */
    public boolean isReadyToClose() {
        // activeTasks (từ DAO) đếm cả ASSIGNED và IN_PROGRESS
        return totalTasks > 0 && activeTasks == 0;
    }

    @Override
    public String toString() {
        return "WorkOrderCloseDTO{" +
                "workOrderID=" + workOrderID +
                ", totalTasks=" + totalTasks +
                ", completedTasks=" + completedTasks +
                ", activeTasks=" + activeTasks +
                ", cancelledTasks=" + cancelledTasks +
                ", isReadyToClose=" + isReadyToClose() +
                '}';
    }
}
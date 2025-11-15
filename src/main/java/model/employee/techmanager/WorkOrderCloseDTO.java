package model.employee.techmanager;

import java.sql.Timestamp;

/**
 * DTO for WorkOrder ready to close (GĐ7).
 * [REFACTORED] Now uses 'activeTasks' for logic, not 'completedTasks'.
 * * @author SWP391 Team
 * 
 * @version 2.0 (Logic Fixed)
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

    // --- TRƯỜNG MỚI (NEW FIELD) ---
    // (Được cung cấp bởi DAO query)
    private int activeTasks; // Số task đang 'ASSIGNED' hoặc 'IN_PROGRESS'
    // -------------------------

    // Constructors (Giữ nguyên)
    public WorkOrderCloseDTO() {
    }

    // Getters and Setters (Giữ nguyên)
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

    // --- GETTER/SETTER MỚI ---
    public int getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(int activeTasks) {
        this.activeTasks = activeTasks;
    }
    // -------------------------

    // --- SỬA LẠI LOGIC NGHIỆP VỤ ---

    /**
     * [LOGIC CŨ - BỊ SAI]
     * (Hàm này sai vì 1 WOD có 1 COMPLETE và 1 CANCELLED sẽ bị false)
     */
    @Deprecated
    public boolean isAllTasksComplete() {
        return totalTasks > 0 && totalTasks == completedTasks;
    }

    /**
     * [LOGIC MỚI - ĐÚNG]
     * Kiểm tra xem WorkOrder có Sẵn sàng để Đóng không.
     * Điều kiện: Phải có task VÀ không còn task nào đang chạy.
     */
    public boolean isReadyToClose() {
        // Chỉ cần kiểm tra không còn task nào đang 'ASSIGNED' hoặc 'IN_PROGRESS'
        return totalTasks > 0 && activeTasks == 0;
    }
    // -----------------------------

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
                ", totalTasks=" + totalTasks +
                ", completedTasks=" + completedTasks +
                ", activeTasks=" + activeTasks + // Thêm vào log
                ", isReadyToClose=" + isReadyToClose() + // Dùng logic mới
                '}';
    }
}
package model.dto;

import java.sql.Timestamp;

/**
 * DTO này đại diện cho MỘT HÀNG trong trang "Danh sách lịch sử sửa chữa".
 * Nó chứa thông tin tóm tắt về một quy trình.
 */
public class  RepairJourneySummaryDTO {

    private int requestID;     // Dùng để tạo link chi tiết
    private int vehicleID;
    private String fullName;
    private String vehicleLicensePlate;
    private String entryType;  // "Appointment" hoặc "Walk-in"
    private Timestamp entryDate; // Ngày bắt đầu (là ngày hẹn hoặc ngày request)
    private String latestStage;  // "Service Request", "Work Order", "Invoice"
    private String latestStatus; // "PENDING", "IN_PROGRESS", "PAID"...

    // Constructors
    public RepairJourneySummaryDTO() {}

    // Getters and Setters cho tất cả các trường

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getVehicleLicensePlate() {
        return vehicleLicensePlate;
    }

    public void setVehicleLicensePlate(String vehicleLicensePlate) {
        this.vehicleLicensePlate = vehicleLicensePlate;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public Timestamp getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Timestamp entryDate) {
        this.entryDate = entryDate;
    }

    public String getLatestStage() {
        return latestStage;
    }

    public void setLatestStage(String latestStage) {
        this.latestStage = latestStage;
    }

    public String getLatestStatus() {
        return latestStatus;
    }

    public void setLatestStatus(String latestStatus) {
        this.latestStatus = latestStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
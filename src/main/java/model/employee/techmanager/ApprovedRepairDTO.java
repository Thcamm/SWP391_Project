package model.employee.techmanager;

import java.sql.Timestamp;

/**
 * DTO for approved Repairs waiting for repair assignment (GĐ5).
 * 
 * LUỒNG MỚI (Triage Workflow):
 * - Contains WODs from BOTH sources:
 *   1. REQUEST: Direct from GĐ2 Triage (classified as direct repair)
 *   2. DIAGNOSTIC: From GĐ4 Quote Approval (customer approved quote)
 * 
 * @author SWP391 Team
 * @version 2.0 (Updated for LUỒNG MỚI)
 */
public class ApprovedRepairDTO {
    private int detailId;
    private int workOrderId;
    private String taskDescription;
    private double estimateAmount;
    private Timestamp approvedAt;
    private int diagnosticId;
    private int vehicleId;
    private String licensePlate;
    private String vehicleModel;
    private String customerName;
    private String phoneNumber;

    // Constructors
    public ApprovedRepairDTO() {
    }

    // Getters and Setters
    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(int workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public double getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(double estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    public int getDiagnosticId() {
        return diagnosticId;
    }

    public void setDiagnosticId(int diagnosticId) {
        this.diagnosticId = diagnosticId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "ApprovedRepairDTO{" +
                "detailId=" + detailId +
                ", workOrderId=" + workOrderId +
                ", taskDescription='" + taskDescription + '\'' +
                ", estimateAmount=" + estimateAmount +
                ", approvedAt=" + approvedAt +
                ", diagnosticId=" + diagnosticId +
                ", vehicleId=" + vehicleId +
                ", licensePlate='" + licensePlate + '\'' +
                ", vehicleModel='" + vehicleModel + '\'' +
                ", customerName='" + customerName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

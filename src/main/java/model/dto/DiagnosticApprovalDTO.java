package model.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO for VehicleDiagnostic records pending customer approval (GĐ3).
 * Used by Tech Manager to monitor quotes awaiting customer decision.
 *
 * @author SWP391 Team
 * @version 1.0
 */
public class DiagnosticApprovalDTO {
    
    private int diagnosticID;
    private int workOrderID;
    private String vehicleInfo; // e.g., "Toyota Camry - 59A-12345"
    private String customerName;
    private String customerPhone;
    private String issueFound;
    private BigDecimal estimateCost;
    private String status; // SUBMITTED, APPROVED, REJECTED
    private Timestamp createdAt;
    private int daysPending; // Days since submission
    
    // Technician who created the diagnostic
    private String technicianName;
    private int technicianID;

    // Constructors
    public DiagnosticApprovalDTO() {
    }

    // Getters and Setters
    public int getDiagnosticID() {
        return diagnosticID;
    }

    public void setDiagnosticID(int diagnosticID) {
        this.diagnosticID = diagnosticID;
    }

    public int getWorkOrderID() {
        return workOrderID;
    }

    public void setWorkOrderID(int workOrderID) {
        this.workOrderID = workOrderID;
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

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getIssueFound() {
        return issueFound;
    }

    public void setIssueFound(String issueFound) {
        this.issueFound = issueFound;
    }

    public BigDecimal getEstimateCost() {
        return estimateCost;
    }

    public void setEstimateCost(BigDecimal estimateCost) {
        this.estimateCost = estimateCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getDaysPending() {
        return daysPending;
    }

    public void setDaysPending(int daysPending) {
        this.daysPending = daysPending;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public int getTechnicianID() {
        return technicianID;
    }

    public void setTechnicianID(int technicianID) {
        this.technicianID = technicianID;
    }

    /**
     * Get status badge class for UI.
     */
    public String getStatusBadgeClass() {
        switch (status) {
            case "SUBMITTED":
                return "bg-warning text-dark";
            case "APPROVED":
                return "bg-success";
            case "REJECTED":
                return "bg-danger";
            default:
                return "bg-secondary";
        }
    }

    /**
     * Check if diagnostic is overdue (more than 2 days pending).
     */
    public boolean isOverdue() {
        return daysPending > 2;
    }
}

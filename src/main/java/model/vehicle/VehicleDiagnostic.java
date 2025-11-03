package model.vehicle;

import model.inventory.DiagnosticPart;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Model cho Vehicle Diagnostic (Chẩn đoán xe)
 * Được tạo bởi technician sau khi kiểm tra xe
 * Bao gồm: vấn đề phát hiện, estimate cost, danh sách parts cần thay
 *
 * @author vuthithuy-qh
 * @version 2.0
 * @since 2025-11-01
 */
public class VehicleDiagnostic {

    // core fields
    private int vehicleDiagnosticID;
    private int assignmentID;
    private String issueFound;
    private BigDecimal estimateCost;
    private boolean status;
    private LocalDateTime createdAt;
    private boolean isSubmitApproval;

    private transient BigDecimal laborCostCalculated;

    private transient BigDecimal laborCostInput; //Dùng khi create/update để truyền labor cost





    // transient fields for display
    private String vehicleInfo;
    private String technicianName;

    // relationships
    private List<DiagnosticPart> parts = new ArrayList<>();
    private List<DiagnosticTechnician> technicians = new ArrayList<>();
    private BigDecimal totalPartsCost = BigDecimal.ZERO;


    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");



    public VehicleDiagnostic() {}

    public VehicleDiagnostic(int assignmentID, String issueFound, BigDecimal estimateCost) {
        this.assignmentID = assignmentID;
        this.issueFound = issueFound;
        this.estimateCost = estimateCost;
        this.status = true;
        this.createdAt = LocalDateTime.now();
    }

    public BigDecimal getLaborCostCalculated() {
        return laborCostCalculated;
    }

    public void setLaborCostCalculated(BigDecimal laborCostCalculated) {
        this.laborCostCalculated = laborCostCalculated;
    }

    public BigDecimal getLaborCostInput() {
        return laborCostInput;
    }

    public void setLaborCostInput(BigDecimal laborCostInput) {
        this.laborCostInput = laborCostInput;
    }
    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(DTF) : "-";
    }

    public String getEstimateCostFormatted() {
        return estimateCost != null ? "$" + estimateCost.toString() : "$0.00";
    }

    public String getTotalEstimateFormatted() {
        return "$" + getTotalEstimate().toString();
    }




    public BigDecimal getTotalEstimate() {
        return (estimateCost != null ? estimateCost : BigDecimal.ZERO)
                .add(totalPartsCost);
    }


    public int getRequiredPartsCount() {
        return (int) parts.stream()
                .filter(p -> p.getPartCondition() == DiagnosticPart.PartCondition.REQUIRED)
                .count();
    }



    public int getRecommendedPartsCount() {
        return (int) parts.stream()
                .filter(p -> p.getPartCondition() == DiagnosticPart.PartCondition.RECOMMENDED)
                .count();
    }


    public int getOptionalPartsCount() {
        return (int) parts.stream()
                .filter(p -> p.getPartCondition() == DiagnosticPart.PartCondition.OPTIONAL)
                .count();
    }


    public boolean hasOutOfStockParts() {
        return parts.stream().anyMatch(p -> !p.isInStock());
    }


    public boolean hasApprovedParts() {
        return parts.stream().anyMatch(DiagnosticPart::isApproved);
    }


    public int getApprovedPartsCount() {
        return (int) parts.stream()
                .filter(DiagnosticPart::isApproved)
                .count();
    }


    public BigDecimal getApprovedPartsCost() {
        return parts.stream()
                .filter(DiagnosticPart::isApproved)
                .map(DiagnosticPart::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public boolean allPartsApproved() {
        if (parts.isEmpty()) return false;
        return parts.stream().allMatch(DiagnosticPart::isApproved);
    }


    public List<DiagnosticPart> getPartsByCondition(DiagnosticPart.PartCondition condition) {
        return parts.stream()
                .filter(p -> p.getPartCondition() == condition)
                .collect(Collectors.toList());
    }


    public DiagnosticTechnician getLeadTechnician() {
        return technicians.stream()
                .filter(DiagnosticTechnician::isLead)
                .findFirst()
                .orElse(null);
    }


    public double getTotalHoursSpent() {
        return technicians.stream()
                .mapToDouble(DiagnosticTechnician::getHoursSpent)
                .sum();
    }

    // ===== VALIDATION =====


    public boolean isValid() {
        return assignmentID > 0
                && issueFound != null && !issueFound.trim().isEmpty()
                && estimateCost != null && estimateCost.compareTo(BigDecimal.ZERO) >= 0;
    }


    public List<String> getValidationErrors() {
        List<String> errors = new ArrayList<>();

        if (assignmentID <= 0) {
            errors.add("Assignment ID is required");
        }

        if (issueFound == null || issueFound.trim().isEmpty()) {
            errors.add("Issue description is required");
        }

        if (estimateCost == null || estimateCost.compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Estimate cost must be non-negative");
        }

        return errors;
    }

    // ===== GETTERS & SETTERS =====

    public int getVehicleDiagnosticID() { return vehicleDiagnosticID; }
    public void setVehicleDiagnosticID(int vehicleDiagnosticID) {
        this.vehicleDiagnosticID = vehicleDiagnosticID;
    }

    public int getAssignmentID() { return assignmentID; }
    public void setAssignmentID(int assignmentID) { this.assignmentID = assignmentID; }

    public String getIssueFound() { return issueFound; }
    public void setIssueFound(String issueFound) { this.issueFound = issueFound; }

    public BigDecimal getEstimateCost() { return estimateCost; }
    public void setEstimateCost(BigDecimal estimateCost) { this.estimateCost = estimateCost; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }

    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }

    public List<DiagnosticPart> getParts() { return parts; }

    public void setParts(List<DiagnosticPart> parts) {
        this.parts = parts;
        // Tự động tính tổng parts cost
        this.totalPartsCost = parts.stream()
                .map(DiagnosticPart::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPartsCost() { return totalPartsCost; }
    public void setTotalPartsCost(BigDecimal totalPartsCost) {
        this.totalPartsCost = totalPartsCost;
    }

    public List<DiagnosticTechnician> getTechnicians() { return technicians; }
    public void setTechnicians(List<DiagnosticTechnician> technicians) {
        this.technicians = technicians;
    }

    public boolean isSubmitApproval() { return isSubmitApproval; }
    public void setSubmitApproval(boolean submitApproval) {
        isSubmitApproval = submitApproval;
    }

    // ===== OVERRIDE METHODS =====

    @Override
    public String toString() {
        return "VehicleDiagnostic{" +
                "id=" + vehicleDiagnosticID +
                ", assignmentID=" + assignmentID +
                ", vehicle='" + vehicleInfo + '\'' +
                ", technician='" + technicianName + '\'' +
                ", issue='" + (issueFound != null && issueFound.length() > 50
                ? issueFound.substring(0, 47) + "..."
                : issueFound) + '\'' +
                ", estimateCost=" + estimateCost +
                ", totalPartsCost=" + totalPartsCost +
                ", totalEstimate=" + getTotalEstimate() +
                ", partsCount=" + parts.size() +
                ", techniciansCount=" + technicians.size() +
                ", status=" + status +
                ", createdAt=" + getCreatedAtFormatted() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VehicleDiagnostic that = (VehicleDiagnostic) o;
        return vehicleDiagnosticID == that.vehicleDiagnosticID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(vehicleDiagnosticID);
    }

    // ===== INNER CLASS: DiagnosticTechnician =====

    public static class DiagnosticTechnician {
        private int technicianID;
        private String technicianName;
        private boolean isLead;
        private double hoursSpent;

        public DiagnosticTechnician() {}

        public DiagnosticTechnician(int technicianID, String technicianName,
                                    boolean isLead, double hoursSpent) {
            this.technicianID = technicianID;
            this.technicianName = technicianName;
            this.isLead = isLead;
            this.hoursSpent = hoursSpent;
        }

        public String getHoursSpentFormatted() {
            return String.format("%.1f hours", hoursSpent);
        }

        public String getRoleDisplay() {
            return isLead ? "Lead Technician" : "Technician";
        }

        // Getters & Setters
        public int getTechnicianID() { return technicianID; }
        public void setTechnicianID(int technicianID) {
            this.technicianID = technicianID;
        }

        public String getTechnicianName() { return technicianName; }
        public void setTechnicianName(String technicianName) {
            this.technicianName = technicianName;
        }

        public boolean isLead() { return isLead; }
        public void setLead(boolean lead) { isLead = lead; }

        public double getHoursSpent() { return hoursSpent; }
        public void setHoursSpent(double hoursSpent) {
            this.hoursSpent = hoursSpent;
        }

        @Override
        public String toString() {
            return technicianName + " (" + getRoleDisplay() + ") - " +
                    getHoursSpentFormatted();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DiagnosticTechnician that = (DiagnosticTechnician) o;
            return technicianID == that.technicianID;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(technicianID);
        }
    }
}


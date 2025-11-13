package model.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class WorkOrderDetailView {

    private int detailID;
    private int workOrderID;
    private String source; // REQUEST or DIAGNOSTIC
    private Integer diagnosticID; // nếu source = DIAGNOSTIC
    private String approvalStatus; // PENDING, APPROVED, DECLINED
    private String taskDescription;
    private BigDecimal estimateAmount;
    private String detailStatus;

    private Integer assignmentID;
    private Integer technicianID;
    private String technicianName;
    private String taskStatus; // ASSIGNED, IN_PROGRESS, COMPLETE, DECLINED
    private Timestamp assignedDate;
    private Timestamp startAt;
    private Timestamp completeAt;

    private Integer vehicleDiagnosticID;
    private String issueFound;
    private BigDecimal diagnosticEstimateCost;
    private String diagnosticStatus; // SUBMITTED, APPROVED, REJECTED
    private String rejectReason;

    private List<DiagnosticPartView> diagnosticParts = new ArrayList<>();

    private BigDecimal approvedPartsCost = BigDecimal.ZERO;
    private int totalPartsCount = 0;
    private int approvedPartsCount = 0;

    public WorkOrderDetailView() {}

    public int getDetailID() { return detailID; }
    public void setDetailID(int detailID) { this.detailID = detailID; }

    public int getWorkOrderID() { return workOrderID; }
    public void setWorkOrderID(int workOrderID) { this.workOrderID = workOrderID; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Integer getDiagnosticID() { return diagnosticID; }
    public void setDiagnosticID(Integer diagnosticID) { this.diagnosticID = diagnosticID; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public BigDecimal getEstimateAmount() { return estimateAmount; }
    public void setEstimateAmount(BigDecimal estimateAmount) { this.estimateAmount = estimateAmount; }

    public String getDetailStatus() { return detailStatus; }
    public void setDetailStatus(String detailStatus) { this.detailStatus = detailStatus; }

    public Integer getAssignmentID() { return assignmentID; }
    public void setAssignmentID(Integer assignmentID) { this.assignmentID = assignmentID; }

    public Integer getTechnicianID() { return technicianID; }
    public void setTechnicianID(Integer technicianID) { this.technicianID = technicianID; }

    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }

    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }

    public Timestamp getAssignedDate() { return assignedDate; }
    public void setAssignedDate(Timestamp assignedDate) { this.assignedDate = assignedDate; }

    public Timestamp getStartAt() { return startAt; }
    public void setStartAt(Timestamp startAt) { this.startAt = startAt; }

    public Timestamp getCompleteAt() { return completeAt; }
    public void setCompleteAt(Timestamp completeAt) { this.completeAt = completeAt; }

    public Integer getVehicleDiagnosticID() { return vehicleDiagnosticID; }
    public void setVehicleDiagnosticID(Integer vehicleDiagnosticID) {
        this.vehicleDiagnosticID = vehicleDiagnosticID;
    }

    public String getIssueFound() { return issueFound; }
    public void setIssueFound(String issueFound) { this.issueFound = issueFound; }

    public BigDecimal getDiagnosticEstimateCost() { return diagnosticEstimateCost; }
    public void setDiagnosticEstimateCost(BigDecimal diagnosticEstimateCost) {
        this.diagnosticEstimateCost = diagnosticEstimateCost;
    }

    public String getDiagnosticStatus() { return diagnosticStatus; }
    public void setDiagnosticStatus(String diagnosticStatus) {
        this.diagnosticStatus = diagnosticStatus;
    }

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }

    public List<DiagnosticPartView> getDiagnosticParts() { return diagnosticParts; }
    public void setDiagnosticParts(List<DiagnosticPartView> diagnosticParts) {
        this.diagnosticParts = diagnosticParts;
    }

    public BigDecimal getApprovedPartsCost() { return approvedPartsCost; }
    public int getTotalPartsCount() { return totalPartsCount; }
    public int getApprovedPartsCount() { return approvedPartsCount; }

    /**
     * Tính tổng chi phí của các parts đã được approve
     */
    public void calculateApprovedPartsCost() {
        approvedPartsCost = BigDecimal.ZERO;
        totalPartsCount = diagnosticParts.size();
        approvedPartsCount = 0;

        for (DiagnosticPartView part : diagnosticParts) {
            if (part.getIsApproved() == 1) {
                approvedPartsCount++;
                BigDecimal partTotal = part.getUnitPrice()
                        .multiply(BigDecimal.valueOf(part.getQuantityNeeded()));
                approvedPartsCost = approvedPartsCost.add(partTotal);
            }
        }
    }


}

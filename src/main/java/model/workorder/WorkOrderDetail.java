package model.workorder;

import java.sql.Date;

public class WorkOrderDetail {
    private int detailId;
    private int workOrderId;
    private String source; // REQUEST, DIAGNOSTIC
    private Integer diagnosticId;
    private String approvalStatus; // PENDING, APPROVED, DECLINED
    private Integer approvedByUserId;
    private Date approvedAt;
    private String taskDescription;
    private Double estimateHours;
    private Double estimateAmount;
    private Double actualHours;

    public WorkOrderDetail() {
    }

    public WorkOrderDetail(int detailId, int workOrderId, String source, Integer diagnosticId,
                           String approvalStatus, Integer approvedByUserId, Date approvedAt,
                           String taskDescription, Double estimateHours, Double estimateAmount, Double actualHours) {
        this.detailId = detailId;
        this.workOrderId = workOrderId;
        this.source = source;
        this.diagnosticId = diagnosticId;
        this.approvalStatus = approvalStatus;
        this.approvedByUserId = approvedByUserId;
        this.approvedAt = approvedAt;
        this.taskDescription = taskDescription;
        this.estimateHours = estimateHours;
        this.estimateAmount = estimateAmount;
        this.actualHours = actualHours;
    }

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getDiagnosticId() {
        return diagnosticId;
    }

    public void setDiagnosticId(Integer diagnosticId) {
        this.diagnosticId = diagnosticId;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getApprovedByUserId() {
        return approvedByUserId;
    }

    public void setApprovedByUserId(Integer approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }

    public Date getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Date approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Double getEstimateHours() {
        return estimateHours;
    }

    public void setEstimateHours(Double estimateHours) {
        this.estimateHours = estimateHours;
    }

    public Double getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(Double estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public Double getActualHours() {
        return actualHours;
    }

    public void setActualHours(Double actualHours) {
        this.actualHours = actualHours;
    }

    @Override
    public String toString() {
        return "WorkOrderDetail{" +
                "detailId=" + detailId +
                ", workOrderId=" + workOrderId +
                ", source='" + source + '\'' +
                ", diagnosticId=" + diagnosticId +
                ", approvalStatus='" + approvalStatus + '\'' +
                ", approvedByUserId=" + approvedByUserId +
                ", approvedAt=" + approvedAt +
                ", taskDescription='" + taskDescription + '\'' +
                ", estimateHours=" + estimateHours +
                ", estimateAmount=" + estimateAmount +
                ", actualHours=" + actualHours +
                '}';
    }
}

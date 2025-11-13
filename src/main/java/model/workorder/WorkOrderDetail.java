package model.workorder;

import model.employee.technician.TaskAssignment;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public class WorkOrderDetail {
    private int detailId;
    private int workOrderId;
    private String source;
    private Integer diagnosticId;
    private String approvalStatus;
    private Integer approvedByUserId;
    private Timestamp approvedAt;
    private String taskDescription;
    private BigDecimal estimateHours;
    private BigDecimal estimateAmount;
    private BigDecimal actualHours;
    private String detailStatus;

    // Relationships
    private List<TaskAssignment> taskAssignments;


    public WorkOrderDetail() {
        this.taskAssignments = new ArrayList<>();
    }

    public WorkOrderDetail(int workOrderId, String source, String taskDescription) {
        this();
        this.workOrderId = workOrderId;
        this.source = source;
        this.taskDescription = taskDescription;
        this.approvalStatus = approvalStatus;
    }

    // Getters and Setters
    public int getDetailId() {
        return detailId;
    }

    public String getDetailStatus() {
        return detailStatus;
    }

    public void setDetailStatus(String detailStatus) {
        this.detailStatus = detailStatus;
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

    public Timestamp getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Timestamp approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public BigDecimal getEstimateHours() {
        return estimateHours;
    }

    public void setEstimateHours(BigDecimal estimateHours) {
        this.estimateHours = estimateHours;
    }

    public BigDecimal getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(BigDecimal estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public BigDecimal getActualHours() {
        return actualHours;
    }

    public void setActualHours(BigDecimal actualHours) {
        this.actualHours = actualHours;
    }

}

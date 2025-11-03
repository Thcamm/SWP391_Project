package model.inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class WorkOrderPart {
    private int workOrderPartId;
    private int detailID;
    private int partDetailID;
    private int requestByID;

    private Integer quantityUsed;
    private BigDecimal unitPrice;
    private String requestStatus;
    private LocalDateTime requestedAt;

    //
    private String priority;
    private String reason;

    private String partName;
    private String partCode;
    private String sku;
    private Integer detailQuantityAvailable;
    private int taskAssignmentID;
    private String vehicleInfo;
    private String requestedByName;

    public int getWorkOrderPartId() {
        return workOrderPartId;
    }

    public void setWorkOrderPartId(int workOrderPartId) {
        this.workOrderPartId = workOrderPartId;
    }

    public int getDetailID() {
        return detailID;
    }

    public void setDetailID(int detailID) {
        this.detailID = detailID;
    }

    public int getPartDetailID() {
        return partDetailID;
    }

    public void setPartDetailID(int partDetailID) {
        this.partDetailID = partDetailID;
    }

    public int getRequestByID() {
        return requestByID;
    }

    public void setRequestByID(int requestByID) {
        this.requestByID = requestByID;
    }

    public Integer getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(Integer quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getDetailQuantityAvailable() {
        return detailQuantityAvailable;
    }

    public void setDetailQuantityAvailable(Integer detailQuantityAvailable) {
        this.detailQuantityAvailable = detailQuantityAvailable;
    }

    public int getTaskAssignmentID() {
        return taskAssignmentID;
    }

    public void setTaskAssignmentID(int taskAssignmentID) {
        this.taskAssignmentID = taskAssignmentID;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getRequestedByName() {
        return requestedByName;
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }
}

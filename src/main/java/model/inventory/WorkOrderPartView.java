package model.inventory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WorkOrderPartView {
    private int workOrderPartID;
    private int detailID;
    private int partDetailID;
    private String partCode;
    private String partName;
    private String sku;
    private int quantityUsed;
    private BigDecimal unitPrice;
    private String requestStatus;   // PENDING / AVAILABLE / DELIVERED
    private LocalDateTime requestedAt;
    private int currentStock;
    private String requestedByName; // technician name
    private Integer diagnosticPartID; // null nếu là request tay

    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===== Helpers cho JSP =====


    public BigDecimal getTotalPrice() {
        if (unitPrice == null) return BigDecimal.ZERO;
        return unitPrice.multiply(BigDecimal.valueOf(quantityUsed));
    }


    public String getRequestedAtFormatted() {
        return requestedAt != null ? requestedAt.format(DTF) : "-";
    }

    /** Label trạng thái đẹp hơn để hiển thị */
    public String getRequestStatusLabel() {
        if (requestStatus == null) return "-";
        switch (requestStatus) {
            case "PENDING":   return "PENDING";
            case "AVAILABLE": return "AVAILABLE";
            case "DELIVERED": return "DELIVERED";
            default:          return requestStatus;
        }
    }

    // ===== Getters / Setters gốc =====
    public int getWorkOrderPartID() {
        return workOrderPartID;
    }

    public void setWorkOrderPartID(int workOrderPartID) {
        this.workOrderPartID = workOrderPartID;
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

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantityUsed() {
        return quantityUsed;
    }

    public void setQuantityUsed(int quantityUsed) {
        this.quantityUsed = quantityUsed;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public String getRequestedByName() {
        return requestedByName;
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }

    public Integer getDiagnosticPartID() {
        return diagnosticPartID;
    }

    public void setDiagnosticPartID(Integer diagnosticPartID) {
        this.diagnosticPartID = diagnosticPartID;
    }
}

package model.dto;

import java.math.BigDecimal;

public class DiagnosticPartView {

    private int diagnosticPartID;
    private int vehicleDiagnosticID;
    private int partDetailID;
    private int quantityNeeded;
    private BigDecimal unitPrice;
    private String partCondition; // REQUIRED, RECOMMENDED, OPTIONAL
    private String reasonForReplacement;
    private int isApproved;

    private String partCode;
    private String partName;
    private String manufacturer;
    private String description;
    private String sku;
    private int availableStock;

    private BigDecimal totalPrice; // unitPrice * quantityNeeded

    // Constructors
    public DiagnosticPartView() {}

    
    public int getDiagnosticPartID() { return diagnosticPartID; }
    public void setDiagnosticPartID(int diagnosticPartID) {
        this.diagnosticPartID = diagnosticPartID;
    }

    public int getVehicleDiagnosticID() { return vehicleDiagnosticID; }
    public void setVehicleDiagnosticID(int vehicleDiagnosticID) {
        this.vehicleDiagnosticID = vehicleDiagnosticID;
    }

    public int getPartDetailID() { return partDetailID; }
    public void setPartDetailID(int partDetailID) { this.partDetailID = partDetailID; }

    public int getQuantityNeeded() { return quantityNeeded; }
    public void setQuantityNeeded(int quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
        calculateTotalPrice();
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public String getPartCondition() { return partCondition; }
    public void setPartCondition(String partCondition) { this.partCondition = partCondition; }

    public String getReasonForReplacement() { return reasonForReplacement; }
    public void setReasonForReplacement(String reasonForReplacement) {
        this.reasonForReplacement = reasonForReplacement;
    }

    public int getIsApproved() { return isApproved; }
    public void setIsApproved(int isApproved) { this.isApproved = isApproved; }

    public String getPartCode() { return partCode; }
    public void setPartCode(String partCode) { this.partCode = partCode; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }

    public BigDecimal getTotalPrice() { return totalPrice; }

    /**
     * Tính tổng giá = unitPrice * quantityNeeded
     */
    private void calculateTotalPrice() {
        if (unitPrice != null && quantityNeeded > 0) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantityNeeded));
        } else {
            totalPrice = BigDecimal.ZERO;
        }
    }

    /**
     * Kiểm tra xem part có đủ stock không
     */
    public boolean isStockSufficient() {
        return availableStock >= quantityNeeded;
    }

    /**
     * Lấy badge class cho condition (cho UI)
     */
    public String getConditionBadgeClass() {
        return switch (partCondition) {
            case "REQUIRED" -> "badge-danger";
            case "RECOMMENDED" -> "badge-warning";
            case "OPTIONAL" -> "badge-info";
            default -> "badge-secondary";
        };
    }
}

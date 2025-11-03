package model.inventory;

import java.math.BigDecimal;

public class DiagnosticPart {

    private int diagnosticPartID;
    private int vehicleDiagnosticID;
    private int partDetailID;
    private int quantityNeeded;
    private BigDecimal unitPrice;
    private PartCondition partCondition;
    private String reasonForReplacement;
    private boolean isApproved;

    // (JOIN từ Part, PartDetail)
    private String partCode;
    private String partName;
    private String sku;
    private int currentStock;
    private String category;
    private String vehicleInfo;
    private String technicianName;

    public enum PartCondition {
        REQUIRED("Required - Must Replace", "badge-danger"),
        RECOMMENDED("Recommended", "badge-warning"),
        OPTIONAL("Optional Upgrade", "badge-info");

        private final String displayText;
        private final String badgeClass;

        PartCondition(String displayText, String badgeClass) {
            this.displayText = displayText;
            this.badgeClass = badgeClass;
        }

        public String getDisplayText() { return displayText; }
        public String getBadgeClass() { return badgeClass; }

        public static PartCondition fromString(String condition) {
            if (condition == null) return REQUIRED;
            try {
                return valueOf(condition.toUpperCase());
            } catch (IllegalArgumentException e) {
                return REQUIRED;
            }
        }
    }

    // Tính tổng giá
    public BigDecimal getTotalPrice() {
        return unitPrice != null ? unitPrice.multiply(new BigDecimal(quantityNeeded)) : BigDecimal.ZERO;
    }

    // Kiểm tra đủ hàng trong kho chưa
    public boolean isInStock() {
        return currentStock >= quantityNeeded;
    }

    public String getStockStatusClass() {
        return isInStock() ? "text-success" : "text-danger";
    }

    public String getStockStatusText() {
        return isInStock() ? "In Stock" : "Out of Stock";
    }

    // Constructor
    public DiagnosticPart() {}

    // Getters & Setters
    public int getDiagnosticPartID() { return diagnosticPartID; }
    public void setDiagnosticPartID(int diagnosticPartID) {
        this.diagnosticPartID = diagnosticPartID;
    }

    public int getVehicleDiagnosticID() { return vehicleDiagnosticID; }
    public void setVehicleDiagnosticID(int vehicleDiagnosticID) {
        this.vehicleDiagnosticID = vehicleDiagnosticID;
    }

    public int getPartDetailID() { return partDetailID; }
    public void setPartDetailID(int partDetailID) {
        this.partDetailID = partDetailID;
    }

    public int getQuantityNeeded() { return quantityNeeded; }
    public void setQuantityNeeded(int quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public PartCondition getPartCondition() { return partCondition; }
    public void setPartCondition(PartCondition partCondition) {
        this.partCondition = partCondition;
    }
    public void setPartCondition(String partCondition) {
        this.partCondition = PartCondition.fromString(partCondition);
    }

    public String getReasonForReplacement() { return reasonForReplacement; }
    public void setReasonForReplacement(String reasonForReplacement) {
        this.reasonForReplacement = reasonForReplacement;
    }

    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approved) { isApproved = approved; }

    public String getPartCode() { return partCode; }
    public void setPartCode(String partCode) { this.partCode = partCode; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getTechnicianName() { return technicianName; }
    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }
    @Override
    public String toString() {
        return "DiagnosticPart{" +
                "partName='" + partName + '\'' +
                ", qty=" + quantityNeeded +
                ", condition=" + partCondition +
                ", price=" + getTotalPrice() +
                ", vehicle='" + vehicleInfo + '\'' +
                '}';
    }
}

package model.inventory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PartDetail {
    private Integer partDetailId;
    private Integer partId;
    private String sku;
    private Integer quantity;
    private Integer minStock;
    private BigDecimal unitPrice;
    private String location;
    private String manufacturer;
    private String description;

    // Thông tin JOIN (không lưu DB)
    private String partCode;
    private String partName;
    private String category;
    private String unitName;
    private List<CharacteristicValue> characteristics;

    // Constructors
    public PartDetail() {
        this.quantity = 0;
        this.minStock = 10;
    }

    public PartDetail(Integer partDetailId, Integer partId, String sku,
                      Integer quantity, Integer minStock, BigDecimal unitPrice, String location, String manufacturer) {
        this.partDetailId = partDetailId;
        this.partId = partId;
        this.sku = sku;
        this.quantity = quantity;
        this.minStock = minStock;
        this.unitPrice = unitPrice;
        this.location = location;
        this.manufacturer = manufacturer;
    }

    public PartDetail(Integer partDetailId, Integer partId, String sku,
                      Integer quantity, Integer minStock, BigDecimal unitPrice,
                      String location, String manufacturer, String description) {
        this.partDetailId = partDetailId;
        this.partId = partId;
        this.sku = sku;
        this.quantity = quantity;
        this.minStock = minStock;
        this.unitPrice = unitPrice;
        this.location = location;
        this.manufacturer = manufacturer;
        this.description = description;
    }

    // Getters and Setters
    public Integer getPartDetailId() {
        return partDetailId;
    }

    public void setPartDetailId(Integer partDetailId) {
        this.partDetailId = partDetailId;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CharacteristicValue> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(List<CharacteristicValue> characteristics) {
        this.characteristics = characteristics;
    }

    // Business Methods
    public String getStockStatus() {
        if (quantity == 0) return "OUT_OF_STOCK";
        if (quantity <= minStock) return "LOW_STOCK";
        return "IN_STOCK";
    }

    public String getStockStatusLabel() {
        switch (getStockStatus()) {
            case "OUT_OF_STOCK":
                return " Hết hàng";
            case "LOW_STOCK":
                return " Sắp hết";
            default:
                return " Đủ";
        }
    }

    public BigDecimal getTotalValue() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }

    public String getCharacteristicsText() {
        if (characteristics == null || characteristics.isEmpty()) {
            return "";
        }
        return characteristics.stream()
                .map(CharacteristicValue::getFullName)
                .collect(Collectors.joining(" | "));
    }

    public boolean isLowStock() {
        return quantity <= minStock;
    }

    public boolean isOutOfStock() {
        return quantity == 0;
    }

    @Override
    public String toString() {
        return "PartDetail{" +
                "partDetailId=" + partDetailId +
                ", sku='" + sku + '\'' +
                ", partName='" + partName + '\'' +
                ", quantity=" + quantity +
                ", minStock=" + minStock +
                ", status=" + getStockStatusLabel() +
                '}';
    }
}
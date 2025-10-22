package model.inventory;

public class Part {
    private Integer partId;
    private String partCode;
    private String partName;
    private String category;
    private String description;
    private Integer baseUnitId;

    // Thông tin JOIN (không lưu DB)
    private String unitName;

    // Constructors
    public Part() {}

    public Part(Integer partId, String partCode, String partName,
                String category, String description, Integer baseUnitId) {
        this.partId = partId;
        this.partCode = partCode;
        this.partName = partName;
        this.category = category;
        this.description = description;
        this.baseUnitId = baseUnitId;
    }

    // Getters and Setters
    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public String getPartCode() { return partCode; }
    public void setPartCode(String partCode) { this.partCode = partCode; }

    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getBaseUnitId() { return baseUnitId; }
    public void setBaseUnitId(Integer baseUnitId) { this.baseUnitId = baseUnitId; }

    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }

    @Override
    public String toString() {
        return "Part{" +
                "partId=" + partId +
                ", partCode='" + partCode + '\'' +
                ", partName='" + partName + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}

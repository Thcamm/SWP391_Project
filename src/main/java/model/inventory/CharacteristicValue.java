package model.inventory;

public class CharacteristicValue {
    private Integer valueId;
    private Integer typeId;
    private String valueName;

    // Thông tin JOIN
    private String typeName;

    // Constructors
    public CharacteristicValue() {}

    public CharacteristicValue(Integer valueId, Integer typeId, String valueName) {
        this.valueId = valueId;
        this.typeId = typeId;
        this.valueName = valueName;
    }

    // Getters and Setters
    public Integer getValueId() { return valueId; }
    public void setValueId(Integer valueId) { this.valueId = valueId; }

    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }

    public String getValueName() { return valueName; }
    public void setValueName(String valueName) { this.valueName = valueName; }

    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }

    // Helper method
    public String getFullName() {
        return (typeName != null ? typeName + ": " : "") + valueName;
    }

    @Override
    public String toString() {
        return "CharacteristicValue{" +
                "valueId=" + valueId +
                ", valueName='" + valueName + '\'' +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
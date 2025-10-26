package model.inventory;

public class CharacteristicType {
    private Integer typeId;
    private String name;

    // Constructors
    public CharacteristicType() {}

    public CharacteristicType(Integer typeId, String name) {
        this.typeId = typeId;
        this.name = name;
    }

    // Getters and Setters
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "CharacteristicType{" +
                "typeId=" + typeId +
                ", name='" + name + '\'' +
                '}';
    }
}
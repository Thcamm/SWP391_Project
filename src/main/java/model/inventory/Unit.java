package model.inventory;

public class Unit {
    private Integer unitId;
    private String code;
    private String name;

    // Constructors
    public Unit() {}

    public Unit(Integer unitId, String code, String name) {
        this.unitId = unitId;
        this.code = code;
        this.name = name;
    }

    // Getters and Setters
    public Integer getUnitId() { return unitId; }
    public void setUnitId(Integer unitId) { this.unitId = unitId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return "Unit{" +
                "unitId=" + unitId +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

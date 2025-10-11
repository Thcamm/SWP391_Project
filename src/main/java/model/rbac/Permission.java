package model.rbac;

public class Permission {
    public int permId;
    public String code;
    public String name;
    public String category;
    public String description;
//    public boolean active;

    public int getPermId() {
        return permId;
    }

    public void setPermId(int permId) {
        this.permId = permId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public boolean isActive() {
//        return active;
//    }
//
//    public void setActive(boolean active) {
//        this.active = active;
//    }
}

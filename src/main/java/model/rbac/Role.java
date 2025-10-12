package model.rbac;

public class Role {
    public int roleId;
    public String roleName;

    private int userCount;

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDesrciption(String description) {
        this.description = description;
    }

    public Role() {
    }
    public Role(int roleId, String roleName, String description) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
    }


    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getUserCount() {
        return userCount;
    }
    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}

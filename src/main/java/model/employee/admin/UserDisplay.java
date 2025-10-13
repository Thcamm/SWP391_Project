package model.employee.admin;

import model.employee.admin.rbac.Role;
import model.user.User;

public class UserDisplay {
    private int userId;
    private String userName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private int roleId;
    private String roleName;
    private String roleBadgeClass;
    private boolean activeStatus;
    private String createdAt;
    private String updatedAt;

    public UserDisplay() {
    }

    public UserDisplay(User user, Role role) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.roleId = user.getRoleId();
        this.activeStatus = user.isActiveStatus();

        // Set role information
        if (role != null) {
            this.roleName = role.getRoleName();
            this.roleBadgeClass = getRoleBadgeClassByName(role.getRoleName());
        } else {
            this.roleName = "Unknown";
            this.roleBadgeClass = "bg-dark";
        }
    }

    /**
     * Dynamic role badge class assignment based on role name
     */
    private String getRoleBadgeClassByName(String roleName) {
        if (roleName == null) {
            return "bg-dark";
        }

        String name = roleName.toLowerCase();

        // Admin roles - red for high authority
        if (name.contains("admin") || name.contains("administrator") ||
                name.contains("superuser") || name.contains("root")) {
            return "bg-danger";
        }

        // Manager roles - warning orange for management
        if (name.contains("manager") || name.contains("supervisor") ||
                name.contains("lead") || name.contains("head") || name.contains("chief") ||
                name.contains("quản lý") || name.contains("giám sát") || name.contains("trưởng")) {
            return "bg-warning text-dark";
        }

        // Technical roles - info blue for technical staff
        if (name.contains("technician") || name.contains("tech") || name.contains("developer") ||
                name.contains("engineer") || name.contains("specialist") || name.contains("analyst") ||
                name.contains("kỹ thuật") || name.contains("phát triển") || name.contains("chuyên viên")) {
            return "bg-info";
        }

        // Financial roles - success green for accounting
        if (name.contains("accountant") || name.contains("finance") || name.contains("cashier") ||
                name.contains("billing") || name.contains("payment") ||
                name.contains("kế toán") || name.contains("tài chính") || name.contains("thu ngân")) {
            return "bg-success";
        }

        // Storage/warehouse roles - secondary gray
        if (name.contains("keeper") || name.contains("storage") || name.contains("inventory") ||
                name.contains("warehouse") || name.contains("stock") ||
                name.contains("kho") || name.contains("tồn kho")) {
            return "bg-secondary";
        }

        // Customer service - primary blue
        if (name.contains("customer") || name.contains("service") || name.contains("support") ||
                name.contains("reception") || name.contains("sales") ||
                name.contains("khách hàng") || name.contains("hỗ trợ") || name.contains("bán hàng")) {
            return "bg-primary";
        }

        // Default for unknown roles
        return "bg-dark";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getRoleBadgeClass() {
        return roleBadgeClass;
    }

    public void setRoleBadgeClass(String roleBadgeClass) {
        this.roleBadgeClass = roleBadgeClass;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package model.employee.admin;

import model.rbac.Role;
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
            this.roleBadgeClass = getRoleBadgeClass(role.getRoleId());
        } else {
            this.roleName = "Unknown";
            this.roleBadgeClass = "bg-dark";
        }
    }

    private String getRoleBadgeClass(int roleId) {
        switch (roleId) {
            case 1:
                return "bg-danger"; // Admin
            case 2:
                return "bg-warning text-dark"; // Manager
            case 3:
                return "bg-info"; // Employee
            case 4:
                return "bg-primary"; // User
            case 5:
                return "bg-secondary"; // Guest
            default:
                return "bg-dark"; // Unknown
        }
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

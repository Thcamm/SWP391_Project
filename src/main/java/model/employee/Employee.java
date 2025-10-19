package model.employee;

import model.user.User;

import java.time.LocalDateTime;

public class Employee extends User{
    private int employeeId;
    private String position;
    private String department;
    private String employeeCode;
    private int managerBy;
    private int createBy;
    private LocalDateTime createAt;

    public Employee() {
        super();
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public int getManagerBy() {
        return managerBy;
    }

    public void setManagerBy(int managerBy) {
        this.managerBy = managerBy;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public Employee(int userId, int roleId, String fullName, String userName, String email, String phoneNumber, String passwordHash, boolean activeStatus, int employeeId, String position, String department) {
        super();
        this.employeeId = employeeId;
        this.position = position;
        this.department = department;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}

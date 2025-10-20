package model.employee;

import java.time.LocalDateTime;
import model.user.User;

public class Employee extends User {

    private int employeeId;
    private String employeeCode;
    private double salary;
    private int managedBy;
    private int createBy;
    private LocalDateTime createAt;

    // Default constructor
    public Employee() {
        super();
    }

    // Parameterized constructor
    public Employee(int employeeId, String employeeCode, double salary, int managedBy, int createBy,
            LocalDateTime createAt) {
        super(); // Gọi constructor của lớp cha User
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.salary = salary;
        this.managedBy = managedBy;
        this.createBy = createBy;
        this.createAt = createAt;
    }

    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(int managedBy) {
        this.managedBy = managedBy;
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
}
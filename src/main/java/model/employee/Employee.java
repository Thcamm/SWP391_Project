package model.employee;

import java.math.BigDecimal;

import model.user.User;

public class Employee extends User {
    private int employeeId;
    private String employeeCode;
    private BigDecimal salary;
    private Integer managedBy;
    private Integer createBy;

    public Employee(int employeeId, String employeeCode, BigDecimal salary, Integer managedBy, Integer createBy) {
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.salary = salary;
        this.managedBy = managedBy;
        this.createBy = createBy;
    }

    public Employee() {
        // Default constructor
    }

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

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Integer getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(Integer managedBy) {
        this.managedBy = managedBy;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

}

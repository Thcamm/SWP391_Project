package model.employee.admin;

import model.employee.Employee;

public class Admin extends Employee {
    public Admin() {
        super();
    }

    public Admin(Employee employee) {
        super(employee.getEmployeeId(), employee.getEmployeeCode(), employee.getSalary(),
              employee.getManagedBy(), employee.getCreateBy());
        // Copy User fields
        this.setUserId(employee.getUserId());
        this.setRoleId(employee.getRoleId());
        this.setFullName(employee.getFullName());
        this.setUserName(employee.getUserName());
        this.setEmail(employee.getEmail());
        this.setPhoneNumber(employee.getPhoneNumber());
        this.setPasswordHash(employee.getPasswordHash());
        this.setActiveStatus(employee.isActiveStatus());
    }

    // Check admin
    public boolean isAdmin() {
        return getRoleId() == 1;
    }

}

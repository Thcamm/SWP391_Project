package model.employee.techmanager;

import model.employee.Employee;

public class TechManager extends Employee {
    // TechManager inherits all from Employee (User fields + employee fields)

    public TechManager() {
        super();
    }

    public TechManager(int employeeId, String employeeCode, java.math.BigDecimal salary,
            Integer managedBy, Integer createBy) {
        super(employeeId, employeeCode, salary, managedBy, createBy);
    }

    public TechManager(Employee employee) {
        super(employee.getEmployeeId(), employee.getEmployeeCode(), employee.getSalary(),
                employee.getManagedBy(), employee.getCreateBy());
        this.setUserId(employee.getUserId());
        this.setRoleId(employee.getRoleId());
        this.setFullName(employee.getFullName());
        this.setUserName(employee.getUserName());
        this.setEmail(employee.getEmail());
        this.setPhoneNumber(employee.getPhoneNumber());
        this.setPasswordHash(employee.getPasswordHash());
        this.setActiveStatus(employee.isActiveStatus());
    }

    public boolean isTechManager() {
        // Assuming RoleID 2 is Tech Manager (check RoleInfo table)
        return getRoleId() == 2;
    }

}

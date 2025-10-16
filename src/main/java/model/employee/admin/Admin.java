package model.employee.admin;

import model.employee.Employee;

public class Admin extends Employee {
    public Admin() {
        super();
    }

    public Admin(int userId, int roleId, String fullName, String userName, String email, String phoneNumber,
                 String passwordHash, boolean activeStatus, int employeeId, String position, String department) {
        super(userId, roleId, fullName, userName, email, phoneNumber, passwordHash, activeStatus, employeeId,
                position, department);
    }

    public Admin(Employee employee) {
        super(employee.getUserId(), employee.getRoleId(), employee.getFullName(), employee.getUserName(),
                employee.getEmail(), employee.getPhoneNumber(), employee.getPasswordHash(), employee.isActiveStatus(),
                employee.getEmployeeId(), employee.getPosition(), employee.getDepartment());
    }

    //Check admin
    public boolean isAdmin() {
        return getRoleId() == 1;
    }




}

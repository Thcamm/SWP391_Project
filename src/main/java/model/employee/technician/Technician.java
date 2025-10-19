package model.employee.technician;

import model.employee.Employee;

public class Technician extends Employee {
    public Technician() {
        super();
    }

    public Technician(int userId, int roleId, String fullName, String userName, String email, String phoneNumber,
                      String passwordHash, boolean activeStatus, int employeeId, String position, String department) {
        super(userId, roleId, fullName, userName, email, phoneNumber, passwordHash, activeStatus, employeeId,
                position, department);
    }

    public Technician(Employee employee) {
        super(employee.getUserId(), employee.getRoleId(), employee.getFullName(), employee.getUserName(),
                employee.getEmail(), employee.getPhoneNumber(), employee.getPasswordHash(), employee.isActiveStatus(),
                employee.getEmployeeId(), employee.getPosition(), employee.getDepartment());
    }

    //Check technician
    public boolean isTechnician() {
        return getRoleId() == 2;
    }
}

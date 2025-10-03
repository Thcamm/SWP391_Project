package model.employee;

import model.user.User;
public class Employee extends User{
    private int employeeId;
    private String position;
    private String department;

    public Employee() {
        super();
    }

    public Employee(int userId, int roleId, String fullName, String userName, String email, String phoneNumber, String passwordHash, boolean activeStatus, int employeeId, String position, String department) {
        super(userId, roleId, fullName, userName, email, phoneNumber, passwordHash, activeStatus);
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

package model.employee.techmanager;
import model.employee.Employee;

public class TechManager extends Employee {
    public TechManager() {
    }

    public TechManager(int userId, int roleId, String fullName, String userName, String email, String phoneNumber, String passwordHash, boolean activeStatus, int employeeId, String position, String department) {
        super(userId, roleId, fullName, userName, email, phoneNumber, passwordHash, activeStatus, employeeId, position, department);
    }




}

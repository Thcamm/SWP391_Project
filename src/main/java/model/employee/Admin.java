package model.employee;

public class Admin extends Employee{
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

    public boolean canManageUser(int targetRoleId) {
        if (this.getRoleId() == 1) {
            return true;
        } else if (this.getRoleId() == 2) {
            return targetRoleId > 2;
        }
        return false;
    }

    public String getAdminDisplayName() {
        return this.getFullName() + " (" + this.getPosition() + ")";
    }



}

package model.employee.admin;

import model.employee.Employee;
import java.time.LocalDateTime; // Đảm bảo đã import

public class Admin extends Employee {

    public Admin() {
        super();
    }

    public Admin(Employee employee) {
        // Gọi đúng constructor của lớp cha Employee với đủ 6 tham số
        super(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getSalary(),
                employee.getManagedBy(),
                employee.getCreateBy(),
                employee.getCreateAt() // <-- Thêm tham số còn thiếu
        );

        // Sao chép các trường của lớp User (đã được kế thừa)
        this.setUserId(employee.getUserId());
        this.setRoleId(employee.getRoleId());
        this.setFullName(employee.getFullName());
        this.setUserName(employee.getUserName());
        this.setEmail(employee.getEmail());
        this.setPhoneNumber(employee.getPhoneNumber());
        this.setPasswordHash(employee.getPasswordHash());
        this.setActiveStatus(employee.isActiveStatus());
    }

    // Kiểm tra admin
    public boolean isAdmin() {
        // Giả sử RoleID 1 là Admin
        return getRoleId() == 1;
    }
}
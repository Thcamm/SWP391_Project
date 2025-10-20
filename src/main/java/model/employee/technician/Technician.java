package model.employee.technician;

import model.employee.Employee;
import java.time.LocalDateTime; // Đảm bảo đã import

public class Technician extends Employee {

    public Technician() {
        super();
    }

    public Technician(Employee employee) {
        // Gọi đúng constructor của lớp cha Employee với đủ 6 tham số
        super(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getSalary(),
                employee.getManagedBy(),
                employee.getCreateBy(),
                employee.getCreateAt());

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

    // Kiểm tra vai trò Technician
    public boolean isTechnician() {
        // Giả sử RoleID 3 là Technician (để tránh trùng với Tech Manager)
        return getRoleId() == 3;
    }
}
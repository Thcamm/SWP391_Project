package model.employee.techmanager;

import model.employee.Employee;
import java.time.LocalDateTime; // Đảm bảo đã import

public class TechManager extends Employee {
    // TechManager kế thừa tất cả thuộc tính từ Employee

    public TechManager() {
        super();
    }

    // Constructor đã được sửa lại
    public TechManager(Employee employee) {
        // Gọi đúng constructor của lớp cha với đủ 6 tham số
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

    public boolean isTechManager() {
        // Giả sử RoleID 2 là Tech Manager
        return getRoleId() == 2;
    }
}
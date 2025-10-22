package model.employee.techmanager;

import model.employee.Employee;
import java.time.LocalDateTime;

public class TechManager extends Employee {


    public TechManager() {
        super();
    }

    public TechManager(Employee employee) {
        super(
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getSalary(),
                employee.getManagedBy(),
                employee.getCreateBy(),
                employee.getCreateAt()
        );

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
        return getRoleId() == 2;
    }
}
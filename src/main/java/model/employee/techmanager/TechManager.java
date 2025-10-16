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

    // Business methods for TechManager
    public boolean isTechManager() {
        // Assuming RoleID 3 is Tech Manager (check RoleInfo table)
        return getRoleId() == 3;
    }

    // Can add methods like:
    // - createWorkOrderFromRequest(ServiceRequest request)
    // - assignTasksToTechnicians(WorkOrder workOrder, List<Employee> technicians)
    // - approveWorkOrderDetails(List<WorkOrderDetail> details)
}

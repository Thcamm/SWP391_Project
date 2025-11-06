package model.employee.techmanager;

/**
 * DTO for available technicians
 */
public class TechnicianDTO {
    private int employeeId;
    private String fullName;
    private String phoneNumber;
    private int activeTasks;

    // Constructors
    public TechnicianDTO() {
    }

    public TechnicianDTO(int employeeId, String fullName, String phoneNumber, int activeTasks) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.activeTasks = activeTasks;
    }

    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getActiveTasks() {
        return activeTasks;
    }

    public void setActiveTasks(int activeTasks) {
        this.activeTasks = activeTasks;
    }

    @Override
    public String toString() {
        return "TechnicianDTO{" +
                "employeeId=" + employeeId +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", activeTasks=" + activeTasks +
                '}';
    }
}

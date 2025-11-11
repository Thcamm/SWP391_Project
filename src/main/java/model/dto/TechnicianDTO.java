package model.dto;

/**
 * DTO for displaying Technician information with User details.
 * Used in:
 * - UC-TM-02: Assign Diagnosis (only Active)
 * - UC-TM-03: Assign Repair (only Active)
 * - UC-TM-11: My Team page (both Active & Inactive)
 *
 * @author SWP391 Team
 * @version 1.0
 */
public class TechnicianDTO {

    private int employeeID;
    private String employeeCode;
    private int userID;
    private String fullName;
    private String phoneNumber;
    private String email;
    private int activeStatus; // 1 = Active, 0 = Inactive
    private Integer managedBy; // For debugging - which Tech Manager manages this technician

    // Constructors
    public TechnicianDTO() {
    }

    public TechnicianDTO(int employeeID, String employeeCode, int userID, String fullName,
            String phoneNumber, String email, int activeStatus) {
        this.employeeID = employeeID;
        this.employeeCode = employeeCode;
        this.userID = userID;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.activeStatus = activeStatus;
    }

    // Getters and Setters
    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(int activeStatus) {
        this.activeStatus = activeStatus;
    }

    public Integer getManagedBy() {
        return managedBy;
    }

    public void setManagedBy(Integer managedBy) {
        this.managedBy = managedBy;
    }

    /**
     * Check if technician is currently active.
     * 
     * @return true if ActiveStatus = 1, false otherwise
     */
    public boolean isActive() {
        return activeStatus == 1;
    }

    /**
     * Get badge CSS class for ActiveStatus display.
     * 
     * @return Bootstrap badge class
     */
    public String getStatusBadgeClass() {
        return activeStatus == 1 ? "bg-success" : "bg-secondary";
    }

    /**
     * Get status label for display.
     * 
     * @return "Active" or "Inactive"
     */
    public String getStatusLabel() {
        return activeStatus == 1 ? "Active" : "Inactive";
    }

    @Override
    public String toString() {
        return "TechnicianDTO{" +
                "employeeID=" + employeeID +
                ", employeeCode='" + employeeCode + '\'' +
                ", fullName='" + fullName + '\'' +
                ", activeStatus=" + activeStatus +
                '}';
    }
}

package model.dto;

import java.sql.Timestamp;

/**
 * DTO for displaying TechnicianActivityLog entries on Tech Manager Dashboard.
 * Represents recent activities performed by technicians.
 *
 * @author SWP391 Team
 * @version 1.0
 */
public class ActivityLogDTO {
    
    private int activityID;
    private String technicianName;
    private String activityType;
    private String description;
    private Timestamp activityTime;
    private Integer taskAssignmentID;
    
    // For linking to specific task details
    private String taskDescription;
    private String vehicleInfo;

    // Constructors
    public ActivityLogDTO() {
    }

    public ActivityLogDTO(int activityID, String technicianName, String activityType, 
                         String description, Timestamp activityTime) {
        this.activityID = activityID;
        this.technicianName = technicianName;
        this.activityType = activityType;
        this.description = description;
        this.activityTime = activityTime;
    }

    // Getters and Setters
    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public void setTechnicianName(String technicianName) {
        this.technicianName = technicianName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Timestamp activityTime) {
        this.activityTime = activityTime;
    }

    public Integer getTaskAssignmentID() {
        return taskAssignmentID;
    }

    public void setTaskAssignmentID(Integer taskAssignmentID) {
        this.taskAssignmentID = taskAssignmentID;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    /**
     * Get a user-friendly activity type display label.
     */
    public String getActivityTypeLabel() {
        switch (activityType) {
            case "TASK_ACCEPTED":
                return "✓ Accepted Task";
            case "TASK_REJECTED":
                return "✗ Rejected Task";
            case "TASK_STARTED":
                return "▶ Started Task";
            case "TASK_COMPLETED":
                return "✔ Completed Task";
            case "PARTS_REQUESTED":
                return "🔧 Requested Parts";
            case "DIAGNOSTIC_CREATED":
                return "🔍 Created Diagnostic";
            case "PROGRESS_UPDATED":
                return "📊 Updated Progress";
            case "NOTE_ADDED":
                return "📝 Added Note";
            case "TASK_REJECT_REQUESTED":
                return "⚠ Requested Rejection";
            case "TASK_AUTO_CANCEL":
                return "⏰ Auto-Cancelled (SLA)";
            default:
                return activityType;
        }
    }

    /**
     * Get Bootstrap color class based on activity type.
     */
    public String getActivityBadgeClass() {
        switch (activityType) {
            case "TASK_COMPLETED":
                return "bg-success";
            case "TASK_STARTED":
            case "TASK_ACCEPTED":
                return "bg-primary";
            case "TASK_REJECTED":
            case "TASK_AUTO_CANCEL":
                return "bg-danger";
            case "TASK_REJECT_REQUESTED":
                return "bg-warning text-dark";
            case "DIAGNOSTIC_CREATED":
            case "PARTS_REQUESTED":
                return "bg-info";
            case "PROGRESS_UPDATED":
            case "NOTE_ADDED":
                return "bg-secondary";
            default:
                return "bg-secondary";
        }
    }
}

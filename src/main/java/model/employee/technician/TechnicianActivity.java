package model.employee.technician;

import java.time.LocalDateTime;

public class TechnicianActivity {
    private int activityID;
    private int technicianID;
    private ActivityType activityType;
    private Integer taskAssignmentID;
    private String description;
    private LocalDateTime activityTime;

    private String vehicleInfo;
    private String taskInfo;



    public enum ActivityType {
        TASK_ACCEPTED("accepted task"),
        TASK_REJECTED("rejected task"),
        TASK_STARTED("started working on"),
        TASK_COMPLETED("completed task"),
        PARTS_REQUESTED("requested parts for"),
        DIAGNOSTIC_CREATED("created diagnostic for"),
        PROGRESS_UPDATED("updated progress on"),
        NOTE_ADDED("added note to");

        private final String displayText;

        ActivityType(String displayText) {
            this.displayText = displayText;
        }

        public String getDisplayText() { return displayText; }

        public static ActivityType fromString(String type) {
            if (type == null) return TASK_ACCEPTED;
            try {
                return valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return TASK_ACCEPTED;
            }
        }
    }



    public TechnicianActivity() {}



    public int getActivityID() { return activityID; }
    public void setActivityID(int activityID) { this.activityID = activityID; }

    public int getTechnicianID() { return technicianID; }
    public void setTechnicianID(int technicianID) { this.technicianID = technicianID; }

    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    public void setActivityType(String activityType) {
        this.activityType = ActivityType.fromString(activityType);
    }

    public Integer getTaskAssignmentID() { return taskAssignmentID; }
    public void setTaskAssignmentID(Integer taskAssignmentID) {
        this.taskAssignmentID = taskAssignmentID;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getActivityTime() { return activityTime; }
    public void setActivityTime(LocalDateTime activityTime) { this.activityTime = activityTime; }

    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }

    public String getTaskInfo() { return taskInfo; }
    public void setTaskInfo(String taskInfo) { this.taskInfo = taskInfo; }

    @Override
    public String toString() {
        return "TechnicianActivity{" +
                "activityType=" + activityType +
                ", description='" + description + '\'' +
                ", activityTime=" + activityTime +
                '}';
    }
}
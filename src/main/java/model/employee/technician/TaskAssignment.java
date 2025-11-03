package model.employee.technician;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskAssignment {
    private int assignmentID;
    private int detailID;
    private int assignToTechID;
    private LocalDateTime assignedDate;
    private LocalDateTime startAt;
    private LocalDateTime completeAt;
    private String taskDescription;
    private TaskType taskType;
    private Priority priority;
    private TaskStatus status;
    private int progressPercentage;
    private String notes;


    private String vehicleInfo;
    private String serviceInfo;
    private String customerName;
    private double estimateHours;
    private String assignedDateFormatted;

    private static final DateTimeFormatter D_HM = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final DateTimeFormatter D_M_Y_HM = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    public String getAssignedDateFormatted() {
        if (assignedDateFormatted != null && !assignedDateFormatted.isEmpty()) return assignedDateFormatted;
        return assignedDate != null ? assignedDate.format(D_HM) : "-";
    }

    public void setAssignedDateFormatted(String assignedDateFormatted) {
        this.assignedDateFormatted = assignedDateFormatted;
    }

    public String getStartAtFormatted() {
        return startAt != null ? startAt.format(D_HM) : "-";
    }

    public String getCompleteAtFormatted() {
        return completeAt != null ? completeAt.format(D_M_Y_HM) : "-";
    }




    public enum TaskType {
        DIAGNOSIS, REPAIR, OTHER;

        public static TaskType fromString(String type) {
            if (type == null) return OTHER;
            try {
                return valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return OTHER;
            }
        }
    }

    public enum Priority {
        LOW("priority-low"),
        MEDIUM("priority-medium"),
        HIGH("priority-high"),
        URGENT("priority-urgent");

        private final String cssClass;

        Priority(String cssClass) {
            this.cssClass = cssClass;
        }

        public String getCssClass() { return cssClass; }

        public static Priority fromString(String priority) {
            if (priority == null) return MEDIUM;
            try {
                return valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                return MEDIUM;
            }
        }
    }

    public enum TaskStatus {
        ASSIGNED, IN_PROGRESS, COMPLETE;

        public static TaskStatus fromString(String status) {
            if (status == null) return ASSIGNED;
            try {
                return valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ASSIGNED;
            }
        }
    }



    public TaskAssignment() {}


    public int getAssignmentID() { return assignmentID; }
    public void setAssignmentID(int assignmentID) { this.assignmentID = assignmentID; }

    public int getDetailID() { return detailID; }
    public void setDetailID(int detailID) { this.detailID = detailID; }

    public int getAssignToTechID() { return assignToTechID; }
    public void setAssignToTechID(int assignToTechID) { this.assignToTechID = assignToTechID; }

    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime assignedDate) { this.assignedDate = assignedDate; }

    public LocalDateTime getStartAt() { return startAt; }
    public void setStartAt(LocalDateTime startAt) { this.startAt = startAt; }

    public LocalDateTime getCompleteAt() { return completeAt; }
    public void setCompleteAt(LocalDateTime completeAt) { this.completeAt = completeAt; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public void setTaskType(String taskType) { this.taskType = TaskType.fromString(taskType); }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setPriority(String priority) { this.priority = Priority.fromString(priority); }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setStatus(String status) { this.status = TaskStatus.fromString(status); }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = Math.min(100, Math.max(0, progressPercentage));
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getVehicleInfo() { return vehicleInfo; }
    public void setVehicleInfo(String vehicleInfo) { this.vehicleInfo = vehicleInfo; }

    public String getServiceInfo() { return serviceInfo; }
    public void setServiceInfo(String serviceInfo) { this.serviceInfo = serviceInfo; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public double getEstimateHours() { return estimateHours; }
    public void setEstimateHours(double estimateHours) { this.estimateHours = estimateHours; }

    @Override
    public String toString() {
        return "TaskAssignment{" +
                "assignmentID=" + assignmentID +
                ", taskDescription='" + taskDescription + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", progress=" + progressPercentage + "%" +
                '}';
    }
}
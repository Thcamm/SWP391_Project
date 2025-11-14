package model.employee.technician;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class TaskAssignment {
    private int assignmentID;
    private int detailID;
    private int assignToTechID;

    private LocalDateTime assignedDate;
    private LocalDateTime startAt;       // khi tech thực sự bấm bắt đầu
    private LocalDateTime completeAt;

    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;


    private LocalDateTime declinedAt;
    private String        declineReason;

    private String taskDescription;
    private TaskType taskType;
    private Priority priority;
    private TaskStatus status;
    private int progressPercentage;
    private String notes;

    // Info bổ trợ cho UI
    private String vehicleInfo;
    private String serviceInfo;
    private String customerName;
    private double estimateHours;
    private String assignedDateFormatted;
    private String customerEmail;
    private String taskDesDetail;
    private WorkSource workSource;

    public WorkSource getWorkSource() {
        return workSource;
    }
    public void setWorkSource(WorkSource workSource) {
        this.workSource = workSource;
    }
    public void setWorkSource(String src) {
        this.workSource = WorkSource.fromString(src);
    }
    public String getTaskDesDetail() {
        return taskDesDetail;
    }

    public void setTaskDesDetail(String taskDesDetail) {
        this.taskDesDetail = taskDesDetail;
    }

    private static final DateTimeFormatter D_HM = DateTimeFormatter.ofPattern("dd/MM HH:mm");
    private static final DateTimeFormatter D_M_Y_HM = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public enum WorkSource {
        REQUEST, DIAGNOSTIC;

        public static WorkSource fromString(String s) {
            if (s == null) return REQUEST;
            try { return valueOf(s.trim().toUpperCase()); }
            catch (IllegalArgumentException e) { return REQUEST; }
        }
    }

    public String getPlannedStartFormatted() {
        return plannedStart != null ? plannedStart.format(D_M_Y_HM) : "-";
    }
    public String getPlannedEndFormatted() {
        return plannedEnd != null ? plannedEnd.format(D_HM) : "-";
    }





    public boolean isOverdue() {
        return status == TaskStatus.ASSIGNED
                && startAt == null
                && plannedStart != null
                && LocalDateTime.now().isAfter(plannedStart);
    }

    public boolean isDeclined() {
        return declinedAt != null && status == TaskStatus.CANCELLED;
    }

    public enum TaskType {
        DIAGNOSIS, REPAIR, OTHER;
        public static TaskType fromString(String type) {
            if (type == null) return OTHER;
            try { return valueOf(type.trim().toUpperCase()); }
            catch (IllegalArgumentException e) { return OTHER; }
        }
    }

    public enum Priority {
        LOW("priority-low"),
        MEDIUM("priority-medium"),
        HIGH("priority-high"),
        URGENT("priority-urgent");

        private final String cssClass;
        Priority(String cssClass) { this.cssClass = cssClass; }
        public String getCssClass() { return cssClass; }

        public static Priority fromString(String priority) {
            if (priority == null) return MEDIUM;
            try { return valueOf(priority.trim().toUpperCase()); }
            catch (IllegalArgumentException e) { return MEDIUM; }
        }
    }

    // NEW: mở rộng theo DB: ASSIGNED, IN_PROGRESS, COMPLETE, DECLINED, CANCELLED
    public enum TaskStatus {
        ASSIGNED, IN_PROGRESS, COMPLETE, DECLINED, CANCELLED;

        public static TaskStatus fromString(String status) {
            if (status == null) return ASSIGNED;
            try { return valueOf(status.trim().toUpperCase()); }
            catch (IllegalArgumentException e) { return ASSIGNED; }
        }

        public boolean isTerminal() {
            return this == COMPLETE || this == DECLINED || this == CANCELLED;
        }
    }

    public TaskAssignment() {}

    // ======= Format helpers =======
    public String getAssignedDateFormatted() {
        if (assignedDateFormatted != null && !assignedDateFormatted.isEmpty()) return assignedDateFormatted;
        return assignedDate != null ? assignedDate.format(D_HM) : "-";
    }
    public void setAssignedDateFormatted(String assignedDateFormatted) { this.assignedDateFormatted = assignedDateFormatted; }

    public String getStartAtFormatted() { return startAt != null ? startAt.format(D_HM) : "-"; }
    public String getCompleteAtFormatted() { return completeAt != null ? completeAt.format(D_M_Y_HM) : "-"; }

    // NEW
    public String getPlannedWindowFormatted() {
        if (plannedStart == null || plannedEnd == null) return "-";
        return plannedStart.format(D_M_Y_HM) + " → " + plannedEnd.format(D_HM);
    }
    public String getDeclinedAtFormatted() { return declinedAt != null ? declinedAt.format(D_M_Y_HM) : "-"; }

    // ======= Business helpers phía client (tham khảo) =======

    /** Có nằm trong “cửa sổ accept” (<= 10 phút sau plannedStart) không? */
    public boolean isWithinAcceptWindow(int minutes) {
        if (plannedStart == null) return false;
        LocalDateTime deadline = plannedStart.plusMinutes(minutes <= 0 ? 10 : minutes);
        LocalDateTime now = LocalDateTime.now();
        return (now.isEqual(plannedStart) || now.isAfter(plannedStart)) && (now.isBefore(deadline) || now.isEqual(deadline));
    }

    /** Còn “chưa phản hồi” (ASSIGNED, chưa StartAt) và đã quá hạn accept? */
    public boolean isAutoCancelEligible(int minutes) {
        if (status != TaskStatus.ASSIGNED || startAt != null || plannedStart == null) return false;
        return LocalDateTime.now().isAfter(plannedStart.plusMinutes(minutes <= 0 ? 10 : minutes));
    }

    /** Check overlap 2 khoảng planned (client-side). DB đã có trigger chống trùng. */
    public boolean overlapsPlanned(TaskAssignment other) {
        if (other == null || plannedStart == null || plannedEnd == null || other.plannedStart == null || other.plannedEnd == null)
            return false;
        return plannedStart.isBefore(other.plannedEnd) && plannedEnd.isAfter(other.plannedStart);
    }

    /** Thời lượng dự kiến (giờ) cho UI */
    public double getPlannedHours() {
        if (plannedStart == null || plannedEnd == null) return 0d;
        long minutes = Duration.between(plannedStart, plannedEnd).toMinutes();
        return minutes / 60.0;
    }

    // ======= Getters/Setters =======
    public int getAssignmentID() { return assignmentID; }
    public void setAssignmentID(int assignmentID) { this.assignmentID = assignmentID; }

    public int getAssignToTechID() {
        return assignToTechID;
    }

    public void setAssignToTechID(int assignToTechID) {
        this.assignToTechID = assignToTechID;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }



    public LocalDateTime getCompleteAt() {
        return completeAt;
    }

    public void setCompleteAt(LocalDateTime completeAt) {
        this.completeAt = completeAt;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = TaskType.fromString(taskType);
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setPriority(String priority) {
        this.priority = Priority.fromString(priority);
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = TaskStatus.fromString(status);
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = Math.min(100, Math.max(0, progressPercentage));
    }

    public int getDetailID() {
        return detailID;
    }

    public void setDetailID(int detailID) {
        this.detailID = detailID;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getServiceInfo() {
        return serviceInfo;
    }

    public void setServiceInfo(String serviceInfo) {
        this.serviceInfo = serviceInfo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getEstimateHours() {
        return estimateHours;
    }

    public void setEstimateHours(double estimateHours) {
        this.estimateHours = estimateHours;
    }

    // NEW: Scheduling getters/setters
    public LocalDateTime getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(LocalDateTime plannedStart) {
        this.plannedStart = plannedStart;
    }

    public LocalDateTime getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(LocalDateTime plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    // NEW: Decline handling getters/setters
    public LocalDateTime getDeclinedAt() {
        return declinedAt;
    }

    public void setDeclinedAt(LocalDateTime declinedAt) {
        this.declinedAt = declinedAt;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    @Override
    public String toString() {
        return "TaskAssignment{" +
                "assignmentID=" + assignmentID +
                ", taskDescription='" + taskDescription + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", progress=" + progressPercentage + "%" +
                ", planned=" + (plannedStart != null ? plannedStart.format(D_M_Y_HM) : "-") +
                "→" + (plannedEnd != null ? plannedEnd.format(D_HM) : "-") +
                '}';
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    // tiện cho sort theo plannedStart
    public int compareByPlannedStart(TaskAssignment other) {
        if (other == null) return -1;
        if (plannedStart == null && other.plannedStart == null) return 0;
        if (plannedStart == null) return 1;
        if (other.plannedStart == null) return -1;
        return plannedStart.compareTo(other.plannedStart);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskAssignment)) return false;
        TaskAssignment that = (TaskAssignment) o;
        return assignmentID == that.assignmentID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentID);
    }
}
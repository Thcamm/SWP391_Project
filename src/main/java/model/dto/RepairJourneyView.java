package model.dto;

import model.appointment.Appointment;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;
import model.invoice.Invoice;
import model.feedback.Feedback;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO đại diện cho hành trình sửa chữa với cấu trúc timeline chi tiết
 */
public class RepairJourneyView {

    // Dữ liệu gốc từ DB
    private Appointment appointment;
    private ServiceRequest serviceRequest;
    private WorkOrder workOrder;
    private Invoice invoice;
    private Feedback feedback;

    // Timeline stages (được xử lý bởi Service)
    private List<TimelineStage> stages = new ArrayList<>();

    // Feedback action và countdown
    private String feedbackAction; // "ALLOW_FEEDBACK", "HAS_FEEDBACK", "EXPIRED", null
    private Integer feedbackDaysLeft; // Số ngày còn lại để feedback

    // Inner class để represent mỗi stage trong timeline
    public static class TimelineStage {
        private String stageType; // "APPOINTMENT", "SERVICE_REQUEST", "WORK_ORDER", "INVOICE", "FEEDBACK"
        private String stageTitle; // "Đặt Lịch Hẹn", "Yêu Cầu Dịch Vụ", ...
        private String icon; // Bootstrap icon class
        private List<TimelineStep> steps = new ArrayList<>();
        private String overallStatus; // "completed", "active", "rejected"

        public TimelineStage(String stageType, String stageTitle, String icon) {
            this.stageType = stageType;
            this.stageTitle = stageTitle;
            this.icon = icon;
        }

        // Getters and Setters
        public String getStageType() { return stageType; }
        public void setStageType(String stageType) { this.stageType = stageType; }

        public String getStageTitle() { return stageTitle; }
        public void setStageTitle(String stageTitle) { this.stageTitle = stageTitle; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public List<TimelineStep> getSteps() { return steps; }
        public void setSteps(List<TimelineStep> steps) { this.steps = steps; }

        public String getOverallStatus() { return overallStatus; }
        public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }

        public void addStep(TimelineStep step) {
            this.steps.add(step);
        }
    }

    // Inner class để represent mỗi bước nhỏ trong stage
    public static class TimelineStep {
        private String statusText; // "Đã tạo lịch hẹn", "Đã chấp nhận", ...
        private String timestamp; // Formatted timestamp
        private String statusIcon; // "check-circle", "clock", "x-circle", "gear"
        private String statusColor; // "success", "warning", "danger", "primary"
        private boolean isCompleted;

        public TimelineStep(String statusText, String timestamp, String statusIcon,
                            String statusColor, boolean isCompleted) {
            this.statusText = statusText;
            this.timestamp = timestamp;
            this.statusIcon = statusIcon;
            this.statusColor = statusColor;
            this.isCompleted = isCompleted;
        }

        // Getters and Setters
        public String getStatusText() { return statusText; }
        public void setStatusText(String statusText) { this.statusText = statusText; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getStatusIcon() { return statusIcon; }
        public void setStatusIcon(String statusIcon) { this.statusIcon = statusIcon; }

        public String getStatusColor() { return statusColor; }
        public void setStatusColor(String statusColor) { this.statusColor = statusColor; }

        public boolean isCompleted() { return isCompleted; }
        public void setCompleted(boolean completed) { isCompleted = completed; }
    }

    // Getters and Setters cho các trường chính
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }

    public ServiceRequest getServiceRequest() { return serviceRequest; }
    public void setServiceRequest(ServiceRequest serviceRequest) { this.serviceRequest = serviceRequest; }

    public WorkOrder getWorkOrder() { return workOrder; }
    public void setWorkOrder(WorkOrder workOrder) { this.workOrder = workOrder; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public List<TimelineStage> getStages() { return stages; }
    public void setStages(List<TimelineStage> stages) { this.stages = stages; }

    public String getFeedbackAction() { return feedbackAction; }
    public void setFeedbackAction(String feedbackAction) { this.feedbackAction = feedbackAction; }

    public Integer getFeedbackDaysLeft() { return feedbackDaysLeft; }
    public void setFeedbackDaysLeft(Integer feedbackDaysLeft) { this.feedbackDaysLeft = feedbackDaysLeft; }
}
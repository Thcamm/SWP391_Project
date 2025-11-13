package model.dto;

import model.appointment.Appointment;
import model.customer.CustomerDiagnosticsView;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;
import model.invoice.Invoice;
import model.feedback.Feedback;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO đại diện cho hành trình sửa chữa với cấu trúc timeline chi tiết
 */
public class RepairJourneyView {

    private int requestID;
    private Timestamp requestDate;
    private String requestStatus; // PENDING, APPROVE, REJECTED
    private String note;

    private int customerID;
    private String customerName;

    private int vehicleID;
    private String licensePlate;
    private String vehicleBrand;
    private String vehicleModel;
    private int vehicleYear;


    private Integer workOrderID;
    private String workOrderStatus; // PENDING, IN_PROCESS, COMPLETE
    private BigDecimal estimateAmount;
    private Timestamp workOrderCreatedAt;


    private List<WorkOrderDetailView> workOrderDetails = new ArrayList<>();

    private BigDecimal totalServiceCost = BigDecimal.ZERO;
    private BigDecimal totalPartsCost = BigDecimal.ZERO;
    private BigDecimal grandTotal = BigDecimal.ZERO;


    // Dữ liệu gốc từ DB
    private Appointment appointment;
    private ServiceRequest serviceRequest;
    private WorkOrder workOrder;
    private Invoice invoice;
    private Feedback feedback;

    private CustomerDiagnosticsView diagnosticsView;

    public CustomerDiagnosticsView getDiagnosticsView() {
        return diagnosticsView;
    }

    public void setDiagnosticsView(CustomerDiagnosticsView diagnosticsView) {
        this.diagnosticsView = diagnosticsView;
    }


    public boolean hasDiagnostics() {
        return diagnosticsView != null && !diagnosticsView.services.isEmpty();
    }



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

    public RepairJourneyView() {
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public int getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(int vehicleYear) {
        this.vehicleYear = vehicleYear;
    }

    public Integer getWorkOrderID() {
        return workOrderID;
    }

    public void setWorkOrderID(Integer workOrderID) {
        this.workOrderID = workOrderID;
    }

    public String getWorkOrderStatus() {
        return workOrderStatus;
    }

    public void setWorkOrderStatus(String workOrderStatus) {
        this.workOrderStatus = workOrderStatus;
    }

    public BigDecimal getEstimateAmount() {
        return estimateAmount;
    }

    public void setEstimateAmount(BigDecimal estimateAmount) {
        this.estimateAmount = estimateAmount;
    }

    public Timestamp getWorkOrderCreatedAt() {
        return workOrderCreatedAt;
    }

    public void setWorkOrderCreatedAt(Timestamp workOrderCreatedAt) {
        this.workOrderCreatedAt = workOrderCreatedAt;
    }

    public List<WorkOrderDetailView> getWorkOrderDetails() {
        return workOrderDetails;
    }

    public void setWorkOrderDetails(List<WorkOrderDetailView> workOrderDetails) {
        this.workOrderDetails = workOrderDetails;
    }

    public BigDecimal getTotalServiceCost() {
        return totalServiceCost;
    }

    public void setTotalServiceCost(BigDecimal totalServiceCost) {
        this.totalServiceCost = totalServiceCost;
    }

    public BigDecimal getTotalPartsCost() {
        return totalPartsCost;
    }

    public void setTotalPartsCost(BigDecimal totalPartsCost) {
        this.totalPartsCost = totalPartsCost;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
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
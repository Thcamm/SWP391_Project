package service.tracking;

import dao.customer.RepairJourneyDAO;
import model.customer.CustomerDiagnosticsView;
import model.dto.RepairJourneySummaryDTO;
import model.dto.RepairJourneyView;
import model.dto.RepairJourneyView.TimelineStage;
import model.dto.RepairJourneyView.TimelineStep;
import model.appointment.Appointment;
import model.feedback.Feedback;
import model.invoice.Invoice;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;
import service.diagnostic.CustomerDiagnosticService;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RepairTrackerService {

    private final RepairJourneyDAO journeyDAO = new RepairJourneyDAO();
    private static final int FEEDBACK_EXPIRATION_DAYS = 7;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");
    private CustomerDiagnosticService diagnosticService = new CustomerDiagnosticService();
    /**
     * Lấy và xử lý hành trình thành timeline có cấu trúc
     */
    public RepairJourneyView getProcessedJourney( int customerId, int requestId) throws SQLException {

        RepairJourneyView journey = journeyDAO.getRepairJourneyByRequestID( requestId);

        if (journey == null) {
            return null;
        }

        // Xây dựng timeline theo thứ tự
        buildTimeline(journey);

        CustomerDiagnosticsView diagView = diagnosticService.getDiagnosticsForRequest(customerId, requestId);
        journey.setDiagnosticsView(diagView);

        return journey;
    }

    /**
     * Xây dựng timeline chi tiết - CHỈ hiển thị stage đã xảy ra
     */
    private void buildTimeline(RepairJourneyView journey) {
        Appointment appointment = journey.getAppointment();
        ServiceRequest serviceRequest = journey.getServiceRequest();
        WorkOrder workOrder = journey.getWorkOrder();
        Invoice invoice = journey.getInvoice();
        Feedback feedback = journey.getFeedback();

        // 1. APPOINTMENT STAGE (nếu có)
        if (appointment != null) {
            TimelineStage apptStage = buildAppointmentStage(appointment);
            journey.getStages().add(apptStage);

            // Nếu appointment bị reject/cancel → DỪNG timeline
            if ("REJECTED".equals(appointment.getStatus()) ||
                    "CANCELLED".equals(appointment.getStatus())) {
                return;
            }
        }

        // 2. SERVICE REQUEST STAGE (luôn có)
        if (serviceRequest != null) {
            TimelineStage srStage = buildServiceRequestStage(serviceRequest);
            journey.getStages().add(srStage);

            // Nếu SR bị reject → DỪNG timeline
            if ("REJECTED".equals(serviceRequest.getStatus())) {
                return;
            }

            // Nếu SR vẫn pending → DỪNG timeline
            if ("PENDING".equals(serviceRequest.getStatus())) {
                return;
            }
        }

        // 3. WORK ORDER STAGE (nếu có)
        if (workOrder != null) {
            TimelineStage woStage = buildWorkOrderStage(workOrder);
            journey.getStages().add(woStage);

            // Nếu WO chưa complete → DỪNG timeline
            if (workOrder.getStatus() != WorkOrder.Status.COMPLETE) {
                return;
            }
        } else {
            return; // Chưa có WO → dừng
        }

        // 4. INVOICE STAGE (nếu có)
        if (invoice != null) {
            TimelineStage invStage = buildInvoiceStage(invoice);
            journey.getStages().add(invStage);

            // Nếu invoice chưa paid hoặc bị void → DỪNG timeline
            if (!"PAID".equals(invoice.getPaymentStatus())) {
                return;
            }
        } else {
            return; // Chưa có invoice → dừng
        }

        // 5. FEEDBACK STAGE (chỉ khi invoice đã paid)
        if ("PAID".equals(invoice.getPaymentStatus())) {
            TimelineStage fbStage = buildFeedbackStage(invoice, feedback, journey);
            journey.getStages().add(fbStage);
        }
    }

    /**
     * Xây dựng Appointment Stage
     */
    private TimelineStage buildAppointmentStage(Appointment appt) {
        TimelineStage stage = new TimelineStage("APPOINTMENT", "Đặt Lịch Hẹn", "bi-calendar-check");

        String status = appt.getStatus();

        // Bước 1: Tạo lịch hẹn (luôn có)
        stage.addStep(new TimelineStep(
                "Đã tạo lịch hẹn",
                formatDateTime(appt.getCreatedAt()),
                "check-circle",
                "success",
                true
        ));

        // Bước 2: Trạng thái hiện tại
        if ("ACCEPTED".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Lịch hẹn đã được chấp nhận",
                    formatDateTime(appt.getUpdatedAt()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
        } else if ("REJECTED".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Lịch hẹn đã bị từ chối",
                    formatDateTime(appt.getUpdatedAt()),
                    "x-circle",
                    "danger",
                    true
            ));
            stage.setOverallStatus("rejected");
        } else if ("CANCELLED".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Bạn đã hủy lịch hẹn",
                    formatDateTime(appt.getUpdatedAt()),
                    "dash-circle",
                    "secondary",
                    true
            ));
            stage.setOverallStatus("rejected");
        } else { // PENDING
            stage.addStep(new TimelineStep(
                    "Đang chờ xác nhận lịch hẹn",
                    "",
                    "clock",
                    "warning",
                    false
            ));
            stage.setOverallStatus("active");
        }

        return stage;
    }

    /**
     * Xây dựng Service Request Stage
     */
    private TimelineStage buildServiceRequestStage(ServiceRequest sr) {
        TimelineStage stage = new TimelineStage("SERVICE_REQUEST", "Yêu Cầu Dịch Vụ", "bi-file-earmark-text");

        String status = sr.getStatus();

        // Bước 1: Tiếp nhận yêu cầu (luôn có)
        stage.addStep(new TimelineStep(
                "Đã tiếp nhận yêu cầu dịch vụ",
                formatDateTime(sr.getRequestDate()),
                "check-circle",
                "success",
                true
        ));

        // Bước 2: Trạng thái hiện tại
        if ("APPROVE".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Yêu cầu đã được chấp thuận",
                    formatDateTime(sr.getUpdatedAt()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
        } else if ("REJECTED".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Yêu cầu dịch vụ đã bị từ chối",
                    formatDateTime(sr.getUpdatedAt()),
                    "x-circle",
                    "danger",
                    true
            ));
            stage.setOverallStatus("rejected");
        } else { // PENDING
            stage.addStep(new TimelineStep(
                    "Đang chờ xem xét yêu cầu",
                    "",
                    "clock",
                    "warning",
                    false
            ));
            stage.setOverallStatus("active");
        }

        return stage;
    }

    /**
     * Xây dựng Work Order Stage
     */
    private TimelineStage buildWorkOrderStage(WorkOrder wo) {
        TimelineStage stage = new TimelineStage("WORK_ORDER", "Quá Trình Sửa Chữa", "bi-tools");

        WorkOrder.Status status = wo.getStatus();

        // Bước 1: Bắt đầu sửa chữa (luôn có)
        stage.addStep(new TimelineStep(
                "Đã tiếp nhận xe và bắt đầu kiểm tra",
                formatDateTime(wo.getCreatedAt()),
                "check-circle",
                "success",
                true
        ));

        // Bước 2: Trạng thái hiện tại
        if (status == WorkOrder.Status.COMPLETE) {
            stage.addStep(new TimelineStep(
                    "Đã hoàn thành sửa chữa",
                    formatDateTime(wo.getUpdatedAt()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
        } else if (status == WorkOrder.Status.IN_PROCESS) {
            stage.addStep(new TimelineStep(
                    "Đang trong quá trình sửa chữa",
                    formatDateTime(wo.getUpdatedAt()),
                    "gear",
                    "primary",
                    false
            ));
            stage.setOverallStatus("active");
        } else { // PENDING
            stage.addStep(new TimelineStep(
                    "Chờ bắt đầu sửa chữa",
                    "",
                    "clock",
                    "warning",
                    false
            ));
            stage.setOverallStatus("active");
        }

        return stage;
    }

    /**
     * Xây dựng Invoice Stage
     */
    private TimelineStage buildInvoiceStage(Invoice inv) {
        TimelineStage stage = new TimelineStage("INVOICE", "Hóa Đơn", "bi-receipt");

        String status = inv.getPaymentStatus();

        // Bước 1: Xuất hóa đơn (luôn có)
        stage.addStep(new TimelineStep(
                "Đã xuất hóa đơn",
                formatDateTime(inv.getCreatedAt()),
                "check-circle",
                "success",
                true
        ));

        // Bước 2: Trạng thái thanh toán
        if ("PAID".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Đã thanh toán thành công",
                    formatDateTime(inv.getUpdatedAt()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
        } else if ("VOID".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Hóa đơn đã bị hủy",
                    formatDateTime(inv.getUpdatedAt()),
                    "x-circle",
                    "secondary",
                    true
            ));
            stage.setOverallStatus("rejected");
        } else { // UNPAID or PARTIALLY_PAID
            stage.addStep(new TimelineStep(
                    "Chờ thanh toán để nhận xe",
                    "",
                    "exclamation-triangle",
                    "warning",
                    false
            ));
            stage.setOverallStatus("active");
        }

        return stage;
    }

    /**
     * Xây dựng Feedback Stage
     */
    private TimelineStage buildFeedbackStage(Invoice inv, Feedback fb, RepairJourneyView journey) {
        TimelineStage stage = new TimelineStage("FEEDBACK", "Đánh Giá Dịch Vụ", "bi-star-fill");

        if (fb != null) {
            // Đã có feedback
            stage.addStep(new TimelineStep(
                    "Đã gửi đánh giá",
                    formatDateTime(fb.getFeedbackDate()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
            journey.setFeedbackAction("HAS_FEEDBACK");
        } else {
            // Chưa có feedback - kiểm tra 7 ngày
            Timestamp paidTimestamp = inv.getUpdatedAt();
            LocalDateTime paidDate = paidTimestamp.toLocalDateTime();
            long daysSincePayment = Duration.between(paidDate, LocalDateTime.now()).toDays();
            long daysLeft = FEEDBACK_EXPIRATION_DAYS - daysSincePayment;

            if (daysLeft > 0) {
                stage.addStep(new TimelineStep(
                        "Bạn có thể gửi đánh giá (Còn " + daysLeft + " ngày)",
                        "",
                        "pencil",
                        "primary",
                        false
                ));
                stage.setOverallStatus("active");
                journey.setFeedbackAction("ALLOW_FEEDBACK");
                journey.setFeedbackDaysLeft((int) daysLeft);
            } else {
                stage.addStep(new TimelineStep(
                        "Thời gian gửi đánh giá đã hết hạn",
                        "",
                        "clock-history",
                        "secondary",
                        true
                ));
                stage.setOverallStatus("rejected");
                journey.setFeedbackAction("EXPIRED");
            }
        }

        return stage;
    }

    /**
     * Format datetime
     */
    private String formatDateTime(Timestamp ts) {
        if (ts == null) return "";
        return ts.toLocalDateTime().format(FORMATTER);
    }

    private String formatDateTime(LocalDateTime ldt) {
        if (ldt == null) return "";
        return ldt.format(FORMATTER);
    }

    /**
     * Lấy danh sách tóm tắt cho trang List
     */
// SỬA ĐỔI phương thức này (đổi tên, thêm tham số)
    public List<RepairJourneySummaryDTO> getPaginatedSummariesForCustomer(int customerId, int limit, int offset) throws SQLException {
        // Gọi phương thức DAO đã được phân trang
        return journeyDAO.getPaginatedJourneySummaries(customerId, limit, offset);
    }

    // Thêm phương thức MỚI này để đếm
    public int countSummariesForCustomer(int customerId) throws SQLException {
        return journeyDAO.countJourneySummaries(customerId);
    }
    public List<RepairJourneySummaryDTO> getAllTracker(int limit, int offset) throws SQLException {
        return journeyDAO.getPaginatedTracking(limit, offset);
    }

    public int countAllTracker() throws SQLException {
        return journeyDAO.countAllTracking();
    }

}
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
     * Get and process the journey into a timeline structure
     */
    public RepairJourneyView getProcessedJourney( int customerId, int requestId) throws SQLException {

        RepairJourneyView journey = journeyDAO.getRepairJourneyByRequestID(requestId);

        if (journey == null) {
            return null;
        }

        // Build timeline in order
        buildTimeline(journey);

//        CustomerDiagnosticsView diagView = diagnosticService.getDiagnosticsForRequest(customerId, requestId);
//        journey.setDiagnosticsView(diagView);

        return journey;
    }

    /**
     * Build detailed timeline - ONLY show stages that have occurred
     */
    private void buildTimeline(RepairJourneyView journey) {
        Appointment appointment = journey.getAppointment();
        ServiceRequest serviceRequest = journey.getServiceRequest();
        WorkOrder workOrder = journey.getWorkOrder();
        Invoice invoice = journey.getInvoice();
        Feedback feedback = journey.getFeedback();

        // 1. APPOINTMENT STAGE (if exists)
        if (appointment != null) {
            TimelineStage apptStage = buildAppointmentStage(appointment);
            journey.getStages().add(apptStage);

            // If appointment rejected/cancelled → STOP timeline
            if ("REJECTED".equals(appointment.getStatus()) ||
                    "CANCELLED".equals(appointment.getStatus())) {
                return;
            }
        }

        // 2. SERVICE REQUEST STAGE (always exists)
        if (serviceRequest != null) {
            TimelineStage srStage = buildServiceRequestStage(serviceRequest);
            journey.getStages().add(srStage);

            // If service request rejected → STOP timeline
            if ("REJECTED".equals(serviceRequest.getStatus())) {
                return;
            }

            // If service request is still pending → STOP timeline
            if ("PENDING".equals(serviceRequest.getStatus())) {
                return;
            }
        }

        // 3. WORK ORDER STAGE (if exists)
        if (workOrder != null) {
            TimelineStage woStage = buildWorkOrderStage(workOrder);
            journey.getStages().add(woStage);

            // If work order not complete → STOP timeline
            if (workOrder.getStatus() != WorkOrder.Status.COMPLETE) {
                return;
            }
        } else {
            return; // No work order yet → stop
        }

        // 4. INVOICE STAGE (if exists)
        if (invoice != null) {
            TimelineStage invStage = buildInvoiceStage(invoice);
            journey.getStages().add(invStage);

            // If invoice not paid or void → STOP timeline
            if (!"PAID".equals(invoice.getPaymentStatus())) {
                return;
            }
        } else {
            return; // No invoice yet → stop
        }

        // 5. FEEDBACK STAGE (only when invoice is paid)
        if ("PAID".equals(invoice.getPaymentStatus())) {
            TimelineStage fbStage = buildFeedbackStage(invoice, feedback, journey);
            journey.getStages().add(fbStage);
        }
    }

    /**
     * Build Appointment Stage
     */
    private TimelineStage buildAppointmentStage(Appointment appt) {
        TimelineStage stage = new TimelineStage("APPOINTMENT", "Appointment", "bi-calendar-check");

        String status = appt.getStatus();

        // Step 1: Create appointment (always exists)
        stage.addStep(new TimelineStep(
                "Appointment created",
                formatDateTime(appt.getCreatedAt()),
                "check-circle",
                "success",
                true
        ));

        // Step 2: Current status
        if ("ACCEPTED".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Appointment accepted",
                    formatDateTime(appt.getUpdatedAt()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
        } else if ("REJECTED".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Appointment rejected",
                    formatDateTime(appt.getUpdatedAt()),
                    "x-circle",
                    "danger",
                    true
            ));
            stage.setOverallStatus("rejected");
        } else if ("CANCELLED".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Appointment cancelled by customer",
                    formatDateTime(appt.getUpdatedAt()),
                    "dash-circle",
                    "secondary",
                    true
            ));
            stage.setOverallStatus("rejected");
        } else { // PENDING
            stage.addStep(new TimelineStep(
                    "Waiting for appointment confirmation",
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
     * Build Service Request Stage
     */
    private TimelineStage buildServiceRequestStage(ServiceRequest sr) {
        TimelineStage stage = new TimelineStage("SERVICE_REQUEST", "Service Request", "bi-file-earmark-text");

        String status = sr.getStatus();

        // Step 1: Receive request (always exists)
        stage.addStep(new TimelineStep(
                "Service request received",
                formatDateTime(sr.getRequestDate()),
                "check-circle",
                "success",
                true
        ));

        // Step 2: Current status
        if ("APPROVE".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Service request approved",
                    formatDateTime(sr.getUpdatedAt()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
        } else if ("REJECTED".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Service request rejected",
                    formatDateTime(sr.getUpdatedAt()),
                    "x-circle",
                    "danger",
                    true
            ));
            stage.setOverallStatus("rejected");
        } else { // PENDING
            stage.addStep(new TimelineStep(
                    "Service request pending review",
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
     * Build Work Order Stage
     */
    private TimelineStage buildWorkOrderStage(WorkOrder wo) {
        TimelineStage stage = new TimelineStage("WORK_ORDER", "Repair Process", "bi-tools");

        WorkOrder.Status status = wo.getStatus();

        // Step 1: Start repair (always exists)
        stage.addStep(new TimelineStep(
                "Vehicle received and inspection started",
                formatDateTime(wo.getCreatedAt()),
                "check-circle",
                "success",
                true
        ));

        // Step 2: Current status
        if (status == WorkOrder.Status.COMPLETE) {
            stage.addStep(new TimelineStep(
                    "Repair completed",
                    formatDateTime(wo.getUpdatedAt()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
        } else if (status == WorkOrder.Status.IN_PROCESS) {
            stage.addStep(new TimelineStep(
                    "Repair in progress",
                    formatDateTime(wo.getUpdatedAt()),
                    "gear",
                    "primary",
                    false
            ));
            stage.setOverallStatus("active");
        } else { // PENDING
            stage.addStep(new TimelineStep(
                    "Waiting to start repair",
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
     * Build Invoice Stage
     */
    private TimelineStage buildInvoiceStage(Invoice inv) {
        TimelineStage stage = new TimelineStage("INVOICE", "Invoice", "bi-receipt");

        String status = inv.getPaymentStatus();

        // Step 1: Issue invoice (always exists)
        stage.addStep(new TimelineStep(
                "Invoice issued",
                formatDateTime(inv.getCreatedAt()),
                "check-circle",
                "success",
                true
        ));

        // Step 2: Payment status
        if ("PAID".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Payment successful",
                    formatDateTime(inv.getUpdatedAt()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
        } else if ("VOID".equals(status)) {
            stage.addStep(new TimelineStep(
                    "Invoice voided",
                    formatDateTime(inv.getUpdatedAt()),
                    "x-circle",
                    "secondary",
                    true
            ));
            stage.setOverallStatus("rejected");
        } else { // UNPAID or PARTIALLY_PAID
            stage.addStep(new TimelineStep(
                    "Awaiting payment to collect vehicle",
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
     * Build Feedback Stage
     */
    private TimelineStage buildFeedbackStage(Invoice inv, Feedback fb, RepairJourneyView journey) {
        TimelineStage stage = new TimelineStage("FEEDBACK", "Service Feedback", "bi-star-fill");

        if (fb != null) {
            // Feedback already submitted
            stage.addStep(new TimelineStep(
                    "Feedback submitted",
                    formatDateTime(fb.getFeedbackDate()),
                    "check-circle",
                    "success",
                    true
            ));
            stage.setOverallStatus("completed");
            journey.setFeedbackAction("HAS_FEEDBACK");
        } else {
            // No feedback yet - check 7-day window
            Timestamp paidTimestamp = inv.getUpdatedAt();
            LocalDateTime paidDate = paidTimestamp.toLocalDateTime();
            long daysSincePayment = Duration.between(paidDate, LocalDateTime.now()).toDays();
            long daysLeft = FEEDBACK_EXPIRATION_DAYS - daysSincePayment;

            if (daysLeft > 0) {
                stage.addStep(new TimelineStep(
                        "You can submit feedback (" + daysLeft + " days left)",
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
                        "Feedback period expired",
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
     * Get summary list for the customer list page
     */
// Thêm vào RepairTrackerService.java

    /**
     * Get summary list with sort order
     */
    public List<RepairJourneySummaryDTO> getPaginatedSummariesForCustomer(
            int customerId, int limit, int offset) throws SQLException {
        return journeyDAO.getPaginatedJourneySummaries(customerId, limit, offset);
    }

    /**
     * Get summary list filtered by vehicle
     */
    public List<RepairJourneySummaryDTO> getPaginatedSummariesForCustomerByVehicle(
            int customerId, int vehicleId, String sortBy, int limit, int offset) throws SQLException {
        return journeyDAO.getPaginatedJourneySummariesByVehicle(customerId, vehicleId, sortBy, limit, offset);
    }

    /**
     * Count summaries filtered by vehicle
     */
    public int countSummariesForCustomerByVehicle(int customerId, int vehicleId) throws SQLException {
        return journeyDAO.countJourneySummariesByVehicle(customerId, vehicleId);
    }

    // New method to count summaries
    public int countSummariesForCustomer(int customerId) throws SQLException {
        return journeyDAO.countJourneySummaries(customerId);
    }

    public List<RepairJourneySummaryDTO> getAllTracker(int limit, int offset) throws SQLException {
        return journeyDAO.getPaginatedTracking(limit, offset);
    }

    public int countAllTracker() throws SQLException {
        return journeyDAO.countAllTracking();
    }
// Thêm các phương thức này vào RepairTrackerService

    public List<RepairJourneySummaryDTO> getFilteredTracker(
            String fullName, Integer vehicleId, String sortBy, int limit, int offset) throws SQLException {
        return journeyDAO.getFilteredTracking(fullName, vehicleId, sortBy, limit, offset);
    }

    public int countFilteredTracker(String fullName, Integer vehicleId) throws SQLException {
        return journeyDAO.countFilteredTracking(fullName, vehicleId);
    }
}

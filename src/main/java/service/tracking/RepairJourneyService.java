package service;

import dao.customer.RepairJourneyDAO;
import model.dto.DiagnosticPartView;
import model.dto.RepairJourneySummaryDTO;
import model.dto.RepairJourneyView;
import model.dto.WorkOrderDetailView;
import model.appointment.Appointment;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;
import model.invoice.Invoice;
import model.feedback.Feedback;
import common.utils.PaginationUtils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class RepairJourneyService {

    private final RepairJourneyDAO dao;
    private final DateTimeFormatter tsFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    public RepairJourneyService(RepairJourneyDAO dao) {
        this.dao = dao;
    }


    public RepairJourneyView getFullJourney(int requestId, Integer customerId) throws SQLException {

        RepairJourneyView view = dao.getRepairJourneyByRequestID(requestId);
        if (view == null) return null;


        if (customerId != null) {
            boolean ok = dao.verifyRequestOwnership(requestId, customerId);
            if (!ok) throw new SecurityException("Không có quyền truy cập hành trình này.");
        }


        Integer woId = dao.getWorkOrderIDByRequestID(requestId);
        view.setWorkOrderID(woId);

        if (woId != null) {

            List<WorkOrderDetailView> details = dao.getWorkOrderDetails(woId);


            for (WorkOrderDetailView d : details) {
                dao.populateTaskAssignment(d);            // Assignment + technician
                dao.populateVehicleDiagnostic(d);         // Diagnostic core
                if (d.getVehicleDiagnosticID() != null) {
                    List<DiagnosticPartView> parts = dao.getDiagnosticParts(d.getVehicleDiagnosticID());
                    d.setDiagnosticParts(parts);
                    d.calculateApprovedPartsCost();       // tính approvedPartsCost, counts
                }
            }

            view.setWorkOrderDetails(details);


            calcTotals(view);
        }


        flattenTopLevelFields(view);


        buildTimeline(view);
        computeFeedbackWindow(view);

        return view;
    }

    /**
     * Approve/Unapprove hàng loạt parts trong một vehicleDiagnostic.
     * Có verify quyền sở hữu theo customer.
     * Trả về danh sách parts mới sau cập nhật (đã sort như trong DAO) và số liệu tổng.
     */
    public UpdatePartsResult updateDiagnosticPartsApproval(int vehicleDiagnosticId,
                                                           List<Integer> diagnosticPartIds,
                                                           boolean approve,
                                                           Integer customerId) throws SQLException {
        if (diagnosticPartIds == null || diagnosticPartIds.isEmpty()) {
            return UpdatePartsResult.empty();
        }

        // Verify ownership (nếu là customer portal)
        if (customerId != null) {
            boolean ok = dao.verifyDiagnosticOwnership(vehicleDiagnosticId, customerId);
            if (!ok) throw new SecurityException("Bạn không thể cập nhật parts của chẩn đoán này.");
        }

        boolean success = dao.updateMultipleDiagnosticParts(diagnosticPartIds, approve);
        if (!success) return UpdatePartsResult.empty();

        // Reload lại danh sách parts để trả về giao diện
        List<DiagnosticPartView> parts = dao.getDiagnosticParts(vehicleDiagnosticId);

        // Tính lại approvedPartsCost và counters (tạm thời dùng một detail giả)
        WorkOrderDetailView tmp = new WorkOrderDetailView();
        tmp.setDiagnosticParts(parts);
        tmp.calculateApprovedPartsCost();

        return new UpdatePartsResult(parts, tmp.getApprovedPartsCost(), tmp.getApprovedPartsCount(), tmp.getTotalPartsCount());
    }

    /**
     * Lấy danh sách tóm tắt theo trang cho 1 khách hàng.
     */
    public PaginatedResult<RepairJourneySummaryDTO> getCustomerJourneySummaries(int customerId,
                                                                                int currentPage,
                                                                                int itemsPerPage) throws SQLException {
        int total = dao.countJourneySummaries(customerId);
        PaginationUtils.PaginationCalculation calc = PaginationUtils.calculateParams(total, currentPage, itemsPerPage);
        List<RepairJourneySummaryDTO> items = dao.getPaginatedJourneySummaries(customerId, Math.max(1, itemsPerPage), calc.getOffset());
        return new PaginatedResult<>(items, calc.getSafePage(), Math.max(1, itemsPerPage), total);
    }

    /**
     * Lấy danh sách tóm tắt tất cả tracking (cho admin/CS) có phân trang.
     */
    public PaginatedResult<RepairJourneySummaryDTO> getAllTracking(int page, int pageSize) throws SQLException {
        int limit = Math.max(1, pageSize);
        int offset = Math.max(0, (page - 1)) * limit;
        int total = dao.countAllTracking();
        List<RepairJourneySummaryDTO> items = dao.getPaginatedTracking(limit, offset);
        return new PaginatedResult<>(items, page, pageSize, total);
    }

    // ============================
    // Private helpers
    // ============================

    private void calcTotals(RepairJourneyView view) {
        BigDecimal serviceTotal = BigDecimal.ZERO;
        BigDecimal partsTotal = BigDecimal.ZERO;

        for (WorkOrderDetailView d : view.getWorkOrderDetails()) {
            if (d.getEstimateAmount() != null) {
                serviceTotal = serviceTotal.add(d.getEstimateAmount());
            }
            partsTotal = partsTotal.add(d.getApprovedPartsCost());
        }

        view.setTotalServiceCost(serviceTotal);
        view.setTotalPartsCost(partsTotal);
        view.setGrandTotal(serviceTotal.add(partsTotal));
    }

    private void flattenTopLevelFields(RepairJourneyView view) {
        ServiceRequest sr = view.getServiceRequest();
        if (sr != null) {
            view.setRequestID(sr.getRequestID());
            view.setRequestDate(sr.getRequestDate());
            view.setRequestStatus(sr.getStatus());
        }
        WorkOrder wo = view.getWorkOrder();
        if (wo != null) {
            view.setWorkOrderStatus(wo.getStatus().name());
            view.setWorkOrderCreatedAt(wo.getCreatedAt());
        }
        Invoice inv = view.getInvoice();
        if (inv != null) {
            view.setEstimateAmount(inv.getTotalAmount()); // nếu có; nếu không dùng wo.EstimateAmount
        }
    }

    private void buildTimeline(RepairJourneyView view) {
        List<RepairJourneyView.TimelineStage> stages = new ArrayList<>();

        // Stage: Appointment
        Appointment appt = view.getAppointment();
        if (appt != null) {
            RepairJourneyView.TimelineStage s = new RepairJourneyView.TimelineStage(
                    "APPOINTMENT", "Đặt lịch hẹn", "bi-calendar-check");
            s.addStep(step("Đã tạo lịch hẹn",
                    ts(appt.getCreatedAt() == null ? null : Timestamp.valueOf(appt.getCreatedAt())),
                    "check-circle", "success", true));
            s.addStep(step(statusVN(appt.getStatus()),
                    ts(appt.getUpdatedAt() == null ? null : Timestamp.valueOf(appt.getUpdatedAt())),
                    iconByStatus(appt.getStatus()), colorByStatus(appt.getStatus()),
                    isDone(appt.getStatus())));
            s.setOverallStatus(overallBy(appt.getStatus()));
            stages.add(s);
        }

        // Stage: Service Request
        ServiceRequest sr = view.getServiceRequest();
        if (sr != null) {
            RepairJourneyView.TimelineStage s = new RepairJourneyView.TimelineStage(
                    "SERVICE_REQUEST", "Yêu cầu dịch vụ", "bi-clipboard-check");
            s.addStep(step("Đã tạo yêu cầu",
                    ts(sr.getRequestDate()),
                    "check-circle", "success", true));
            s.addStep(step(statusVN(sr.getStatus()), ts(sr.getUpdatedAt()),
                    iconByStatus(sr.getStatus()), colorByStatus(sr.getStatus()), isDone(sr.getStatus())));
            s.setOverallStatus(overallBy(sr.getStatus()));
            stages.add(s);
        }

        // Stage: Work Order (gồm detail/task/diagnostic)
        WorkOrder wo = view.getWorkOrder();
        if (wo != null) {
            RepairJourneyView.TimelineStage s = new RepairJourneyView.TimelineStage(
                    "WORK_ORDER", "Lệnh sửa chữa", "bi-wrench-adjustable");
            s.addStep(step("Đã tạo lệnh sửa chữa", ts(wo.getCreatedAt()),
                    "check-circle", "success", true));

            // Các detail thể hiện dưới dạng nhiều step nhỏ
            int idx = 1;
            for (WorkOrderDetailView d : view.getWorkOrderDetails()) {
                String title = "Hạng mục #" + idx++ + ": " + safe(d.getTaskDescription());
                s.addStep(step(title + " (" + safe(d.getDetailStatus()) + ")",
                        ts(wo.getUpdatedAt()), "gear", "primary", Objects.equals(d.getTaskStatus(), "COMPLETE")));

                // Nếu có Assignment -> show
                if (d.getAssignmentID() != null) {
                    s.addStep(step("Đã phân công cho " + safe(d.getTechnicianName()),
                            ts(d.getAssignedDate()), "person-workspace", "primary", true));
                    if (d.getStartAt() != null) {
                        s.addStep(step("Bắt đầu thực hiện", ts(d.getStartAt()), "play-circle", "primary", true));
                    }
                    if (d.getCompleteAt() != null) {
                        s.addStep(step("Hoàn thành hạng mục", ts(d.getCompleteAt()), "check2-circle", "success", true));
                    }
                }

                // Nếu có Diagnostic -> show trạng thái + parts
                if (d.getVehicleDiagnosticID() != null) {
                    s.addStep(step("Kết quả chẩn đoán: " + safe(d.getDiagnosticStatus()),
                            ts(wo.getUpdatedAt()), iconByStatus(d.getDiagnosticStatus()), colorByStatus(d.getDiagnosticStatus()),
                            isDone(d.getDiagnosticStatus())));

                    String partsSummary = String.format("Parts đã duyệt: %d/%d (%,.0f)",
                            d.getApprovedPartsCount(), d.getTotalPartsCount(), d.getApprovedPartsCost());
                    s.addStep(step(partsSummary, ts(wo.getUpdatedAt()), "boxes", "secondary", d.getApprovedPartsCount() > 0));
                }
            }

            s.setOverallStatus(overallBy(wo.getStatus().name()));
            stages.add(s);
        }

        // Stage: Invoice
        Invoice inv = view.getInvoice();
        if (inv != null) {
            RepairJourneyView.TimelineStage s = new RepairJourneyView.TimelineStage(
                    "INVOICE", "Hóa đơn & thanh toán", "bi-receipt");
            s.addStep(step("Đã phát hành hóa đơn", ts(inv.getCreatedAt()), "receipt", "primary", true));
            s.addStep(step("Trạng thái thanh toán: " + safe(inv.getPaymentStatus()), ts(inv.getUpdatedAt()),
                    iconByPayment(inv.getPaymentStatus()), colorByPayment(inv.getPaymentStatus()),
                    Objects.equals(inv.getPaymentStatus(), "PAID")));
            s.setOverallStatus(Objects.equals(inv.getPaymentStatus(), "PAID") ? "completed" : "active");
            stages.add(s);
        }

        // Stage: Feedback
        Feedback fb = view.getFeedback();
        if (fb != null || view.getFeedbackAction() != null) {
            RepairJourneyView.TimelineStage s = new RepairJourneyView.TimelineStage(
                    "FEEDBACK", "Đánh giá dịch vụ", "bi-chat-heart");

            if (fb != null) {
                s.addStep(step("Khách đã gửi đánh giá", ts(fb.getFeedbackDate() == null ? null : Timestamp.valueOf(fb.getFeedbackDate())),
                        "hand-thumbs-up", "success", true));
                if (fb.getReplyDate() != null) {
                    s.addStep(step("Đã phản hồi khách", ts(Timestamp.valueOf(fb.getReplyDate())), "reply", "secondary", true));
                }
                s.setOverallStatus("completed");
            } else {
                String title = switch (safe(view.getFeedbackAction())) {
                    case "ALLOW_FEEDBACK" -> "Đang mở nhận đánh giá";
                    case "EXPIRED" -> "Hết hạn gửi đánh giá";
                    default -> "";
                };
                if (!title.isEmpty()) {
                    s.addStep(step(title, null, "hourglass", "warning", false));
                    s.setOverallStatus(Objects.equals(view.getFeedbackAction(), "EXPIRED") ? "rejected" : "active");
                }
            }

            stages.add(s);
        }

        view.setStages(stages);
    }

    private void computeFeedbackWindow(RepairJourneyView view) {
        Invoice inv = view.getInvoice();
        Feedback fb = view.getFeedback();
        if (inv == null || fb != null) {
            // Không có hóa đơn => chưa tới bước feedback, hoặc đã có feedback rồi
            view.setFeedbackAction(fb != null ? "HAS_FEEDBACK" : null);
            view.setFeedbackDaysLeft(null);
            return;
        }

        Timestamp updatedAt = inv.getUpdatedAt(); // đã được DAO set
        if (updatedAt == null) {
            view.setFeedbackAction(null);
            view.setFeedbackDaysLeft(null);
            return;
        }

        Instant invUpdate = updatedAt.toInstant();
        Instant now = Instant.now();
        long days = 7 - Duration.between(invUpdate, now).toDays();

        if (days > 0) {
            view.setFeedbackAction("ALLOW_FEEDBACK");
            view.setFeedbackDaysLeft((int) days);
        } else {
            view.setFeedbackAction("EXPIRED");
            view.setFeedbackDaysLeft(0);
        }
    }

    // ============================
    // Timeline helpers
    // ============================

    private RepairJourneyView.TimelineStep step(String text, String ts,
                                                String icon, String color, boolean done) {
        return new RepairJourneyView.TimelineStep(text, ts, icon, color, done);
    }

    private String ts(Timestamp ts) {
        if (ts == null) return null;
        return tsFmt.format(ts.toInstant());
    }

    private String statusVN(String status) {
        if (status == null) return "";
        return switch (status) {
            case "PENDING" -> "Đang chờ";
            case "APPROVE", "APPROVED" -> "Đã chấp nhận";
            case "REJECTED", "DECLINED" -> "Từ chối";
            case "IN_PROCESS", "IN_PROGRESS" -> "Đang thực hiện";
            case "COMPLETE", "COMPLETED" -> "Hoàn thành";
            default -> status;
        };
    }

    private String iconByStatus(String status) {
        if (status == null) return "clock";
        return switch (status) {
            case "PENDING" -> "clock";
            case "APPROVE", "APPROVED", "COMPLETE", "COMPLETED" -> "check-circle";
            case "REJECTED", "DECLINED" -> "x-circle";
            case "IN_PROCESS", "IN_PROGRESS" -> "gear";
            default -> "info-circle";
        };
    }

    private String colorByStatus(String status) {
        if (status == null) return "secondary";
        return switch (status) {
            case "PENDING" -> "warning";
            case "APPROVE", "APPROVED", "COMPLETE", "COMPLETED" -> "success";
            case "REJECTED", "DECLINED" -> "danger";
            case "IN_PROCESS", "IN_PROGRESS" -> "primary";
            default -> "secondary";
        };
    }

    private String iconByPayment(String paymentStatus) {
        if (paymentStatus == null) return "cash-coin";
        return switch (paymentStatus) {
            case "PAID" -> "check-circle";
            case "UNPAID" -> "cash-coin";
            case "PARTIAL" -> "cash-stack";
            default -> "info-circle";
        };
    }

    private String colorByPayment(String paymentStatus) {
        if (paymentStatus == null) return "secondary";
        return switch (paymentStatus) {
            case "PAID" -> "success";
            case "UNPAID" -> "warning";
            case "PARTIAL" -> "primary";
            default -> "secondary";
        };
    }

    private boolean isDone(String status) {
        if (status == null) return false;
        return switch (status) {
            case "APPROVE", "APPROVED", "COMPLETE", "COMPLETED", "PAID" -> true;
            default -> false;
        };
    }

    private String overallBy(String status) {
        if (status == null) return "active";
        return switch (status) {
            case "REJECTED", "DECLINED" -> "rejected";
            case "COMPLETE", "COMPLETED", "PAID" -> "completed";
            default -> "active";
        };
    }

    private String safe(String s) { return s == null ? "" : s; }

    // ============================
    // DTOs phụ trợ cho Service
    // ============================

    public static class PaginatedResult<T> {
        public final List<T> items;
        public final int page;
        public final int pageSize;
        public final int totalItems;
        public final int totalPages;

        public PaginatedResult(List<T> items, int page, int pageSize, int totalItems) {
            this.items = items == null ? Collections.emptyList() : items;
            this.page = Math.max(1, page);
            this.pageSize = Math.max(1, pageSize);
            this.totalItems = Math.max(0, totalItems);
            this.totalPages = (int) Math.ceil(this.totalItems / (double) this.pageSize);
        }
    }

    public static class UpdatePartsResult {
        public final List<DiagnosticPartView> parts;
        public final BigDecimal approvedPartsCost;
        public final int approvedCount;
        public final int totalCount;

        public UpdatePartsResult(List<DiagnosticPartView> parts, BigDecimal cost, int approvedCount, int totalCount) {
            this.parts = parts;
            this.approvedPartsCost = cost == null ? BigDecimal.ZERO : cost;
            this.approvedCount = approvedCount;
            this.totalCount = totalCount;
        }

        public static UpdatePartsResult empty() {
            return new UpdatePartsResult(new ArrayList<>(), BigDecimal.ZERO, 0, 0);
        }
    }


    public int acceptDiagnosticAndCreateWorkDetail(int vehicleDiagnosticId, Integer customerId) throws SQLException {
        // Verify ownership theo customer (nếu có)
        if (customerId != null) {
            boolean ok = dao.verifyDiagnosticOwnership(vehicleDiagnosticId, customerId);
            if (!ok) throw new SecurityException("Bạn không có quyền chốt chẩn đoán này.");
        }

        // Xác định WorkOrder cha
        Integer woId = dao.getWorkOrderIdByDiagnostic(vehicleDiagnosticId);
        if (woId == null) throw new IllegalStateException("Không tìm thấy WorkOrder cha cho VehicleDiagnostic " + vehicleDiagnosticId);

        // Lấy danh sách parts & lọc parts đã duyệt
        List<DiagnosticPartView> parts = dao.getDiagnosticParts(vehicleDiagnosticId);
        List<DiagnosticPartView> approved = new ArrayList<>();
        for (DiagnosticPartView p : parts) if (p.getIsApproved() == 1) approved.add(p);
        if (approved.isEmpty()) throw new IllegalStateException("Chưa có part nào được duyệt, không thể chốt chẩn đoán.");

        // Tính tổng estimate
        BigDecimal partsTotal = BigDecimal.ZERO;
        for (DiagnosticPartView p : approved) {
            BigDecimal unit = p.getUnitPrice() == null ? BigDecimal.ZERO : p.getUnitPrice();
            BigDecimal qty = BigDecimal.valueOf(Math.max(0, p.getQuantityNeeded()));
            partsTotal = partsTotal.add(unit.multiply(qty));
        }
        BigDecimal diagnosticFee = BigDecimal.ZERO; // nếu muốn cộng thêm phí chẩn đoán, đọc từ VehicleDiagnostic.EstimateCost
        BigDecimal estimateAmount = partsTotal.add(diagnosticFee);

        try {
            dao.beginTx();

            // 1) Accept diagnostic
            dao.acceptVehicleDiagnostic(vehicleDiagnosticId);

            // 2) Tạo WorkOrderDetail mới
            String taskDescription = "Vật tư theo chẩn đoán #" + vehicleDiagnosticId;
            int newDetailId = dao.insertWorkOrderDetail(
                    woId,
                    "DIAGNOSTIC",
                    vehicleDiagnosticId,
                    "APPROVED",
                    taskDescription,
                    estimateAmount,
                    "PENDING"
            );

            // 3) Materialize WorkOrderPart từ parts đã duyệt
            for (DiagnosticPartView p : approved) {
                dao.insertWorkOrderPart(
                        newDetailId,
                        p.getPartDetailID(),
                        p.getQuantityNeeded(),
                        p.getUnitPrice() == null ? BigDecimal.ZERO : p.getUnitPrice(),
                        p.getTotalPrice() == null ? BigDecimal.ZERO : p.getTotalPrice(),
                        "DIAGNOSTIC"
                );
            }

            dao.commitTx();
            return newDetailId;
        } catch (SQLException | RuntimeException ex) {
            dao.rollbackTx();
            throw ex;
        }
    }

}

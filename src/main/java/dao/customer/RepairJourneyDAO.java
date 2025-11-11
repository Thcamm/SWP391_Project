package dao.customer;

import common.DbContext; // Lớp DbContext của bạn
import model.dto.RepairJourneySummaryDTO;
import model.dto.RepairJourneyView;
// Import các model của bạn
import model.appointment.Appointment;
import model.feedback.Feedback;
import model.invoice.Invoice;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class RepairJourneyDAO extends DbContext {

    /**
     * Lấy toàn bộ hành trình sửa chữa dựa trên RequestID và CustomerID.
     * Sử dụng LEFT JOIN vì Appointment, WorkOrder, Invoice, Feedback có thể chưa tồn tại.
     */
    public RepairJourneyView getRepairJourneyByRequestID( int requestId) throws SQLException {

        RepairJourneyView journey = null; // Khởi tạo là null

        // Câu SQL này lấy tất cả các trường cần thiết, bao gồm cả CreatedAt/UpdatedAt
        String sql = """
            SELECT 
                -- Appointment (a)
                a.AppointmentID, a.AppointmentDate, a.Status AS ApptStatus, a.Description, 
                a.CreatedAt AS ApptCreatedAt, a.UpdatedAt AS ApptUpdatedAt,
                
                -- ServiceRequest (sr)
                sr.RequestID, sr.VehicleID, sr.RequestDate, sr.Status AS SRStatus, sr.Note,
                sr.UpdatedAt AS SRUpdatedAt, 
                
                -- WorkOrder (wo)
                wo.WorkOrderID, wo.TechManagerID, wo.EstimateAmount, wo.Status AS WOStatus,
                wo.CreatedAt AS WOCreatedAt, wo.UpdatedAt AS WOUUpdatedAt,
                
                -- Invoice (i)
                i.InvoiceID, i.InvoiceNumber, i.InvoiceDate, i.PaymentStatus, 
                i.CreatedAt AS InvCreatedAt, i.UpdatedAt AS InvUpdatedAt,
                
                -- Feedback (f)
                f.FeedbackID, f.Rating, f.FeedbackText, f.Status AS FeedbackStatus,
                f.FeedbackDate, f.ReplyDate
                
            FROM ServiceRequest sr
            LEFT JOIN Appointment a ON sr.AppointmentID = a.AppointmentID
            LEFT JOIN WorkOrder wo ON sr.RequestID = wo.RequestID
            LEFT JOIN Invoice i ON wo.WorkOrderID = i.WorkOrderID
            LEFT JOIN Feedback f ON wo.WorkOrderID = f.WorkOrderID
            
            WHERE sr.RequestID = ? 
        """;

        try (Connection conn = getConnection(); // Lấy connection từ DbContext
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Nếu tìm thấy, mới khởi tạo DTO
                    journey = new RepairJourneyView();

                    // --- 1. Tải ServiceRequest (Chắc chắn có) ---
                    ServiceRequest sr = new ServiceRequest();
                    sr.setRequestID(rs.getInt("RequestID"));
                    // sr.setVehicleID(rs.getInt("VehicleID")); // (Nếu bạn cần)
                    sr.setRequestDate(rs.getTimestamp("RequestDate")); // Đây là CreatedAt
                    sr.setUpdatedAt(rs.getTimestamp("SRUpdatedAt"));
                    sr.setStatus(rs.getString("SRStatus"));
                    journey.setServiceRequest(sr);

                    // --- 2. Tải Appointment (Có thể null) ---
                    if (rs.getObject("AppointmentID") != null) {
                        Appointment appt = new Appointment();
                        appt.setAppointmentID(rs.getInt("AppointmentID"));
                        appt.setAppointmentDate(rs.getTimestamp("AppointmentDate").toLocalDateTime());
                        appt.setStatus(rs.getString("ApptStatus"));
                        appt.setCreatedAt(rs.getTimestamp("ApptCreatedAt").toLocalDateTime());
                        appt.setUpdatedAt(rs.getTimestamp("ApptUpdatedAt").toLocalDateTime());
                        journey.setAppointment(appt);
                    }

                    // --- 3. Tải WorkOrder (Có thể null) ---
                    if (rs.getObject("WorkOrderID") != null) {
                        WorkOrder wo = new WorkOrder();
                        wo.setWorkOrderId(rs.getInt("WorkOrderID"));
                        // Chuyển string status -> enum WorkOrder.Status
                        wo.setStatus(WorkOrder.Status.valueOf(rs.getString("WOStatus")));
                        wo.setCreatedAt(rs.getTimestamp("WOCreatedAt"));
                        wo.setUpdatedAt(rs.getTimestamp("WOUUpdatedAt"));
                        journey.setWorkOrder(wo);
                    }

                    // --- 4. Tải Invoice (Có thể null) ---
                    if (rs.getObject("InvoiceID") != null) {
                        Invoice inv = new Invoice();
                        inv.setInvoiceID(rs.getInt("InvoiceID"));
                        inv.setPaymentStatus(rs.getString("PaymentStatus"));
                        inv.setCreatedAt(rs.getTimestamp("InvCreatedAt"));
                        inv.setUpdatedAt(rs.getTimestamp("InvUpdatedAt")); // Rất quan trọng cho logic 7 ngày
                        journey.setInvoice(inv);
                    }

                    // --- 5. Tải Feedback (Có thể null) ---
                    if (rs.getObject("FeedbackID") != null) {
                        Feedback fb = new Feedback();
                        fb.setFeedbackID(rs.getInt("FeedbackID"));
                        fb.setStatus(rs.getString("FeedbackStatus"));
                        Timestamp fbTs = rs.getTimestamp("FeedbackDate");
                        Timestamp replyTs = rs.getTimestamp("ReplyDate");
                        if(fbTs != null) fb.setFeedbackDate(fbTs.toLocalDateTime());
                        if(replyTs != null) fb.setReplyDate(replyTs.toLocalDateTime());
                        journey.setFeedback(fb);
                    }
                }
            }
        }
        return journey; // Trả về DTO (có thể là null nếu không tìm thấy)
    }
    /**
     * Lấy danh sách tóm tắt tất cả các hành trình sửa chữa của một khách hàng.
     * Dùng cho trang "Lịch sử sửa chữa" (trang List).
     */
    public List<RepairJourneySummaryDTO> getJourneySummaries(int customerId) throws SQLException {
        List<RepairJourneySummaryDTO> list = new ArrayList<>();

        /*
         * Câu SQL này dùng:
         * 1. CASE...WHEN: Để xác định "EntryType" (Loại hình)
         * 2. COALESCE: Để lấy ngày bắt đầu (ưu tiên ngày hẹn)
         * 3. CASE...WHEN: Để xác định "Giai đoạn" mới nhất (check ngược từ Invoice -> WorkOrder)
         * 4. COALESCE: Để lấy "Trạng thái" mới nhất (check ngược)
         */
        String sql = """
            SELECT
                sr.RequestID,
                sr.VehicleID,

                -- 1. Logic cho "Loại hình"
                CASE
                    WHEN a.AppointmentID IS NOT NULL THEN 'Appointment'
                    ELSE 'Walk-in'
                END AS EntryType,
                
                -- 2. Logic cho "Ngày bắt đầu"
                COALESCE(a.AppointmentDate, sr.RequestDate) AS EntryDate,

                -- 3. Logic cho "Giai đoạn mới nhất"
                CASE
                    WHEN i.InvoiceID IS NOT NULL THEN 'Invoice'
                    WHEN wo.WorkOrderID IS NOT NULL THEN 'Work Order'
                    ELSE 'Service Request'
                END AS LatestStage,
                
                -- 4. Logic cho "Trạng thái mới nhất"
                COALESCE(i.PaymentStatus, wo.Status, sr.Status) AS LatestStatus

            FROM servicerequest sr
            LEFT JOIN appointment a ON sr.AppointmentID = a.AppointmentID
            LEFT JOIN workorder wo ON sr.RequestID = wo.RequestID
            LEFT JOIN invoice i ON wo.WorkOrderID = i.WorkOrderID
            WHERE sr.CustomerID = ?
            ORDER BY sr.RequestDate DESC -- Hiển thị yêu cầu mới nhất lên đầu
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairJourneySummaryDTO summary = new RepairJourneySummaryDTO();
                    summary.setRequestID(rs.getInt("RequestID"));
                    summary.setVehicleID(rs.getInt("VehicleID"));
                    summary.setEntryType(rs.getString("EntryType"));
                    summary.setEntryDate(rs.getTimestamp("EntryDate"));
                    summary.setLatestStage(rs.getString("LatestStage"));
                    summary.setLatestStatus(rs.getString("LatestStatus"));
                    list.add(summary);
                }
            }
        }
        return list; // Trả về danh sách
    }

    public List<RepairJourneySummaryDTO> getAllTracking() throws SQLException {
        List<RepairJourneySummaryDTO> list = new ArrayList<>();

        /*
         * Câu SQL này dùng:
         * 1. CASE...WHEN: Để xác định "EntryType" (Loại hình)
         * 2. COALESCE: Để lấy ngày bắt đầu (ưu tiên ngày hẹn)
         * 3. CASE...WHEN: Để xác định "Giai đoạn" mới nhất (check ngược từ Invoice -> WorkOrder)
         * 4. COALESCE: Để lấy "Trạng thái" mới nhất (check ngược)
         */
        String sql = """
            SELECT
                sr.RequestID,
                sr.VehicleID,

                -- 1. Logic cho "Loại hình"
                CASE
                    WHEN a.AppointmentID IS NOT NULL THEN 'Appointment'
                    ELSE 'Walk-in'
                END AS EntryType,
                
                -- 2. Logic cho "Ngày bắt đầu"
                COALESCE(a.AppointmentDate, sr.RequestDate) AS EntryDate,

                -- 3. Logic cho "Giai đoạn mới nhất"
                CASE
                    WHEN i.InvoiceID IS NOT NULL THEN 'Invoice'
                    WHEN wo.WorkOrderID IS NOT NULL THEN 'Work Order'
                    ELSE 'Service Request'
                END AS LatestStage,
                
                -- 4. Logic cho "Trạng thái mới nhất"
                COALESCE(i.PaymentStatus, wo.Status, sr.Status) AS LatestStatus

            FROM servicerequest sr
            LEFT JOIN appointment a ON sr.AppointmentID = a.AppointmentID
            LEFT JOIN workorder wo ON sr.RequestID = wo.RequestID
            LEFT JOIN invoice i ON wo.WorkOrderID = i.WorkOrderID
            ORDER BY sr.RequestDate DESC -- Hiển thị yêu cầu mới nhất lên đầu
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairJourneySummaryDTO summary = new RepairJourneySummaryDTO();
                    summary.setRequestID(rs.getInt("RequestID"));
                    summary.setVehicleID(rs.getInt("VehicleID"));
                    summary.setEntryType(rs.getString("EntryType"));
                    summary.setEntryDate(rs.getTimestamp("EntryDate"));
                    summary.setLatestStage(rs.getString("LatestStage"));
                    summary.setLatestStatus(rs.getString("LatestStatus"));
                    list.add(summary);
                }
            }
        }
        return list; // Trả về danh sách
    }

}
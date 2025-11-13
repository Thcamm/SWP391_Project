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
// Thêm phương thức MỚI này vào JourneyDAO
// Cập nhật trong RepairJourneyDAO.java

    public int countJourneySummariesByVehicle(int customerId, Integer vehicleId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(sr.RequestID) FROM servicerequest sr WHERE sr.CustomerID = ?");

        if (vehicleId != null) {
            sql.append(" AND sr.VehicleID = ?");
        }

        int count = 0;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, customerId);
            if (vehicleId != null) {
                ps.setInt(2, vehicleId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }

    // Overload để backward compatible
    public int countJourneySummaries(int customerId) throws SQLException {
        return countJourneySummariesByVehicle(customerId, null);
    }

    public List<RepairJourneySummaryDTO> getPaginatedJourneySummariesByVehicle(
            int customerId, Integer vehicleId, String sortBy, int limit, int offset) throws SQLException {

        List<RepairJourneySummaryDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT
            sr.RequestID,
            sr.VehicleID,
            v.LicensePlate,
            CASE
                WHEN a.AppointmentID IS NOT NULL THEN 'Appointment'
                ELSE 'Walk-in'
            END AS EntryType,
            COALESCE(a.AppointmentDate, sr.RequestDate) AS EntryDate,
            CASE
                WHEN i.InvoiceID IS NOT NULL THEN 'Invoice'
                WHEN wo.WorkOrderID IS NOT NULL THEN 'Work Order'
                ELSE 'Service Request'
            END AS LatestStage,
            COALESCE(i.PaymentStatus, wo.Status, sr.Status) AS LatestStatus
        FROM servicerequest sr
        LEFT JOIN appointment a ON sr.AppointmentID = a.AppointmentID
        LEFT JOIN workorder wo ON sr.RequestID = wo.RequestID
        LEFT JOIN invoice i ON wo.WorkOrderID = i.WorkOrderID
        LEFT JOIN vehicle v ON sr.VehicleID = v.VehicleID
        WHERE sr.CustomerID = ?
    """);

        // Add vehicle filter if provided
        if (vehicleId != null) {
            sql.append(" AND sr.VehicleID = ?");
        }

        // Add sort order
        sql.append(" ORDER BY ");
        switch (sortBy) {
            case "oldest":
                sql.append("sr.RequestDate ASC");
                break;
            case "status":
                sql.append("LatestStatus ASC, sr.RequestDate DESC");
                break;
            case "stage":
                sql.append("LatestStage DESC, sr.RequestDate DESC");
                break;
            case "newest":
            default:
                sql.append("sr.RequestDate DESC");
                break;
        }

        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, customerId);

            if (vehicleId != null) {
                ps.setInt(paramIndex++, vehicleId);
            }

            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairJourneySummaryDTO summary = new RepairJourneySummaryDTO();
                    summary.setRequestID(rs.getInt("RequestID"));
                    summary.setVehicleID(rs.getInt("VehicleID"));
                    summary.setVehicleLicensePlate(rs.getString("LicensePlate"));
                    summary.setEntryType(rs.getString("EntryType"));
                    summary.setEntryDate(rs.getTimestamp("EntryDate"));
                    summary.setLatestStage(rs.getString("LatestStage"));
                    summary.setLatestStatus(rs.getString("LatestStatus"));
                    list.add(summary);
                }
            }
        }
        return list;
    }

    // Overload để backward compatible
    public List<RepairJourneySummaryDTO> getPaginatedJourneySummaries(
            int customerId, int limit, int offset) throws SQLException {
        return getPaginatedJourneySummariesByVehicle(customerId, null, "newest", limit, offset);
    }
    public int countAllTracking() throws SQLException {
        // Câu lệnh SQL chỉ cần đếm trên bảng chính
        String sql = "SELECT COUNT(sr.RequestID) FROM servicerequest sr";
        int count = 0;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }
    public List<RepairJourneySummaryDTO> getPaginatedTracking(int limit, int offset) throws SQLException {
        List<RepairJourneySummaryDTO> list = new ArrayList<>();

        // Câu SQL giữ nguyên, chỉ thêm "LIMIT ? OFFSET ?" ở cuối
        String sql = """
        SELECT
            sr.RequestID,
            sr.VehicleID,
            u.FullName,
            v.LicensePlate,

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
        LEFT JOIN customer c ON sr.CustomerID = c.CustomerID
        LEFT JOIN user u ON c.UserID = u.UserID
        LEFT JOIN vehicle v ON sr.VehicleID = v.VehicleID
        ORDER BY sr.RequestDate DESC
        LIMIT ? OFFSET ?
    """; // Thêm LIMIT và OFFSET

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set 2 tham số mới cho LIMIT và OFFSET
            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairJourneySummaryDTO summary = new RepairJourneySummaryDTO();
                    summary.setRequestID(rs.getInt("RequestID"));
                    summary.setVehicleID(rs.getInt("VehicleID"));
                    summary.setFullName(rs.getString("FullName"));
                    summary.setVehicleLicensePlate(rs.getString("LicensePlate"));
                    summary.setEntryType(rs.getString("EntryType"));
                    summary.setEntryDate(rs.getTimestamp("EntryDate"));
                    summary.setLatestStage(rs.getString("LatestStage"));
                    summary.setLatestStatus(rs.getString("LatestStatus"));
                    list.add(summary);
                }
            }
        }
        return list; // Trả về danh sách đã phân trang
    }
    // Thêm các phương thức này vào JourneyDAO

    public int countFilteredTracking(String fullName, Integer vehicleId) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(DISTINCT sr.RequestID) FROM servicerequest sr " +
                        "LEFT JOIN appointment a ON sr.AppointmentID = a.AppointmentID " +
                        "LEFT JOIN customer c ON sr.CustomerID = c.CustomerID " +
                        "LEFT JOIN user u ON c.UserID = u.UserID " +
                        "LEFT JOIN vehicle v ON sr.VehicleID = v.VehicleID " +
                        "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (fullName != null && !fullName.trim().isEmpty()) {
            sql.append(" AND u.FullName LIKE ?");
            params.add("%" + fullName.trim() + "%");
        }

        if (vehicleId != null) {
            sql.append(" AND sr.VehicleID = ?");
            params.add(vehicleId);
        }

        int count = 0;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        }
        return count;
    }

    public List<RepairJourneySummaryDTO> getFilteredTracking(
            String fullName, Integer vehicleId, String sortBy, int limit, int offset) throws SQLException {

        List<RepairJourneySummaryDTO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT
            sr.RequestID,
            sr.VehicleID,
            u.FullName,
            v.LicensePlate,
            CASE
                WHEN a.AppointmentID IS NOT NULL THEN 'Appointment'
                ELSE 'Walk-in'
            END AS EntryType,
            COALESCE(a.AppointmentDate, sr.RequestDate) AS EntryDate,
            CASE
                WHEN i.InvoiceID IS NOT NULL THEN 'Invoice'
                WHEN wo.WorkOrderID IS NOT NULL THEN 'Work Order'
                ELSE 'Service Request'
            END AS LatestStage,
            COALESCE(i.PaymentStatus, wo.Status, sr.Status) AS LatestStatus
        FROM servicerequest sr
        LEFT JOIN appointment a ON sr.AppointmentID = a.AppointmentID
        LEFT JOIN workorder wo ON sr.RequestID = wo.RequestID
        LEFT JOIN invoice i ON wo.WorkOrderID = i.WorkOrderID
        LEFT JOIN customer c ON sr.CustomerID = c.CustomerID
        LEFT JOIN user u ON c.UserID = u.UserID
        LEFT JOIN vehicle v ON sr.VehicleID = v.VehicleID
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        // Áp dụng filter
        if (fullName != null && !fullName.trim().isEmpty()) {
            sql.append(" AND u.FullName LIKE ?");
            params.add("%" + fullName.trim() + "%");
        }

        if (vehicleId != null) {
            sql.append(" AND sr.VehicleID = ?");
            params.add(vehicleId);
        }

        // Áp dụng sort
        if ("oldest".equals(sortBy)) {
            sql.append(" ORDER BY sr.RequestDate ASC");
        } else {
            sql.append(" ORDER BY sr.RequestDate DESC");
        }

        // Áp dụng pagination
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RepairJourneySummaryDTO summary = new RepairJourneySummaryDTO();
                    summary.setRequestID(rs.getInt("RequestID"));
                    summary.setVehicleID(rs.getInt("VehicleID"));
                    summary.setFullName(rs.getString("FullName"));
                    summary.setVehicleLicensePlate(rs.getString("LicensePlate"));
                    summary.setEntryType(rs.getString("EntryType"));
                    summary.setEntryDate(rs.getTimestamp("EntryDate"));
                    summary.setLatestStage(rs.getString("LatestStage"));
                    summary.setLatestStatus(rs.getString("LatestStatus"));
                    list.add(summary);
                }
            }
        }
        return list;
    }
}
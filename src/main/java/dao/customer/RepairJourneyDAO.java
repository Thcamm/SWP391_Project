package dao.customer;

import common.DbContext; // Lớp DbContext của bạn
import model.dto.DiagnosticPartView;
import model.dto.RepairJourneySummaryDTO;
import model.dto.RepairJourneyView;
// Import các model của bạn
import model.appointment.Appointment;
import model.dto.WorkOrderDetailView;
import model.feedback.Feedback;
import model.invoice.Invoice;
import model.workorder.ServiceRequest;
import model.workorder.WorkOrder;

import java.math.BigDecimal;
import java.sql.*;
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

    public Integer getWorkOrderIDByRequestID(int requestID) throws SQLException {
        String sql = """
            SELECT WorkOrderID, Status, EstimateAmount, CreatedAt
            FROM WorkOrder 
            WHERE RequestID = ?
        """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("WorkOrderID");
            }
        }
        return null;
    }

    public List<WorkOrderDetailView> getWorkOrderDetails(int workOrderID) throws SQLException {
        String sql = """
            SELECT 
                wod.DetailID,
                wod.WorkOrderID,
                wod.source,
                wod.diagnostic_id,
                wod.approval_status,
                wod.TaskDescription,
                wod.EstimateAmount,
                wod.detail_status
            FROM WorkOrderDetail wod
            WHERE wod.WorkOrderID = ?
            ORDER BY wod.DetailID
        """;

        List<WorkOrderDetailView> details = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                WorkOrderDetailView detail = new WorkOrderDetailView();
                detail.setDetailID(rs.getInt("DetailID"));
                detail.setWorkOrderID(rs.getInt("WorkOrderID"));
                detail.setSource(rs.getString("source"));
                detail.setDiagnosticID(rs.getObject("diagnostic_id", Integer.class));
                detail.setApprovalStatus(rs.getString("approval_status"));
                detail.setTaskDescription(rs.getString("TaskDescription"));
                detail.setEstimateAmount(rs.getBigDecimal("EstimateAmount"));
                detail.setDetailStatus(rs.getString("detail_status"));
                details.add(detail);
            }
        }
        return details;
    }

    public void populateTaskAssignment(WorkOrderDetailView detail) throws SQLException {
        String sql = """
            SELECT 
                ta.AssignmentID,
                ta.AssignToTechID,
                u.FullName as TechnicianName,
                ta.AssignedDate,
                ta.StartAt,
                ta.CompleteAt,
                ta.Status,
                ta.task_type
            FROM TaskAssignment ta
            INNER JOIN Employee e ON ta.AssignToTechID = e.EmployeeID
            INNER JOIN User u ON e.UserID = u.UserID
            WHERE ta.DetailID = ?
            LIMIT 1
        """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detail.getDetailID());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                detail.setAssignmentID(rs.getInt("AssignmentID"));
                detail.setTechnicianID(rs.getInt("AssignToTechID"));
                detail.setTechnicianName(rs.getString("TechnicianName"));
                detail.setAssignedDate(rs.getTimestamp("AssignedDate"));
                detail.setStartAt(rs.getTimestamp("StartAt"));
                detail.setCompleteAt(rs.getTimestamp("CompleteAt"));
                detail.setTaskStatus(rs.getString("Status"));
            }
        }
    }


    public void populateVehicleDiagnostic(WorkOrderDetailView detail) throws SQLException {
        if (detail.getAssignmentID() == null) return;

        String sql = """
            SELECT 
                VehicleDiagnosticID,
                IssueFound,
                EstimateCost,
                Status,
                RejectReason
            FROM VehicleDiagnostic
            WHERE AssignmentID = ?
        """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detail.getAssignmentID());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                detail.setVehicleDiagnosticID(rs.getInt("VehicleDiagnosticID"));
                detail.setIssueFound(rs.getString("IssueFound"));
                detail.setDiagnosticEstimateCost(rs.getBigDecimal("EstimateCost"));
                detail.setDiagnosticStatus(rs.getString("Status"));
                detail.setRejectReason(rs.getString("RejectReason"));
            }
        }
    }

    public List<DiagnosticPartView> getDiagnosticParts(int vehicleDiagnosticID) throws SQLException {
        String sql = """
            SELECT 
                dp.DiagnosticPartID,
                dp.VehicleDiagnosticID,
                dp.PartDetailID,
                dp.QuantityNeeded,
                dp.UnitPrice,
                dp.PartCondition,
                dp.ReasonForReplacement,
                dp.IsApproved,
                p.PartCode,
                p.PartName,
                pd.Manufacturer,
                pd.Description,
                pd.SKU,
                pd.Quantity as AvailableStock
            FROM DiagnosticPart dp
            INNER JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID
            INNER JOIN Part p ON pd.PartID = p.PartID
            WHERE dp.VehicleDiagnosticID = ?
            ORDER BY 
                CASE dp.PartCondition
                    WHEN 'REQUIRED' THEN 1
                    WHEN 'RECOMMENDED' THEN 2
                    WHEN 'OPTIONAL' THEN 3
                END,
                dp.DiagnosticPartID
        """;

        List<DiagnosticPartView> parts = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicleDiagnosticID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DiagnosticPartView part = new DiagnosticPartView();
                part.setDiagnosticPartID(rs.getInt("DiagnosticPartID"));
                part.setVehicleDiagnosticID(rs.getInt("VehicleDiagnosticID"));
                part.setPartDetailID(rs.getInt("PartDetailID"));
                part.setQuantityNeeded(rs.getInt("QuantityNeeded"));
                part.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                part.setPartCondition(rs.getString("PartCondition"));
                part.setReasonForReplacement(rs.getString("ReasonForReplacement"));
                part.setIsApproved(rs.getInt("IsApproved"));
                part.setPartCode(rs.getString("PartCode"));
                part.setPartName(rs.getString("PartName"));
                part.setManufacturer(rs.getString("Manufacturer"));
                part.setDescription(rs.getString("Description"));
                part.setSku(rs.getString("SKU"));
                part.setAvailableStock(rs.getInt("AvailableStock"));
                parts.add(part);
            }
        }
        return parts;
    }

    public boolean updateMultipleDiagnosticParts(List<Integer> partIDs, boolean isApproved)
            throws SQLException {
        if (partIDs == null || partIDs.isEmpty()) return false;

        StringBuilder sql = new StringBuilder("UPDATE DiagnosticPart SET IsApproved = ? WHERE DiagnosticPartID IN (");
        for (int i = 0; i < partIDs.size(); i++) {
            sql.append("?");
            if (i < partIDs.size() - 1) sql.append(",");
        }
        sql.append(")");

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            ps.setInt(1, isApproved ? 1 : 0);
            for (int i = 0; i < partIDs.size(); i++) {
                ps.setInt(i + 2, partIDs.get(i));
            }

            return ps.executeUpdate() > 0;
        }
    }

    public boolean verifyPartOwnership(int diagnosticPartID, int customerID) throws SQLException {
        String sql = """
            SELECT 1
            FROM DiagnosticPart dp
            INNER JOIN VehicleDiagnostic vd ON dp.VehicleDiagnosticID = vd.VehicleDiagnosticID
            INNER JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID
            INNER JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID
            INNER JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID
            INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID
            WHERE dp.DiagnosticPartID = ? AND sr.CustomerID = ?
        """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, diagnosticPartID);
            ps.setInt(2, customerID);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        }
    }

    public boolean verifyDetailOwnership(int detailID, int customerID) throws SQLException {
        String sql = """
            SELECT 1
            FROM WorkOrderDetail wod
            INNER JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID
            INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID
            WHERE wod.DetailID = ? AND sr.CustomerID = ?
        """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, detailID);
            ps.setInt(2, customerID);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        }
    }

    public boolean verifyDiagnosticOwnership(int vehicleDiagnosticID, int customerID) throws SQLException {
        String sql = """
            SELECT 1
            FROM VehicleDiagnostic vd
            INNER JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID
            INNER JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID
            INNER JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID
            INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID
            WHERE vd.VehicleDiagnosticID = ? AND sr.CustomerID = ?
        """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicleDiagnosticID);
            ps.setInt(2, customerID);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        }
    }

    public boolean verifyRequestOwnership(int requestID, int customerID) throws SQLException {
        String sql = "SELECT 1 FROM ServiceRequest WHERE RequestID = ? AND CustomerID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestID);
            ps.setInt(2, customerID);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        }
    }

    // Transaction helpers
    public void beginTx() throws SQLException { getConnection().setAutoCommit(false); }
    public void commitTx() throws SQLException { getConnection().commit(); getConnection().setAutoCommit(true); }
    public void rollbackTx() {
        try { getConnection().rollback(); getConnection().setAutoCommit(true); } catch (SQLException ignored) {}
    }

    // Lấy WorkOrderID từ VehicleDiagnostic
    public Integer getWorkOrderIdByDiagnostic(int vehicleDiagnosticId) throws SQLException {
        String sql = """
        SELECT wo.WorkOrderID
        FROM VehicleDiagnostic vd
        JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID
        JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID
        JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID
        WHERE vd.VehicleDiagnosticID = ?
    """;
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleDiagnosticId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : null; }
        }
    }

    // Accept VehicleDiagnostic
    public void acceptVehicleDiagnostic(int vehicleDiagnosticId) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET Status = 'APPROVED' WHERE VehicleDiagnosticID = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleDiagnosticId);
            ps.executeUpdate();
        }
    }

    // Tạo WorkOrderDetail mới từ diagnostic
    public int insertWorkOrderDetail(int workOrderId, String source, Integer diagnosticId,
                                     String approvalStatus, String taskDescription,
                                     BigDecimal estimateAmount, String detailStatus) throws SQLException {
        String sql = """
        INSERT INTO WorkOrderDetail (WorkOrderID, source, diagnostic_id, approval_status, TaskDescription, EstimateAmount, detail_status)
        VALUES (?,?,?,?,?,?,?)
    """;
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, workOrderId);
            ps.setString(2, source);
            if (diagnosticId == null) ps.setNull(3, Types.INTEGER); else ps.setInt(3, diagnosticId);
            ps.setString(4, approvalStatus);
            ps.setString(5, taskDescription);
            ps.setBigDecimal(6, estimateAmount);
            ps.setString(7, detailStatus);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    // Tạo WorkOrderPart từ part đã duyệt
    public void insertWorkOrderPart(int detailId, int partDetailId, int qty,
                                    BigDecimal unitPrice, BigDecimal totalPrice, String source) throws SQLException {
        String sql = """
        INSERT INTO WorkOrderPart (DetailID, PartDetailID, Quantity, UnitPrice, TotalPrice, Source)
        VALUES (?,?,?,?,?,?)
    """;
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            ps.setInt(2, partDetailId);
            ps.setInt(3, qty);
            ps.setBigDecimal(4, unitPrice);
            ps.setBigDecimal(5, totalPrice);
            ps.setString(6, source);
            ps.executeUpdate();
        }
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
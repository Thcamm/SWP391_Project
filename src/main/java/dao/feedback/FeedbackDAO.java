package dao.feedback;

import common.DbContext;
import model.feedback.Feedback;
import model.invoice.Invoice;
import model.workorder.WorkOrder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedbackDAO extends DbContext {

    // Insert feedback và trả về feedbackID vừa tạo
    public int insertFeedbackReturnId(Feedback fb) throws SQLException {
        String sql = "INSERT INTO Feedback(WorkOrderID, CustomerID, Rating, FeedbackText, IsAnonymous, FeedbackDate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, fb.getWorkOrderID());
            ps.setInt(2, fb.getCustomerID());
            if (fb.getRating() != null) {
                ps.setInt(3, fb.getRating());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, fb.getFeedbackText());
            ps.setBoolean(5, fb.isAnonymous());
            ps.setTimestamp(6, Timestamp.valueOf(fb.getFeedbackDate()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) return -1;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        }
    }

    // Lấy feedback theo ID
    public Feedback getFeedbackById(int feedbackID) throws SQLException {
        String sql = "SELECT * FROM Feedback WHERE FeedbackID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, feedbackID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Feedback fb = new Feedback();
                    fb.setFeedbackID(rs.getInt("FeedbackID"));
                    fb.setWorkOrderID(rs.getInt("WorkOrderID"));
                    fb.setCustomerID(rs.getInt("CustomerID"));
                    fb.setRating(rs.getObject("Rating") != null ? rs.getInt("Rating") : null);
                    fb.setFeedbackText(rs.getString("FeedbackText"));
                    fb.setAnonymous(rs.getBoolean("IsAnonymous"));
                    fb.setFeedbackDate(rs.getTimestamp("FeedbackDate").toLocalDateTime());
                    fb.setReplyText(rs.getString("ReplyText"));
                    Timestamp replyTs = rs.getTimestamp("ReplyDate");
                    fb.setReplyDate(replyTs != null ? replyTs.toLocalDateTime() : null);
                    fb.setReplyBy(rs.getObject("RepliedBy") != null ? rs.getInt("RepliedBy") : null);
                    return fb;
                }
            }
        }
        return null;
    }

    // Lấy tất cả feedback của một khách hàng
    public List<Feedback> getFeedbacksByCustomerId(int customerId) throws SQLException {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE CustomerID = ? ORDER BY FeedbackDate DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback fb = new Feedback();
                    fb.setFeedbackID(rs.getInt("FeedbackID"));
                    fb.setWorkOrderID(rs.getInt("WorkOrderID"));
                    fb.setCustomerID(rs.getInt("CustomerID"));
                    fb.setRating(rs.getObject("Rating") != null ? rs.getInt("Rating") : null);
                    fb.setFeedbackText(rs.getString("FeedbackText"));
                    fb.setAnonymous(rs.getBoolean("IsAnonymous"));
                    fb.setFeedbackDate(rs.getTimestamp("FeedbackDate").toLocalDateTime());
                    fb.setReplyText(rs.getString("ReplyText"));
                    Timestamp replyTs = rs.getTimestamp("ReplyDate");
                    fb.setReplyDate(replyTs != null ? replyTs.toLocalDateTime() : null);
                    fb.setReplyBy(rs.getObject("RepliedBy") != null ? rs.getInt("RepliedBy") : null);
                    list.add(fb);
                }
            }
        }
        return list;
    }

    // Lấy tất cả feedback (dành cho admin hoặc view-feedback-list)
    public List<Feedback> getAllFeedbacks() throws SQLException {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM Feedback ORDER BY FeedbackDate DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Feedback fb = new Feedback();
                fb.setFeedbackID(rs.getInt("FeedbackID"));
                fb.setWorkOrderID(rs.getInt("WorkOrderID"));
                fb.setCustomerID(rs.getInt("CustomerID"));
                fb.setRating(rs.getObject("Rating") != null ? rs.getInt("Rating") : null);
                fb.setFeedbackText(rs.getString("FeedbackText"));
                fb.setAnonymous(rs.getBoolean("IsAnonymous"));
                fb.setFeedbackDate(rs.getTimestamp("FeedbackDate").toLocalDateTime());
                fb.setReplyText(rs.getString("ReplyText"));
                Timestamp replyTs = rs.getTimestamp("ReplyDate");
                fb.setReplyDate(replyTs != null ? replyTs.toLocalDateTime() : null);
                fb.setReplyBy(rs.getObject("RepliedBy") != null ? rs.getInt("RepliedBy") : null);
                list.add(fb);
            }
        }
        return list;
    }
    public int countPaidWorkOrdersByCustomer(int customerId) throws SQLException {
        String sql =
                "SELECT COUNT(*) " +
                        "FROM WorkOrder wo " +
                        "INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "INNER JOIN Invoice i ON wo.WorkOrderID = i.WorkOrderID " +
                        "WHERE sr.CustomerID = ? AND i.PaymentStatus = 'PAID'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    public boolean replyFeedback(int feedbackID, String replyText, int repliedBy) throws SQLException {
        String sql = "UPDATE feedback SET ReplyText = ?, ReplyDate = NOW(), RepliedBy = ?, Status = 'REPLIED' WHERE FeedbackID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, replyText);
            ps.setInt(2, repliedBy);
            ps.setInt(3, feedbackID);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Map<String, Object>> getPaidWorkOrdersWithFeedback(int customerId, int limit, int offset) throws SQLException {
        String sql = """
            SELECT 
                wo.WorkOrderID, wo.TechManagerID, wo.RequestID,
                wo.EstimateAmount, wo.Status AS WOStatus, wo.CreatedAt AS WOCreatedAt,

                i.InvoiceID, i.InvoiceNumber, i.InvoiceDate, i.PaymentStatus,
                i.CreatedAt AS InvCreatedAt, i.UpdatedAt AS InvUpdatedAt,

                f.FeedbackID, f.Rating, f.FeedbackText, f.FeedbackDate,
                f.ReplyText, f.ReplyDate, f.Status AS FeedbackStatus

            FROM WorkOrder wo
            INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID
            INNER JOIN Invoice i ON wo.WorkOrderID = i.WorkOrderID
            LEFT JOIN Feedback f ON wo.WorkOrderID = f.WorkOrderID
            WHERE sr.CustomerID = ? AND i.PaymentStatus = 'PAID'
            ORDER BY i.UpdatedAt DESC
            LIMIT ? OFFSET ?
        """;

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("workOrder", extractWorkOrder(rs));
                    record.put("invoice", extractInvoice(rs));
                    record.put("feedback", extractFeedback(rs));
                    list.add(record);
                }
            }
        }

        return list;
    }

    // ================= HELPER METHODS =================

    private WorkOrder extractWorkOrder(ResultSet rs) throws SQLException {
        WorkOrder wo = new WorkOrder();
        wo.setWorkOrderId(rs.getInt("WorkOrderID"));
        wo.setTechManagerId(rs.getInt("TechManagerID"));
        wo.setRequestId(rs.getInt("RequestID"));
        wo.setEstimateAmount(rs.getBigDecimal("EstimateAmount"));

        // Chuyển string status -> enum WorkOrder.Status
        String statusStr = rs.getString("WOStatus");
        if (statusStr != null) {
            try {
                wo.setStatus(WorkOrder.Status.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                wo.setStatus(WorkOrder.Status.PENDING);
            }
        }

        wo.setCreatedAt(rs.getTimestamp("WOCreatedAt"));
        return wo;
    }

    private Invoice extractInvoice(ResultSet rs) throws SQLException {
        int id = rs.getInt("InvoiceID");
        if (rs.wasNull()) return null;

        Invoice inv = new Invoice();
        inv.setInvoiceID(id);
        inv.setInvoiceNumber(rs.getString("InvoiceNumber"));
        inv.setInvoiceDate(rs.getDate("InvoiceDate"));
        inv.setPaymentStatus(rs.getString("PaymentStatus"));
        inv.setCreatedAt(rs.getTimestamp("InvCreatedAt"));
        inv.setUpdatedAt(rs.getTimestamp("InvUpdatedAt"));
        return inv;
    }

    private Feedback extractFeedback(ResultSet rs) throws SQLException {
        int id = rs.getInt("FeedbackID");
        if (rs.wasNull()) return null;

        Feedback fb = new Feedback();
        fb.setFeedbackID(id);
        fb.setRating(rs.getInt("Rating"));
        fb.setFeedbackText(rs.getString("FeedbackText"));
        fb.setFeedbackDate(toLocalDateTime(rs.getTimestamp("FeedbackDate")));
        fb.setReplyText(rs.getString("ReplyText"));
        fb.setReplyDate(toLocalDateTime(rs.getTimestamp("ReplyDate")));
        fb.setStatus(rs.getString("FeedbackStatus"));
        return fb;
    }

    private java.time.LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts != null ? ts.toLocalDateTime() : null;
    }
}

package dao.feedback;

import common.DbContext;
import model.feedback.Feedback;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}

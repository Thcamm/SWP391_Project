package dao.feedback;

import common.DbContext;
import model.feedback.Feedback;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO extends DbContext {
    public int insertFeedbackReturnId(Feedback fb) throws SQLException {
        String sql = "INSERT INTO Feedback(workOrderID, customerID, rating, feedbackText, anonymous, createdAt) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, fb.getWorkOrderID());
            ps.setInt(2, fb.getCustomerID());
            ps.setObject(3, fb.getRating(), java.sql.Types.INTEGER); // rating có thể null
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
        String sql = "SELECT * FROM Feedback WHERE feedbackID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, feedbackID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Feedback fb = new Feedback();
                    fb.setFeedbackID(rs.getInt("feedbackID"));
                    fb.setWorkOrderID(rs.getInt("workOrderID"));
                    fb.setCustomerID(rs.getInt("customerID"));
                    fb.setRating(rs.getInt("rating"));
                    fb.setFeedbackText(rs.getString("feedbackText"));
                    fb.setAnonymous(rs.getBoolean("anonymous"));
                    fb.setFeedbackDate(rs.getTimestamp("createdAt").toLocalDateTime());
                    return fb;
                }
            }
        }
        return null;
    }

    // Lấy tất cả feedback của 1 user
    public List<Feedback> getFeedbacksByCustomerId(int customerId) throws SQLException {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE customerID = ? ORDER BY createdAt DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback fb = new Feedback();
                    fb.setFeedbackID(rs.getInt("feedbackID"));
                    fb.setWorkOrderID(rs.getInt("workOrderID"));
                    fb.setCustomerID(rs.getInt("customerID"));
                    fb.setRating(rs.getInt("rating"));
                    fb.setFeedbackText(rs.getString("feedbackText"));
                    fb.setAnonymous(rs.getBoolean("anonymous"));
                    fb.setFeedbackDate(rs.getTimestamp("createdAt").toLocalDateTime());
                    list.add(fb);
                }
            }
        }
        return list;
    }

    // Lấy toàn bộ feedback (dành cho admin hoặc view-feedback-list)
    public List<Feedback> getAllFeedbacks() throws SQLException {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM Feedback ORDER BY createdAt DESC";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Feedback fb = new Feedback();
                fb.setFeedbackID(rs.getInt("feedbackID"));
                fb.setWorkOrderID(rs.getInt("workOrderID"));
                fb.setCustomerID(rs.getInt("customerID"));
                fb.setRating(rs.getInt("rating"));
                fb.setFeedbackText(rs.getString("feedbackText"));
                fb.setAnonymous(rs.getBoolean("anonymous"));
                fb.setFeedbackDate(rs.getTimestamp("createdAt").toLocalDateTime());
                list.add(fb);
            }
        }
        return list;
    }
}

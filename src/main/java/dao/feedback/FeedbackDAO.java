package dao.feedback;

import common.DbContext;
import model.feedback.Feedback;

import java.sql.*;
import java.time.LocalDateTime;

public class FeedbackDAO extends DbContext {
    public boolean insertFeedback(Feedback fb) {
        String sql = """
                INSERT INTO Feedback (CustomerID, WorkOrderID, Rating, FeedbackText, IsAnonymous, FeedbackDate)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (fb.getCustomerID() != null)
                ps.setInt(1, fb.getCustomerID());
            else
                ps.setNull(1, Types.INTEGER);

            if (fb.getWorkOrderID() != null)
                ps.setInt(2, fb.getWorkOrderID());
            else
                ps.setNull(2, Types.INTEGER);

            if (fb.getRating() != null)
                ps.setInt(3, fb.getRating());
            else
                ps.setNull(3, Types.INTEGER);

            ps.setString(4, fb.getFeedbackText());

            ps.setBoolean(5, fb.isAnonymous());

            ps.setTimestamp(6, Timestamp.valueOf(
                    fb.getFeedbackDate() != null ? fb.getFeedbackDate() : LocalDateTime.now()
            ));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(" Error inserting feedback: " + e.getMessage(), e);
        }
    }


    public boolean replyFeedback(int feedbackID, String replyText, int repliedBy) {
        String sql = """
                UPDATE Feedback
                SET ReplyText = ?, ReplyDate = ?, RepliedBy = ?
                WHERE FeedbackID = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, replyText);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, repliedBy);
            ps.setInt(4, feedbackID);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error replying to feedback: " + e.getMessage(), e);
        }
    }
}

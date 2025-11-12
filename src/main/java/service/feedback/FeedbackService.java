package service.feedback;

import dao.feedback.FeedbackDAO;
import model.feedback.Feedback;
import model.invoice.Invoice;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class FeedbackService {
    private final FeedbackDAO feedbackDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
    }

    /**
     * Lấy danh sách WorkOrder có invoice PAID và kiểm tra logic feedback.
     * Nếu chưa có feedback → kiểm tra invoice.createdAt <= 7 ngày mới cho phép gửi.
     */
    public List<Map<String, Object>> getCustomerFeedbackView(int customerId, int limit, int offset) throws SQLException {
        List<Map<String, Object>> list = feedbackDAO.getPaidWorkOrdersWithFeedback(customerId, limit, offset);

        for (Map<String, Object> item : list) {
            Invoice inv = (Invoice) item.get("invoice");
            Feedback fb = (Feedback) item.get("feedback");

            // Mặc định
            String feedbackAction = "NONE"; // NONE, ALLOW_FEEDBACK, EXPIRED, HAS_FEEDBACK

            if (fb != null) {
                feedbackAction = "HAS_FEEDBACK";
            } else if (inv != null && inv.getCreatedAt() != null) {
                LocalDateTime createdAt = inv.getCreatedAt().toLocalDateTime();
                LocalDateTime now = LocalDateTime.now();

                long days = Duration.between(createdAt, now).toDays();

                if (days <= 7) {
                    feedbackAction = "ALLOW_FEEDBACK";
                } else {
                    feedbackAction = "EXPIRED";
                }
            }

            item.put("feedbackAction", feedbackAction);
        }

        return list;
    }
}

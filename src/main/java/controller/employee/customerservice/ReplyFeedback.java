package controller.employee.customerservice;

import dao.feedback.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.user.User;

import java.io.IOException;
@MultipartConfig
@WebServlet("/api/feedback-reply")
public class ReplyFeedback extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            // Lấy user từ session (trùng key với login servlet)
            User currentUser = (User) req.getSession().getAttribute("user");
            if (currentUser == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                resp.getWriter().write("{\"success\":false,\"message\":\"User not logged in\"}");
                return;
            }

            // Lấy thông tin từ request
            String feedbackIdParam = req.getParameter("feedbackID");
            String replyText = req.getParameter("replyText");

            if (feedbackIdParam == null || replyText == null || replyText.trim().isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                resp.getWriter().write("{\"success\":false,\"message\":\"Invalid parameters\"}");
                return;
            }

            int feedbackID;
            try {
                feedbackID = Integer.parseInt(feedbackIdParam);
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false,\"message\":\"feedbackID must be an integer\"}");
                return;
            }

            int repliedBy = currentUser.getUserId();
            System.out.println("[DAO] Replying feedbackID=" + feedbackID + ", text=" + replyText + ", byUser=" + repliedBy);

            // Gọi DAO để reply feedback
            boolean success = feedbackDAO.replyFeedback(feedbackID, replyText, repliedBy);

            if (success) {
                resp.getWriter().write("{\"success\":true}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"success\":false,\"message\":\"Cannot update feedback\"}");
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            resp.getWriter().write("{\"success\":false,\"message\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}

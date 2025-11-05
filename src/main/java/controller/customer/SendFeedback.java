package controller.customer;

import dao.customer.CustomerDAO;
import dao.feedback.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.feedback.Feedback;
import model.user.User;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/customer/send-feedback")
public class SendFeedback extends HttpServlet {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị form gửi feedback
        request.getRequestDispatcher("/view/customer/feedback-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());
            if (customerID == -1) {
                throw new Exception("Không tìm thấy khách hàng tương ứng với tài khoản này.");
            }

            int workOrderID = Integer.parseInt(request.getParameter("workOrderID"));
            String feedbackText = request.getParameter("feedbackText");
            String ratingStr = request.getParameter("rating");

            Integer rating = null;
            if (ratingStr != null && !ratingStr.isEmpty()) {
                rating = Integer.parseInt(ratingStr);
            }

            boolean isAnonymous = request.getParameter("isAnonymous") != null; // ✅ true nếu tick, false nếu không

            Feedback fb = new Feedback();
            fb.setCustomerID(customerID);
            fb.setWorkOrderID(workOrderID);
            fb.setFeedbackText(feedbackText);
            fb.setRating(rating);
            fb.setAnonymous(isAnonymous);
            fb.setFeedbackDate(LocalDateTime.now());

            boolean success = feedbackDAO.insertFeedback(fb);

            if (success) {
                request.setAttribute("message", "Cảm ơn bạn đã gửi đánh giá!");
                request.setAttribute("messageType", "success");
            } else {
                request.setAttribute("message", "❌ Lỗi khi gửi đánh giá. Vui lòng thử lại.");
                request.setAttribute("messageType", "error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "❌ Lỗi: " + e.getMessage());
            request.setAttribute("messageType", "error");
        }

        request.getRequestDispatcher("/view/customer/view-feedback.jsp").forward(request, response);
    }
}

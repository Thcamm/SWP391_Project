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
import java.util.List;

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
            // Lấy user hiện tại
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Lấy customerID
            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());
            if (customerID == -1) throw new Exception("Không tìm thấy khách hàng.");

            // Lấy dữ liệu từ form
            int workOrderID = Integer.parseInt(request.getParameter("workOrderID"));
            String feedbackText = request.getParameter("feedbackText");
            String ratingStr = request.getParameter("rating");
            Integer rating = (ratingStr != null && !ratingStr.isEmpty()) ? Integer.parseInt(ratingStr) : null;
            boolean isAnonymous = request.getParameter("isAnonymous") != null;

            // Tạo Feedback
            Feedback fb = new Feedback();
            fb.setCustomerID(customerID);
            fb.setWorkOrderID(workOrderID);
            fb.setFeedbackText(feedbackText);
            fb.setRating(rating);
            fb.setAnonymous(isAnonymous);
            fb.setFeedbackDate(LocalDateTime.now());

            // Lưu và lấy feedbackID vừa tạo
            int newFeedbackID = feedbackDAO.insertFeedbackReturnId(fb);
            if (newFeedbackID == -1) throw new Exception("Lỗi khi lưu feedback.");
            response.sendRedirect(request.getContextPath() + "/customer/view-feedback?feedbackId=" + newFeedbackID);
            return;

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi thì quay về form feedback
            request.setAttribute("message", "Lỗi: " + e.getMessage());
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/view/customer/feedback-form.jsp").forward(request, response);
        }
    }
}

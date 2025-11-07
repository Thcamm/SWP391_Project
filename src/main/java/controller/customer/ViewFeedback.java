package controller.customer;

import dao.customer.CustomerDAO;
import dao.feedback.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.feedback.Feedback;

import java.io.IOException;

@WebServlet("/customer/view-feedback")
public class ViewFeedback extends HttpServlet {
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị form gửi feedback
        String feedbackIdParam = request.getParameter("feedbackId");

        if (feedbackIdParam == null || feedbackIdParam.isEmpty()) {
            // Nếu không có feedbackId thì quay về danh sách feedback
            response.sendRedirect(request.getContextPath() + "/customer/view-feedback-list");
            return;
        }

        try {
            int feedbackId = Integer.parseInt(feedbackIdParam);

            // Gọi DAO để lấy thông tin feedback
            FeedbackDAO feedbackDAO = new FeedbackDAO();
            Feedback feedback = feedbackDAO.getFeedbackById(feedbackId);
            String customerName = customerDAO.getCustomerById(feedback.getCustomerID()).getUserName();

            if (feedback == null) {
                // Nếu không tìm thấy feedback => chuyển hướng về danh sách
                request.setAttribute("errorMessage", "Không tìm thấy Feedback với ID: " + feedbackId);
                request.getRequestDispatcher("/view/customer/view-workorder-list.jsp").forward(request, response);
                return;
            }

            // Gửi dữ liệu sang JSP hiển thị

            request.setAttribute("feedback", feedback);
            request.setAttribute("customerName", customerName); // nếu có thuộc tính này
            request.getRequestDispatcher("/view/customer/view-feedback.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            // Nếu feedbackId không phải số
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid feedbackId parameter");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi tải thông tin feedback");
        }
    }
    }


package controller.user;


import dao.feedback.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.feedback.Feedback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/view-feedback")
public class ViewFeedbackList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String workOrderId = request.getParameter("workOrderID");

        if (workOrderId == null || workOrderId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing workOrderID");
            return;
        }

        FeedbackDAO feedbackDAO = new FeedbackDAO();
        try {
            List<Feedback> feedbacks = feedbackDAO.getFeedbacksByWorkOrderId(Integer.parseInt(workOrderId));
            request.setAttribute("feedbacks", feedbacks);
            request.setAttribute("workOrderId", workOrderId);

            request.getRequestDispatcher("/view/customer/view-feedback-list.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error while loading feedback");
        }
    }
}

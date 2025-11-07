package controller.customer;

import common.utils.PaginationUtils;
import dao.customer.CustomerDAO;
import dao.feedback.FeedbackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import service.feedback.FeedbackService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/customer/workorder-list")
public class WorkOrderList extends HttpServlet {

    private static final int ITEMS_PER_PAGE = 10;
    private final FeedbackService feedbackService = new FeedbackService();
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // ✅ Lấy customerID từ UserID
        int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());

        // ✅ Lấy page hiện tại
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException ignored) {}
        }

        try {
            // ✅ Tổng số workorder có invoice PAID
            int totalItems = feedbackDAO.countPaidWorkOrdersByCustomer(customerID);

            // ✅ Tính toán phân trang
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, ITEMS_PER_PAGE);

            // ✅ Lấy danh sách workorder + invoice + feedback
            List<Map<String, Object>> feedbackViewList =
                    feedbackService.getCustomerFeedbackView(customerID, ITEMS_PER_PAGE, calc.getOffset());

            // ✅ Gói dữ liệu vào PaginationResult để tiện dùng ở JSP
            PaginationUtils.PaginationResult<Map<String, Object>> result =
                    new PaginationUtils.PaginationResult<>(
                            feedbackViewList,
                            totalItems,
                            calc.getTotalPages(),
                            calc.getSafePage(),
                            ITEMS_PER_PAGE
                    );

            // ✅ Gửi sang JSP
            request.setAttribute("feedbackViewList", result);
            request.setAttribute("currentPage", result.getCurrentPage());
            request.setAttribute("totalPages", result.getTotalPages());
            request.setAttribute("totalItems", totalItems);

            request.getRequestDispatcher("/view/customer/view-workorder-list.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Lỗi truy vấn dữ liệu: " + e.getMessage());
        }
    }
}

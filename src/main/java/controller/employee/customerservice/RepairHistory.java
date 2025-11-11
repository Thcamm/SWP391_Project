package controller.employee.customerservice;

import dao.customer.CustomerDAO;
import model.dto.RepairJourneySummaryDTO; // DTO tóm tắt
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.user.User;
import service.tracking.RepairTrackerService;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/customerservice/repair-history")
public class RepairHistory extends HttpServlet {

    private final RepairTrackerService repairListService = new RepairTrackerService();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // 1. Lấy CustomerID
            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());

            // 2. Gọi Service để lấy danh sách tóm tắt
            List<RepairJourneySummaryDTO> journeyList =
                    repairListService.getSummariesForCustomer(customerID);

            // 3. Gửi danh sách này sang JSP
            request.setAttribute("journeyList", journeyList);
            request.getRequestDispatcher("/view/customer/view-repair-list.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Lỗi truy vấn danh sách sửa chữa: " + e.getMessage(), e);
        }
    }
}
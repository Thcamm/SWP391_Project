package controller.customer;

import common.utils.PaginationUtils;
import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.RepairJourneySummaryDTO;
import model.user.User;
import service.tracking.RepairTrackerService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/customer/repair-list")
public class CustomerRepairListServlet extends HttpServlet {

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

        int currentPage = 1;
        int itemsPerPage = 10;

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Math.max(1, Integer.parseInt(pageParam));
            } catch (NumberFormatException ignored) {}
        }

        try {
            // 1️⃣ Lấy CustomerID
            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());

            // 2️⃣ Đếm tổng số bản ghi
            int totalItems = repairListService.countSummariesForCustomer(customerID);

            // 3️⃣ Tính toán phân trang
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            int offset = calc.getOffset();
            int safePage = calc.getSafePage();

            // 4️⃣ Lấy danh sách phân trang (limit, offset)
            List<RepairJourneySummaryDTO> repairJourneySummaryDTOList =
                    repairListService.getPaginatedSummariesForCustomer(customerID, itemsPerPage, offset);

            // 5️⃣ Đóng gói PaginationResult
            PaginationUtils.PaginationResult<RepairJourneySummaryDTO> result =
                    new PaginationUtils.PaginationResult<>(
                            repairJourneySummaryDTOList,
                            totalItems,
                            calc.getTotalPages(),
                            safePage,
                            itemsPerPage
                    );

            // 6️⃣ Gửi sang JSP
            request.setAttribute("journeyList", result);
            request.getRequestDispatcher("/view/customer/view-repair-list.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Lỗi truy vấn danh sách sửa chữa: " + e.getMessage(), e);
        }
    }
}

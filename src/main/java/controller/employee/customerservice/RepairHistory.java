package controller.employee.customerservice;

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

        // ---- 1️⃣ Xử lý phân trang ----
        int currentPage = 1;
        int itemsPerPage = 10;

        String pageParam = request.getParameter("journeyPage"); // dùng đúng paramName trong JSP
        if (pageParam != null) {
            try {
                currentPage = Math.max(1, Integer.parseInt(pageParam));
            } catch (NumberFormatException ignored) {}
        }

        try {
            // ---- 2️⃣ Lấy customerID từ user ----
            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());

            // ---- 3️⃣ Lấy tổng số bản ghi ----
            int totalItems = repairListService.countSummariesForCustomer(customerID);

            // ---- 4️⃣ Tính toán phân trang ----
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            int offset = calc.getOffset();
            int safePage = calc.getSafePage();

            // ---- 5️⃣ Lấy danh sách phân trang ----
            List<RepairJourneySummaryDTO> repairJourneySummaryDTOList =
                    repairListService.getPaginatedSummariesForCustomer(customerID, itemsPerPage, offset);

            // ---- 6️⃣ Gói kết quả ----
            PaginationUtils.PaginationResult<RepairJourneySummaryDTO> result =
                    new PaginationUtils.PaginationResult<>(
                            repairJourneySummaryDTOList,
                            totalItems,
                            calc.getTotalPages(),
                            safePage,
                            itemsPerPage
                    );

            // ---- 7️⃣ Truyền dữ liệu sang JSP ----
            request.setAttribute("journeyList", result);
            request.setAttribute("journeyCurrentPage", safePage);
            request.setAttribute("journeyTotalPages", calc.getTotalPages());
            request.setAttribute("customerID", customerID);

            request.getRequestDispatcher("/view/customerservice/repair-history.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Lỗi truy vấn danh sách sửa chữa: " + e.getMessage(), e);
        }
    }
}

package controller.employee.customerservice;

import common.utils.PaginationUtils;
import dao.customer.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.RepairJourneySummaryDTO;
import service.tracking.RepairTrackerService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/customerservice/view-all-repairs")
public class ViewAllRepair extends HttpServlet {

    private final RepairTrackerService repairListService = new RepairTrackerService();
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int currentPage = 1;
        int itemsPerPage = 10;

        // Lấy số trang hiện tại
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Math.max(1, Integer.parseInt(pageParam));
            } catch (NumberFormatException ignored) {}
        }

        try {
            int totalItems = repairListService.countAllTracker();

            // Tính toán phân trang
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            int offset = calc.getOffset();
            int safePage = calc.getSafePage();

            // Lấy danh sách dữ liệu theo phân trang
            List<RepairJourneySummaryDTO> repairJourneySummaryDTOList =
                    repairListService.getAllTracker(itemsPerPage, offset);

            // Đóng gói kết quả phân trang
            PaginationUtils.PaginationResult<RepairJourneySummaryDTO> result =
                    new PaginationUtils.PaginationResult<>(
                            repairJourneySummaryDTOList,
                            totalItems,
                            calc.getTotalPages(),
                            safePage,
                            itemsPerPage
                    );

            // Gửi sang JSP
            request.setAttribute("journeyList", result);
            request.getRequestDispatcher("/view/customerservice/view-all-repair.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Lỗi truy vấn danh sách sửa chữa: " + e.getMessage(), e);
        }
    }
}

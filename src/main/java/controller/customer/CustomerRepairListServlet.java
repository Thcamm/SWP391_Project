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

        // Pagination parameters
        int currentPage = 1;
        int itemsPerPage = 10;

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Math.max(1, Integer.parseInt(pageParam));
            } catch (NumberFormatException ignored) {}
        }

        // Filter parameters
        String vehicleIdParam = request.getParameter("vehicleId");
        String sortBy = request.getParameter("sortBy");

        Integer vehicleId = null;
        if (vehicleIdParam != null && !vehicleIdParam.trim().isEmpty()) {
            try {
                vehicleId = Integer.parseInt(vehicleIdParam);
            } catch (NumberFormatException ignored) {}
        }

        // Default sort order
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "newest"; // Default: newest first
        }

        try {
            // Get CustomerID
            int customerID = customerDAO.getCustomerIdByUserId(user.getUserId());

            // Count total items (with filter if applied)
            int totalItems;
            if (vehicleId != null) {
                totalItems = repairListService.countSummariesForCustomerByVehicle(customerID, vehicleId);
            } else {
                totalItems = repairListService.countSummariesForCustomer(customerID);
            }

            // Calculate pagination
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            int offset = calc.getOffset();
            int safePage = calc.getSafePage();

            // Get paginated list (with filter and sort)
            List<RepairJourneySummaryDTO> repairJourneySummaryDTOList;
            if (vehicleId != null) {
                repairJourneySummaryDTOList = repairListService.getPaginatedSummariesForCustomerByVehicle(
                        customerID, vehicleId, sortBy, itemsPerPage, offset);
            } else {
                repairJourneySummaryDTOList = repairListService.getPaginatedSummariesForCustomer(
                        customerID, itemsPerPage, offset);
            }

            // Wrap in PaginationResult
            PaginationUtils.PaginationResult<RepairJourneySummaryDTO> result =
                    new PaginationUtils.PaginationResult<>(
                            repairJourneySummaryDTOList,
                            totalItems,
                            calc.getTotalPages(),
                            safePage,
                            itemsPerPage
                    );

            // Set attributes for JSP
            request.setAttribute("journeyList", result);
            request.setAttribute("selectedVehicleId", vehicleId);
            request.setAttribute("selectedSortBy", sortBy);

            request.getRequestDispatcher("/view/customer/view-repair-list.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error querying repair list: " + e.getMessage(), e);
        }
    }
}
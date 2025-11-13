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

        // Get parameters
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                currentPage = Math.max(1, Integer.parseInt(pageParam));
            } catch (NumberFormatException ignored) {}
        }

        String fullName = request.getParameter("fullName");
        String vehicleIdParam = request.getParameter("vehicleId");
        String sortBy = request.getParameter("sortBy");

        // Clean up empty parameters
        if (fullName != null && fullName.trim().isEmpty()) {
            fullName = null;
        }

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "newest"; // default
        }
        Integer vehicleId = null;

        if (vehicleIdParam != null && !vehicleIdParam.trim().isEmpty()) {
            try {
                vehicleId = Integer.parseInt(vehicleIdParam);
            } catch (NumberFormatException e) {
                // Invalid vehicleId passed, you can log or ignore
                vehicleId = null;
            }
        }

        try {
            // Count total items with filters
            int totalItems = repairListService.countFilteredTracker(fullName, vehicleId);

            // Calculate pagination
            PaginationUtils.PaginationCalculation calc =
                    PaginationUtils.calculateParams(totalItems, currentPage, itemsPerPage);

            int offset = calc.getOffset();
            int safePage = calc.getSafePage();

            // Get filtered data
            List<RepairJourneySummaryDTO> repairJourneySummaryDTOList =
                    repairListService.getFilteredTracker(fullName, vehicleId, sortBy, itemsPerPage, offset);

            // Create pagination result
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
            request.setAttribute("selectedVehicleId", vehicleIdParam);
            request.setAttribute("selectedSortBy", sortBy);

            // Forward to JSP
            request.getRequestDispatcher("/view/customerservice/view-all-repair.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Error querying repair list: " + e.getMessage(), e);
        }
    }
}
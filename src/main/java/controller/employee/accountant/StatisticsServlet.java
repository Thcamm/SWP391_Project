package controller.employee.accountant;

import model.dto.ReportDTO;
import service.statistics.StatisticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "StatisticsServlet", urlPatterns = {"/accountant/statistics"})
public class StatisticsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private StatisticsService statisticsService;

    @Override
    public void init() throws ServletException {
        super.init();
        statisticsService = new StatisticsService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer roleID = (Integer) session.getAttribute("roleID");

        if (roleID == null || roleID != 5) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            ReportDTO kpiSummary = statisticsService.getKPISummary();
            int totalCustomers = statisticsService.getTotalCustomers();
            BigDecimal collectionRate = statisticsService.getCollectionRate();
            BigDecimal growthRate = statisticsService.getRevenueGrowthRate();
            List<ReportDTO> topMonths = statisticsService.getTopRevenueMonths();

            request.setAttribute("kpiSummary", kpiSummary);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("collectionRate", collectionRate);
            request.setAttribute("growthRate", growthRate);
            request.setAttribute("topMonths", topMonths);

            request.getRequestDispatcher("/view/accountant/statistics.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error loading statistics: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }
}
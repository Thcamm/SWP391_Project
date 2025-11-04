package controller.employee.accountant;

import model.dto.ReportDTO;
import service.statistics.StatisticsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "StatisticsServlet", urlPatterns = {"/accountant/statistics"})
public class StatisticsServlet extends HttpServlet {

    private StatisticsService statisticsService;

    @Override
    public void init() throws ServletException {
        super.init();
        statisticsService = new StatisticsService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Lấy tất cả dữ liệu thống kê

            // 1. KPI Summary
            ReportDTO kpiSummary = statisticsService.getKPISummary();
            int totalCustomers = statisticsService.getTotalCustomers();
            BigDecimal onTimeRate = statisticsService.getOnTimePaymentRate();
            BigDecimal growthRate = statisticsService.getRevenueGrowthRate();
            BigDecimal collectionRate = statisticsService.getCollectionRate();

            // 2. Charts data
            List<ReportDTO> revenueByWeek = statisticsService.getRevenueByWeek();
            List<ReportDTO> yearComparison = statisticsService.getYearOverYearComparison();
            List<ReportDTO> topMonths = statisticsService.getTopRevenueMonths(3);
            List<ReportDTO> newCustomers = statisticsService.getNewCustomersByMonth();

            // Set attributes
            request.setAttribute("kpiSummary", kpiSummary);
            request.setAttribute("totalCustomers", totalCustomers);
            request.setAttribute("onTimeRate", onTimeRate);
            request.setAttribute("growthRate", growthRate);
            request.setAttribute("collectionRate", collectionRate);

            request.setAttribute("revenueByWeek", revenueByWeek);
            request.setAttribute("yearComparison", yearComparison);
            request.setAttribute("topMonths", topMonths);
            request.setAttribute("newCustomers", newCustomers);

            request.getRequestDispatcher("/view/accountant/statistics.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }
}
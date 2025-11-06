package controller.employee.accountant;

import model.dto.ReportDTO;
import service.report.ReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "ReportServlet", urlPatterns = {"/accountant/report"})
public class ReportServlet extends HttpServlet {

    private ReportService reportService;

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if (action == null) {
                action = "dashboard";
            }

            switch (action) {
                case "dashboard":
                    showDashboard(request, response);
                    break;
                case "revenue":
                    showRevenueReport(request, response);
                    break;
                case "customer":
                    showCustomerReport(request, response);
                    break;
                case "overdue":
                    showOverdueReport(request, response);
                    break;
                default:
                    showDashboard(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // Lấy khoảng thời gian từ request (mặc định tháng hiện tại)
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        Date startDate;
        Date endDate;

        if (startDateStr != null && endDateStr != null) {
            startDate = Date.valueOf(startDateStr);
            endDate = Date.valueOf(endDateStr);
        } else {
            LocalDate now = LocalDate.now();
            startDate = Date.valueOf(now.withDayOfMonth(1));
            endDate = Date.valueOf(now.withDayOfMonth(now.lengthOfMonth()));
        }

        // Lấy tất cả báo cáo
        ReportDTO revenueSummary = reportService.getRevenueSummary(startDate, endDate);
        List<ReportDTO> revenueByMonth = reportService.getRevenueByMonth(6);
        List<ReportDTO> invoiceByStatus = reportService.getInvoiceByStatus(startDate, endDate);
        List<ReportDTO> paymentByMethod = reportService.getPaymentByMethod(startDate, endDate);
        List<ReportDTO> topCustomers = reportService.getTopPayingCustomers(5, null, null);
        List<ReportDTO> overdueReport = reportService.getOverdueInvoicesReport();

        // Set attributes
        request.setAttribute("revenueSummary", revenueSummary);
        request.setAttribute("revenueByMonth", revenueByMonth);
        request.setAttribute("invoiceByStatus", invoiceByStatus);
        request.setAttribute("paymentByMethod", paymentByMethod);
        request.setAttribute("topCustomers", topCustomers);
        request.setAttribute("overdueReport", overdueReport);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);

        request.getRequestDispatcher("/view/accountant/report-dashboard.jsp").forward(request, response);
    }

    private void showRevenueReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String monthsStr = request.getParameter("months");
        int months = monthsStr != null ? Integer.parseInt(monthsStr) : 12;

        List<ReportDTO> revenueByMonth = reportService.getRevenueByMonth(months);

        request.setAttribute("revenueByMonth", revenueByMonth);
        request.setAttribute("selectedMonths", months);

        request.getRequestDispatcher("/view/accountant/report-revenue.jsp").forward(request, response);
    }

    private void showCustomerReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String typeStr = request.getParameter("type");
        String type = typeStr != null ? typeStr : "top";

        if ("outstanding".equals(type)) {
            // Danh sách khách hàng có công nợ
            List<ReportDTO> customersWithOutstanding = reportService.getCustomersWithOutstanding();
            request.setAttribute("customers", customersWithOutstanding);
            request.setAttribute("reportType", "outstanding");
        } else {
            // Top khách hàng thanh toán nhiều
            LocalDate now = LocalDate.now();
            Date startDate = Date.valueOf(now.minusMonths(6));
            Date endDate = Date.valueOf(now);

            String limitStr = request.getParameter("limit");
            int limit = limitStr != null ? Integer.parseInt(limitStr) : 10;

            List<ReportDTO> topCustomers = reportService.getTopPayingCustomers(limit, startDate, endDate);
            request.setAttribute("customers", topCustomers);
            request.setAttribute("reportType", "top");
            request.setAttribute("limit", limit);
        }

        request.getRequestDispatcher("/view/accountant/report-customer.jsp").forward(request, response);
    }

    private void showOverdueReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<ReportDTO> overdueReport = reportService.getOverdueInvoicesReport();

        request.setAttribute("overdueReport", overdueReport);

        request.getRequestDispatcher("/view/accountant/report-overdue.jsp").forward(request, response);
    }
}
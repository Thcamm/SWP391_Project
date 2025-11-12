package controller.employee.accountant;

import model.dto.ReportDTO;
import service.report.ReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "ReportServlet", urlPatterns = {"/accountant/report"})
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ReportService reportService;

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
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

        String action = request.getParameter("action");

        try {
            if (action == null || action.isEmpty()) {
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
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        Date startDate;
        Date endDate;

        if (startDateStr != null && !startDateStr.isEmpty() &&
                endDateStr != null && !endDateStr.isEmpty()) {
            try {
                startDate = Date.valueOf(startDateStr);
                endDate = Date.valueOf(endDateStr);
            } catch (IllegalArgumentException e) {
                throw new Exception("Invalid date format. Use YYYY-MM-DD");
            }
        } else {
            LocalDate now = LocalDate.now();
            startDate = Date.valueOf(now.withDayOfMonth(1));
            endDate = Date.valueOf(now.withDayOfMonth(now.lengthOfMonth()));
        }

        ReportDTO revenueSummary = reportService.getRevenueSummary(startDate, endDate);
        List<ReportDTO> revenueByMonth = reportService.getRevenueByMonth(6);
        List<ReportDTO> invoiceByStatus = reportService.getInvoiceByStatus(startDate, endDate);
        List<ReportDTO> paymentByMethod = reportService.getPaymentByMethod(startDate, endDate);
        List<ReportDTO> topCustomers = reportService.getTopPayingCustomers(5, startDate, endDate);
        ReportDTO overdueInvoices = reportService.getOverdueInvoicesSummary();
        List<ReportDTO> customersWithOutstanding = reportService.getCustomersWithOutstanding();

        request.setAttribute("revenueSummary", revenueSummary);
        request.setAttribute("revenueByMonth", revenueByMonth);
        request.setAttribute("invoiceByStatus", invoiceByStatus);
        request.setAttribute("paymentByMethod", paymentByMethod);
        request.setAttribute("topCustomers", topCustomers);
        request.setAttribute("overdueInvoices", overdueInvoices);
        request.setAttribute("customersWithOutstanding", customersWithOutstanding);
        request.setAttribute("startDate", startDate.toString());
        request.setAttribute("endDate", endDate.toString());

        request.getRequestDispatcher("/view/accountant/report-dashboard.jsp").forward(request, response);
    }

    private void showRevenueReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String monthsStr = request.getParameter("months");
        int months = 12;

        if (monthsStr != null && !monthsStr.isEmpty()) {
            try {
                months = Integer.parseInt(monthsStr);
            } catch (NumberFormatException e) {
                throw new Exception("Invalid months parameter");
            }
        }

        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");

        Date startDate;
        Date endDate;

        if (startDateStr != null && !startDateStr.isEmpty() &&
                endDateStr != null && !endDateStr.isEmpty()) {
            try {
                startDate = Date.valueOf(startDateStr);
                endDate = Date.valueOf(endDateStr);
            } catch (IllegalArgumentException e) {
                throw new Exception("Invalid date format. Use YYYY-MM-DD");
            }
        } else {
            LocalDate now = LocalDate.now();
            startDate = Date.valueOf(now.minusMonths(1).withDayOfMonth(1));
            endDate = Date.valueOf(now.withDayOfMonth(now.lengthOfMonth()));
        }

        List<ReportDTO> revenueByMonth = reportService.getRevenueByMonth(months);
        ReportDTO revenueSummary = reportService.getRevenueSummary(startDate, endDate);
        List<ReportDTO> paymentByMethod = reportService.getPaymentByMethod(startDate, endDate);

        request.setAttribute("revenueByMonth", revenueByMonth);
        request.setAttribute("revenueSummary", revenueSummary);
        request.setAttribute("paymentByMethod", paymentByMethod);
        request.setAttribute("selectedMonths", months);
        request.setAttribute("startDate", startDate.toString());
        request.setAttribute("endDate", endDate.toString());

        request.getRequestDispatcher("/view/accountant/report-revenue.jsp").forward(request, response);
    }

    private void showCustomerReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String type = request.getParameter("type");
        if (type == null || type.isEmpty()) {
            type = "top";
        }

        if ("outstanding".equals(type)) {
            List<ReportDTO> customersWithOutstanding = reportService.getCustomersWithOutstanding();

            request.setAttribute("customers", customersWithOutstanding);
            request.setAttribute("reportType", "outstanding");
            request.setAttribute("totalCustomers", customersWithOutstanding.size());

        } else {
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");

            Date startDate;
            Date endDate;

            if (startDateStr != null && !startDateStr.isEmpty() &&
                    endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    startDate = Date.valueOf(startDateStr);
                    endDate = Date.valueOf(endDateStr);
                } catch (IllegalArgumentException e) {
                    throw new Exception("Invalid date format. Use YYYY-MM-DD");
                }
            } else {
                LocalDate now = LocalDate.now();
                startDate = Date.valueOf(now.minusMonths(6));
                endDate = Date.valueOf(now);
            }

            String limitStr = request.getParameter("limit");
            int limit = 10;

            if (limitStr != null && !limitStr.isEmpty()) {
                try {
                    limit = Integer.parseInt(limitStr);
                } catch (NumberFormatException e) {
                    throw new Exception("Invalid limit parameter");
                }
            }

            List<ReportDTO> topCustomers = reportService.getTopPayingCustomers(limit, startDate, endDate);

            request.setAttribute("customers", topCustomers);
            request.setAttribute("reportType", "top");
            request.setAttribute("limit", limit);
            request.setAttribute("startDate", startDate.toString());
            request.setAttribute("endDate", endDate.toString());
        }

        request.getRequestDispatcher("/view/accountant/report-customer.jsp").forward(request, response);
    }

    private void showOverdueReport(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ReportDTO overdueInvoices = reportService.getOverdueInvoicesSummary();
        List<ReportDTO> customersWithOutstanding = reportService.getCustomersWithOutstanding();

        request.setAttribute("overdueInvoices", overdueInvoices);
        request.setAttribute("customersWithOutstanding", customersWithOutstanding);

        request.getRequestDispatcher("/view/accountant/report-overdue.jsp").forward(request, response);
    }
}
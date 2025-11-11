package service.report;

import dao.report.ReportDAO;
import model.dto.ReportDTO;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO;

    public ReportService() {
        this.reportDAO = new ReportDAO();
    }

    public ReportService(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    /**
     *  Get revenue summary for date range
     */
    public ReportDTO getRevenueSummary(Date startDate, Date endDate) throws Exception {
        validateDateRange(startDate, endDate);

        try {
            return reportDAO.getRevenueSummary(startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Error getting revenue summary: " + e.getMessage(), e);
        }
    }

    /**
     * Get revenue by month (default 12 months)
     */
    public List<ReportDTO> getRevenueByMonth() throws Exception {
        return getRevenueByMonth(12);
    }

    /**
     *  Get revenue by month (custom number of months)
     */
    public List<ReportDTO> getRevenueByMonth(int numberOfMonths) throws Exception {
        if (numberOfMonths < 1 || numberOfMonths > 24) {
            throw new Exception("Number of months must be between 1 and 24");
        }

        try {
            return reportDAO.getRevenueByMonth(numberOfMonths);
        } catch (SQLException e) {
            throw new Exception("Error getting monthly revenue: " + e.getMessage(), e);
        }
    }

    /**
     * Get payment by method (ONLINE vs OFFLINE)
     */
    public List<ReportDTO> getPaymentByMethod(Date startDate, Date endDate) throws Exception {
        validateDateRange(startDate, endDate);

        try {
            return reportDAO.getPaymentByMethod(startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Error getting payment method report: " + e.getMessage(), e);
        }
    }


    /**
     * Get top paying customers (default top 10)
     */
    public List<ReportDTO> getTopPayingCustomers(Date startDate, Date endDate) throws Exception {
        return getTopPayingCustomers(10, startDate, endDate);
    }

    /**
     * Get top paying customers (custom limit)
     */
    public List<ReportDTO> getTopPayingCustomers(int limit, Date startDate, Date endDate) throws Exception {
        validateDateRange(startDate, endDate);

        if (limit < 1 || limit > 100) {
            throw new Exception("Limit must be between 1 and 100");
        }

        try {
            return reportDAO.getTopPayingCustomers(limit, startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Error getting top customers: " + e.getMessage(), e);
        }
    }

    /**
     *  Get customers with outstanding balance
     */
    public List<ReportDTO> getCustomersWithOutstanding() throws Exception {
        try {
            return reportDAO.getCustomersWithOutstanding();
        } catch (SQLException e) {
            throw new Exception("Error getting customers with outstanding: " + e.getMessage(), e);
        }
    }

    /**
     *  Get invoice distribution by status
     */
    public List<ReportDTO> getInvoiceByStatus(Date startDate, Date endDate) throws Exception {
        validateDateRange(startDate, endDate);

        try {
            return reportDAO.getInvoiceByStatus(startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Error getting invoice by status: " + e.getMessage(), e);
        }
    }

    /**
     * Get overdue invoices summary
     */
    public ReportDTO getOverdueInvoicesSummary() throws Exception {
        try {
            return reportDAO.getOverdueInvoicesSummary();
        } catch (SQLException e) {
            throw new Exception("Error getting overdue invoices: " + e.getMessage(), e);
        }
    }

    /**
     * Get all reports for dashboard (current month)
     */
    public ReportSummary getDashboardReports() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Date startDate = Date.valueOf(startOfMonth);
        Date endDate = Date.valueOf(endOfMonth);

        return getDashboardReports(startDate, endDate);
    }

    /**
     * Get all reports for dashboard (custom date range)
     */
    public ReportSummary getDashboardReports(Date startDate, Date endDate) throws Exception {
        validateDateRange(startDate, endDate);

        ReportSummary summary = new ReportSummary();

        try {
            summary.setRevenueSummary(getRevenueSummary(startDate, endDate));
            summary.setRevenueByMonth(getRevenueByMonth(6));
            summary.setInvoiceByStatus(getInvoiceByStatus(startDate, endDate));
            summary.setPaymentByMethod(getPaymentByMethod(startDate, endDate));
            summary.setTopCustomers(getTopPayingCustomers(5, startDate, endDate));
            summary.setOverdueInvoices(getOverdueInvoicesSummary());
            summary.setCustomersWithOutstanding(getCustomersWithOutstanding());
        } catch (Exception e) {
            throw new Exception("Error generating dashboard: " + e.getMessage(), e);
        }

        return summary;
    }


    /**
     * Validate date range
     */
    private void validateDateRange(Date startDate, Date endDate) throws Exception {
        if (startDate == null || endDate == null) {
            throw new Exception("Start date and end date are required");
        }

        if (startDate.after(endDate)) {
            throw new Exception("Start date must be before or equal to end date");
        }

        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();
        LocalDate now = LocalDate.now();

        if (start.isAfter(now)) {
            throw new Exception("Start date cannot be in the future");
        }

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start, end);
        if (daysBetween > 365) {
            throw new Exception("Date range cannot exceed 1 year");
        }
    }


    /**
     * Container for all dashboard reports
     */
    public static class ReportSummary {
        private ReportDTO revenueSummary;
        private List<ReportDTO> revenueByMonth;
        private List<ReportDTO> invoiceByStatus;
        private List<ReportDTO> paymentByMethod;
        private List<ReportDTO> topCustomers;
        private ReportDTO overdueInvoices;
        private List<ReportDTO> customersWithOutstanding;

        public ReportDTO getRevenueSummary() {
            return revenueSummary;
        }

        public void setRevenueSummary(ReportDTO revenueSummary) {
            this.revenueSummary = revenueSummary;
        }

        public List<ReportDTO> getRevenueByMonth() {
            return revenueByMonth;
        }

        public void setRevenueByMonth(List<ReportDTO> revenueByMonth) {
            this.revenueByMonth = revenueByMonth;
        }

        public List<ReportDTO> getInvoiceByStatus() {
            return invoiceByStatus;
        }

        public void setInvoiceByStatus(List<ReportDTO> invoiceByStatus) {
            this.invoiceByStatus = invoiceByStatus;
        }

        public List<ReportDTO> getPaymentByMethod() {
            return paymentByMethod;
        }

        public void setPaymentByMethod(List<ReportDTO> paymentByMethod) {
            this.paymentByMethod = paymentByMethod;
        }

        public List<ReportDTO> getTopCustomers() {
            return topCustomers;
        }

        public void setTopCustomers(List<ReportDTO> topCustomers) {
            this.topCustomers = topCustomers;
        }

        public ReportDTO getOverdueInvoices() {
            return overdueInvoices;
        }

        public void setOverdueInvoices(ReportDTO overdueInvoices) {
            this.overdueInvoices = overdueInvoices;
        }

        public List<ReportDTO> getCustomersWithOutstanding() {
            return customersWithOutstanding;
        }

        public void setCustomersWithOutstanding(List<ReportDTO> customersWithOutstanding) {
            this.customersWithOutstanding = customersWithOutstanding;
        }
    }
}
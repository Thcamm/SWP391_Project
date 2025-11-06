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

    /**
     * Lấy báo cáo tổng quan doanh thu theo khoảng thời gian
     */
    public ReportDTO getRevenueSummary(Date startDate, Date endDate) throws Exception {
        try {
            return reportDAO.getRevenueSummary(startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy báo cáo doanh thu: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy báo cáo doanh thu theo tháng (mặc định 12 tháng)
     */
    public List<ReportDTO> getRevenueByMonth() throws Exception {
        return getRevenueByMonth(12);
    }

    /**
     * Lấy báo cáo doanh thu theo tháng
     */
    public List<ReportDTO> getRevenueByMonth(int numberOfMonths) throws Exception {
        try {
            return reportDAO.getRevenueByMonth(numberOfMonths);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy báo cáo doanh thu theo tháng: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy báo cáo theo trạng thái hóa đơn
     */
    public List<ReportDTO> getInvoiceByStatus(Date startDate, Date endDate) throws Exception {
        try {
            return reportDAO.getInvoiceByStatus(startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy báo cáo theo trạng thái: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy báo cáo thanh toán theo phương thức
     */
    public List<ReportDTO> getPaymentByMethod(Date startDate, Date endDate) throws Exception {
        try {
            return reportDAO.getPaymentByMethod(startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy báo cáo thanh toán: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy top khách hàng thanh toán nhiều nhất
     */
    public List<ReportDTO> getTopPayingCustomers(int limit, Date startDate, Date endDate) throws Exception {
        try {
            return reportDAO.getTopPayingCustomers(limit, startDate, endDate);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy báo cáo khách hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy danh sách khách hàng có công nợ
     */
    public List<ReportDTO> getCustomersWithOutstanding() throws Exception {
        try {
            return reportDAO.getCustomersWithOutstanding();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy danh sách công nợ: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy báo cáo hóa đơn quá hạn
     */
    public List<ReportDTO> getOverdueInvoicesReport() throws Exception {
        try {
            return reportDAO.getOverdueInvoicesReport();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy báo cáo quá hạn: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tất cả báo cáo cho dashboard (tháng hiện tại)
     */
    public ReportSummary getDashboardReports() throws Exception {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Date startDate = Date.valueOf(startOfMonth);
        Date endDate = Date.valueOf(endOfMonth);

        ReportSummary summary = new ReportSummary();
        summary.setRevenueSummary(getRevenueSummary(startDate, endDate));
        summary.setRevenueByMonth(getRevenueByMonth(6)); // 6 tháng gần nhất
        summary.setInvoiceByStatus(getInvoiceByStatus(startDate, endDate));
        summary.setPaymentByMethod(getPaymentByMethod(startDate, endDate));
        summary.setTopCustomers(getTopPayingCustomers(5, startDate, endDate));
        summary.setOverdueReport(getOverdueInvoicesReport());

        return summary;
    }

    /**
     * Inner class để gom tất cả báo cáo
     */
    public static class ReportSummary {
        private ReportDTO revenueSummary;
        private List<ReportDTO> revenueByMonth;
        private List<ReportDTO> invoiceByStatus;
        private List<ReportDTO> paymentByMethod;
        private List<ReportDTO> topCustomers;
        private List<ReportDTO> overdueReport;

        // Getters and Setters
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

        public List<ReportDTO> getOverdueReport() {
            return overdueReport;
        }

        public void setOverdueReport(List<ReportDTO> overdueReport) {
            this.overdueReport = overdueReport;
        }
    }
}
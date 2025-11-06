package dao.report;

import common.DbContext;
import model.dto.ReportDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    /**
     * 1. Báo cáo Tổng quan Doanh thu
     */
    public ReportDTO getRevenueSummary(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT " +
                "    COALESCE(SUM(TotalAmount), 0) as TotalInvoiced, " +
                "    COALESCE(SUM(PaidAmount), 0) as TotalPaid, " +
                "    COALESCE(SUM(BalanceAmount), 0) as TotalOutstanding, " +
                "    COUNT(*) as InvoiceCount " +
                "FROM Invoice " +
                "WHERE InvoiceDate BETWEEN ? AND ? " +
                "AND PaymentStatus != 'VOID'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ReportDTO report = new ReportDTO();
                    report.setTotalInvoiced(rs.getBigDecimal("TotalInvoiced"));
                    report.setTotalPaid(rs.getBigDecimal("TotalPaid"));
                    report.setTotalOutstanding(rs.getBigDecimal("TotalOutstanding"));
                    report.setCount(rs.getInt("InvoiceCount"));
                    return report;
                }
            }
        }
        return new ReportDTO("No Data", BigDecimal.ZERO, 0);
    }

    /**
     * 2. Báo cáo Doanh thu theo Tháng (12 tháng gần nhất) - FIXED
     */
    public List<ReportDTO> getRevenueByMonth(int numberOfMonths) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    YEAR(InvoiceDate) as Year, " +
                "    MONTH(InvoiceDate) as Month, " +
                "    DATE_FORMAT(InvoiceDate, '%Y-%m') as MonthYear, " +
                "    COALESCE(SUM(TotalAmount), 0) as TotalInvoiced, " +
                "    COALESCE(SUM(PaidAmount), 0) as TotalPaid, " +
                "    COUNT(*) as InvoiceCount " +
                "FROM Invoice " +
                "WHERE InvoiceDate >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                "AND PaymentStatus != 'VOID' " +
                "GROUP BY YEAR(InvoiceDate), MONTH(InvoiceDate), DATE_FORMAT(InvoiceDate, '%Y-%m') " +
                "ORDER BY Year DESC, Month DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numberOfMonths);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = new ReportDTO();
                    report.setYear(rs.getInt("Year"));
                    report.setMonth(rs.getInt("Month"));
                    report.setMonthYear(rs.getString("MonthYear"));
                    report.setTotalInvoiced(rs.getBigDecimal("TotalInvoiced"));
                    report.setTotalPaid(rs.getBigDecimal("TotalPaid"));
                    report.setCount(rs.getInt("InvoiceCount"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * 3. Báo cáo theo Trạng thái Hóa đơn
     */
    public List<ReportDTO> getInvoiceByStatus(Date startDate, Date endDate) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    PaymentStatus, " +
                "    COUNT(*) as InvoiceCount, " +
                "    COALESCE(SUM(TotalAmount), 0) as TotalAmount, " +
                "    COALESCE(SUM(PaidAmount), 0) as PaidAmount, " +
                "    COALESCE(SUM(BalanceAmount), 0) as BalanceAmount " +
                "FROM Invoice " +
                "WHERE InvoiceDate BETWEEN ? AND ? " +
                "GROUP BY PaymentStatus " +
                "ORDER BY InvoiceCount DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = new ReportDTO();
                    report.setStatus(rs.getString("PaymentStatus"));
                    report.setInvoiceCount(rs.getInt("InvoiceCount"));
                    report.setTotalInvoiced(rs.getBigDecimal("TotalAmount"));
                    report.setTotalPaid(rs.getBigDecimal("PaidAmount"));
                    report.setTotalOutstanding(rs.getBigDecimal("BalanceAmount"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * 4. Báo cáo Thanh toán theo Phương thức
     */
    public List<ReportDTO> getPaymentByMethod(Date startDate, Date endDate) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    Method, " +
                "    COUNT(*) as PaymentCount, " +
                "    COALESCE(SUM(Amount), 0) as TotalAmount " +
                "FROM Payment " +
                "WHERE DATE(PaymentDate) BETWEEN ? AND ? " +
                "GROUP BY Method " +
                "ORDER BY TotalAmount DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = new ReportDTO();
                    report.setPaymentMethod(rs.getString("Method"));
                    report.setPaymentCount(rs.getInt("PaymentCount"));
                    report.setPaymentAmount(rs.getBigDecimal("TotalAmount"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * 5. Top Khách hàng thanh toán nhiều nhất - FIXED
     */
    public List<ReportDTO> getTopPayingCustomers(int limit, Date startDate, Date endDate) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    c.CustomerID, " +
                "    MAX(u.FullName) as CustomerName, " +
                "    MAX(u.Email) as CustomerEmail, " +
                "    COUNT(DISTINCT i.InvoiceID) as TotalInvoices, " +
                "    COALESCE(SUM(i.TotalAmount), 0) as TotalInvoiced, " +
                "    COALESCE(SUM(i.PaidAmount), 0) as TotalPaid, " +
                "    COALESCE(SUM(i.BalanceAmount), 0) as OutstandingBalance " +
                "FROM Invoice i " +
                "INNER JOIN WorkOrder wo ON i.WorkOrderID = wo.WorkOrderID " +
                "INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "INNER JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "INNER JOIN User u ON c.UserID = u.UserID " +
                "WHERE i.InvoiceDate BETWEEN ? AND ? " +
                "AND i.PaymentStatus != 'VOID' " +
                "GROUP BY c.CustomerID " +
                "ORDER BY TotalPaid DESC " +
                "LIMIT ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = new ReportDTO();
                    report.setCustomerID(rs.getInt("CustomerID"));
                    report.setCustomerName(rs.getString("CustomerName"));
                    report.setCustomerEmail(rs.getString("CustomerEmail"));
                    report.setTotalInvoices(rs.getInt("TotalInvoices"));
                    report.setTotalInvoiced(rs.getBigDecimal("TotalInvoiced"));
                    report.setTotalPaid(rs.getBigDecimal("TotalPaid"));
                    report.setOutstandingBalance(rs.getBigDecimal("OutstandingBalance"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * 6. Khách hàng có công nợ - FIXED
     */
    public List<ReportDTO> getCustomersWithOutstanding() throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    c.CustomerID, " +
                "    MAX(u.FullName) as CustomerName, " +
                "    MAX(u.Email) as CustomerEmail, " +
                "    MAX(u.PhoneNumber) as PhoneNumber, " +
                "    COUNT(DISTINCT i.InvoiceID) as TotalInvoices, " +
                "    COALESCE(SUM(i.BalanceAmount), 0) as OutstandingBalance " +
                "FROM Invoice i " +
                "INNER JOIN WorkOrder wo ON i.WorkOrderID = wo.WorkOrderID " +
                "INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "INNER JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "INNER JOIN User u ON c.UserID = u.UserID " +
                "WHERE i.PaymentStatus IN ('UNPAID', 'PARTIALLY_PAID') " +
                "GROUP BY c.CustomerID " +
                "HAVING OutstandingBalance > 0 " +
                "ORDER BY OutstandingBalance DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReportDTO report = new ReportDTO();
                report.setCustomerID(rs.getInt("CustomerID"));
                report.setCustomerName(rs.getString("CustomerName"));
                report.setCustomerEmail(rs.getString("CustomerEmail"));
                report.setTotalInvoices(rs.getInt("TotalInvoices"));
                report.setOutstandingBalance(rs.getBigDecimal("OutstandingBalance"));
                reports.add(report);
            }
        }
        return reports;
    }

    /**
     * 7. Hóa đơn quá hạn chi tiết
     */
    public List<ReportDTO> getOverdueInvoicesReport() throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    COUNT(*) as OverdueCount, " +
                "    COALESCE(SUM(BalanceAmount), 0) as OverdueAmount " +
                "FROM Invoice " +
                "WHERE PaymentStatus IN ('UNPAID', 'PARTIALLY_PAID') " +
                "AND DueDate < CURDATE()";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                ReportDTO report = new ReportDTO();
                report.setLabel("Overdue Summary");
                report.setCount(rs.getInt("OverdueCount"));
                report.setAmount(rs.getBigDecimal("OverdueAmount"));
                reports.add(report);
            }
        }
        return reports;
    }
}
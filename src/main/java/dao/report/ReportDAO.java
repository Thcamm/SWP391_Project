package dao.report;

import common.DbContext;
import model.dto.ReportDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
    /**
     * Revenue Summary
     * Total revenue overview for a date range
     */
    public ReportDTO getRevenueSummary(Date startDate, Date endDate) throws SQLException {
        String sql = "SELECT " +
                "    COALESCE(SUM(TotalAmount), 0) as TotalInvoiced, " +
                "    COALESCE(SUM(PaidAmount), 0) as TotalPaid, " +
                "    COALESCE(SUM(BalanceAmount), 0) as TotalOutstanding, " +
                "    COUNT(*) as InvoiceCount " +
                "FROM Invoice " +
                "WHERE InvoiceDate BETWEEN ? AND ? " +
                "  AND PaymentStatus != 'VOID'";

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
     *  Revenue by Month
     * Monthly revenue for chart display (last N months)
     */
    public List<ReportDTO> getRevenueByMonth(int numberOfMonths) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    YEAR(InvoiceDate) as Year, " +
                "    MONTH(InvoiceDate) as Month, " +
                "    COALESCE(SUM(TotalAmount), 0) as TotalInvoiced, " +
                "    COALESCE(SUM(PaidAmount), 0) as TotalPaid, " +
                "    COUNT(*) as InvoiceCount " +
                "FROM Invoice " +
                "WHERE InvoiceDate >= DATE_SUB(CURDATE(), INTERVAL ? MONTH) " +
                "  AND PaymentStatus != 'VOID' " +
                "GROUP BY YEAR(InvoiceDate), MONTH(InvoiceDate) " +
                "ORDER BY Year DESC, Month DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, numberOfMonths);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = new ReportDTO();

                    int year = rs.getInt("Year");
                    int month = rs.getInt("Month");

                    report.setYear(year);
                    report.setMonth(month);
                    report.setMonthYear(String.format("%d-%02d", year, month));
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
     * Payment by Method
     * Compare ONLINE vs OFFLINE payments
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
     * Top Paying Customers
     * Top N customers by payment amount (for CRM insights)
     */
    public List<ReportDTO> getTopPayingCustomers(int limit, Date startDate, Date endDate) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    c.CustomerID, " +
                "    u.FullName as CustomerName, " +
                "    u.Email as CustomerEmail, " +
                "    u.PhoneNumber, " +
                "    COUNT(DISTINCT i.InvoiceID) as TotalInvoices, " +
                "    COALESCE(SUM(i.TotalAmount), 0) as TotalInvoiced, " +
                "    COALESCE(SUM(i.PaidAmount), 0) as TotalPaid " +
                "FROM Customer c " +
                "INNER JOIN User u ON c.UserID = u.UserID " +
                "INNER JOIN ServiceRequest sr ON sr.CustomerID = c.CustomerID " +
                "INNER JOIN WorkOrder wo ON wo.RequestID = sr.RequestID " +
                "INNER JOIN Invoice i ON i.WorkOrderID = wo.WorkOrderID " +
                "WHERE i.InvoiceDate BETWEEN ? AND ? " +
                "  AND i.PaymentStatus != 'VOID' " +
                "GROUP BY c.CustomerID, u.FullName, u.Email, u.PhoneNumber " +
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
                    report.setPhoneNumber(rs.getString("PhoneNumber"));
                    report.setTotalInvoices(rs.getInt("TotalInvoices"));
                    report.setTotalInvoiced(rs.getBigDecimal("TotalInvoiced"));
                    report.setTotalPaid(rs.getBigDecimal("TotalPaid"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * Customers with Outstanding Balance
     * Customers who still owe money (debt management)
     */
    public List<ReportDTO> getCustomersWithOutstanding() throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    c.CustomerID, " +
                "    u.FullName as CustomerName, " +
                "    u.Email as CustomerEmail, " +
                "    u.PhoneNumber, " +
                "    COUNT(DISTINCT i.InvoiceID) as TotalInvoices, " +
                "    COALESCE(SUM(i.BalanceAmount), 0) as OutstandingBalance " +
                "FROM Customer c " +
                "INNER JOIN User u ON c.UserID = u.UserID " +
                "INNER JOIN ServiceRequest sr ON sr.CustomerID = c.CustomerID " +
                "INNER JOIN WorkOrder wo ON wo.RequestID = sr.RequestID " +
                "INNER JOIN Invoice i ON i.WorkOrderID = wo.WorkOrderID " +
                "WHERE i.PaymentStatus IN ('UNPAID', 'PARTIALLY_PAID') " +
                "GROUP BY c.CustomerID, u.FullName, u.Email, u.PhoneNumber " +
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
                report.setPhoneNumber(rs.getString("PhoneNumber"));
                report.setTotalInvoices(rs.getInt("TotalInvoices"));
                report.setOutstandingBalance(rs.getBigDecimal("OutstandingBalance"));
                reports.add(report);
            }
        }
        return reports;
    }

    /**
     * Invoice by Status
     * Distribution of invoice statuses (pie chart)
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
                "ORDER BY " +
                "  CASE PaymentStatus " +
                "    WHEN 'UNPAID' THEN 1 " +
                "    WHEN 'PARTIALLY_PAID' THEN 2 " +
                "    WHEN 'PAID' THEN 3 " +
                "    WHEN 'VOID' THEN 4 " +
                "  END";

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
     *  Overdue Invoices
     * Summary of invoices past due date
     */
    public ReportDTO getOverdueInvoicesSummary() throws SQLException {
        String sql = "SELECT " +
                "    COUNT(*) as OverdueCount, " +
                "    COALESCE(SUM(BalanceAmount), 0) as OverdueAmount " +
                "FROM Invoice " +
                "WHERE PaymentStatus IN ('UNPAID', 'PARTIALLY_PAID') " +
                "  AND DueDate < CURDATE()";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                ReportDTO report = new ReportDTO();
                report.setLabel("Overdue Summary");
                report.setCount(rs.getInt("OverdueCount"));
                report.setAmount(rs.getBigDecimal("OverdueAmount"));
                return report;
            }
        }
        return new ReportDTO("No Overdue", BigDecimal.ZERO, 0);
    }
}
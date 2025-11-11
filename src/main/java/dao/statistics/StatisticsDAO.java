package dao.statistics;

import common.DbContext;
import model.dto.ReportDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDAO {

    /**
     * Overall KPI Summary
     * All key numbers in one query
     */
    public ReportDTO getKPISummary() throws SQLException {
        String sql = "SELECT " +
                "    COUNT(DISTINCT i.InvoiceID) as TotalInvoices, " +
                "    COALESCE(SUM(i.TotalAmount), 0) as TotalRevenue, " +
                "    COALESCE(SUM(i.PaidAmount), 0) as TotalPaid, " +
                "    COALESCE(SUM(i.BalanceAmount), 0) as TotalOutstanding, " +
                "    COALESCE(AVG(i.TotalAmount), 0) as AvgInvoiceValue " +
                "FROM Invoice i " +
                "WHERE i.PaymentStatus != 'VOID'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                ReportDTO kpi = new ReportDTO();
                kpi.setCount(rs.getInt("TotalInvoices"));
                kpi.setTotalInvoiced(rs.getBigDecimal("TotalRevenue"));
                kpi.setTotalPaid(rs.getBigDecimal("TotalPaid"));
                kpi.setTotalOutstanding(rs.getBigDecimal("TotalOutstanding"));
                kpi.setAmount(rs.getBigDecimal("AvgInvoiceValue"));
                return kpi;
            }
        }
        return new ReportDTO();
    }

    /**
     * Total Customers
     * Count unique customers with invoices
     */
    public int getTotalCustomers() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT c.CustomerID) as Total " +
                "FROM Customer c " +
                "INNER JOIN ServiceRequest sr ON sr.CustomerID = c.CustomerID " +
                "INNER JOIN WorkOrder wo ON wo.RequestID = sr.RequestID " +
                "INNER JOIN Invoice i ON i.WorkOrderID = wo.WorkOrderID " +
                "WHERE i.PaymentStatus != 'VOID'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("Total");
            }
        }
        return 0;
    }

    /**
     * Collection Rate (%)
     * How much we collected vs invoiced
     */
    public BigDecimal getCollectionRate() throws SQLException {
        String sql = "SELECT " +
                "    CASE " +
                "        WHEN COALESCE(SUM(TotalAmount), 0) = 0 THEN 0 " +
                "        ELSE COALESCE(SUM(PaidAmount), 0) * 100.0 / SUM(TotalAmount) " +
                "    END as Rate " +
                "FROM Invoice " +
                "WHERE PaymentStatus != 'VOID'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("Rate");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Revenue Growth Rate (%)
     * Current month vs previous month
     */
    public BigDecimal getRevenueGrowthRate() throws SQLException {
        String sql = "SELECT " +
                "    COALESCE(thisMonth.Revenue, 0) as Current, " +
                "    COALESCE(lastMonth.Revenue, 0) as Previous, " +
                "    CASE " +
                "        WHEN COALESCE(lastMonth.Revenue, 0) = 0 THEN 0 " +
                "        ELSE (COALESCE(thisMonth.Revenue, 0) - COALESCE(lastMonth.Revenue, 0)) * 100.0 / lastMonth.Revenue " +
                "    END as GrowthRate " +
                "FROM " +
                "    (SELECT COALESCE(SUM(PaidAmount), 0) as Revenue FROM Invoice " +
                "     WHERE YEAR(InvoiceDate) = YEAR(CURDATE()) " +
                "       AND MONTH(InvoiceDate) = MONTH(CURDATE()) " +
                "       AND PaymentStatus != 'VOID') as thisMonth, " +
                "    (SELECT COALESCE(SUM(PaidAmount), 0) as Revenue FROM Invoice " +
                "     WHERE InvoiceDate >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 MONTH), '%Y-%m-01') " +
                "       AND InvoiceDate < DATE_FORMAT(CURDATE(), '%Y-%m-01') " +
                "       AND PaymentStatus != 'VOID') as lastMonth";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("GrowthRate");
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Top Revenue Months (Top 5 all time)
     * Best performing months
     */
    public List<ReportDTO> getTopRevenueMonths() throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    YEAR(InvoiceDate) as Year, " +
                "    MONTH(InvoiceDate) as Month, " +
                "    COALESCE(SUM(PaidAmount), 0) as Revenue, " +
                "    COUNT(*) as InvoiceCount " +
                "FROM Invoice " +
                "WHERE PaymentStatus != 'VOID' " +
                "GROUP BY YEAR(InvoiceDate), MONTH(InvoiceDate) " +
                "ORDER BY Revenue DESC " +
                "LIMIT 5";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReportDTO report = new ReportDTO();

                int year = rs.getInt("Year");
                int month = rs.getInt("Month");

                report.setYear(year);
                report.setMonth(month);
                report.setMonthYear(String.format("%d-%02d", year, month));
                report.setAmount(rs.getBigDecimal("Revenue"));
                report.setCount(rs.getInt("InvoiceCount"));

                reports.add(report);
            }
        }
        return reports;
    }
}
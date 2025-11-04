package dao.statistics;

import common.DbContext;
import model.dto.ReportDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDAO {

    /**
     * 1. Lấy các KPI tổng quan
     */
    public ReportDTO getKPISummary() throws SQLException {
        String sql = "SELECT " +
                "    COALESCE(SUM(i.TotalAmount), 0) as TotalRevenue, " +
                "    COALESCE(SUM(i.PaidAmount), 0) as TotalPaid, " +
                "    COUNT(DISTINCT i.InvoiceID) as TotalInvoices, " +
                "    COUNT(DISTINCT wo.WorkOrderID) as TotalWorkOrders, " +
                "    COALESCE(AVG(i.TotalAmount), 0) as AvgInvoiceValue " +
                "FROM Invoice i " +
                "LEFT JOIN WorkOrder wo ON i.WorkOrderID = wo.WorkOrderID " +
                "WHERE i.PaymentStatus != 'VOID'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                ReportDTO kpi = new ReportDTO();
                kpi.setTotalInvoiced(rs.getBigDecimal("TotalRevenue"));
                kpi.setTotalPaid(rs.getBigDecimal("TotalPaid"));
                kpi.setCount(rs.getInt("TotalInvoices"));
                kpi.setAmount(rs.getBigDecimal("AvgInvoiceValue"));
                return kpi;
            }
        }
        return new ReportDTO();
    }

    /**
     * 2. Số lượng khách hàng duy nhất
     */
    public int getTotalCustomers() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT c.CustomerID) as TotalCustomers " +
                "FROM Invoice i " +
                "INNER JOIN WorkOrder wo ON i.WorkOrderID = wo.WorkOrderID " +
                "INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "INNER JOIN Customer c ON sr.CustomerID = c.CustomerID " +
                "WHERE i.PaymentStatus != 'VOID'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TotalCustomers");
            }
        }
        return 0;
    }

    /**
     * 3. Tỷ lệ thanh toán đúng hạn (%)
     */
    public BigDecimal getOnTimePaymentRate() throws SQLException {
        String sql = "SELECT " +
                "    COUNT(CASE WHEN PaymentStatus = 'PAID' AND DueDate >= CURDATE() THEN 1 END) * 100.0 / " +
                "    NULLIF(COUNT(CASE WHEN PaymentStatus != 'VOID' THEN 1 END), 0) as OnTimeRate " +
                "FROM Invoice";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                BigDecimal rate = rs.getBigDecimal("OnTimeRate");
                return rate != null ? rate : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 4. Tốc độ tăng trưởng doanh thu (% so với tháng trước) - FIXED
     */
    public BigDecimal getRevenueGrowthRate() throws SQLException {
        String sql = "SELECT " +
                "    (COALESCE(thisMonth.Revenue, 0) - COALESCE(lastMonth.Revenue, 0)) * 100.0 / " +
                "    NULLIF(COALESCE(lastMonth.Revenue, 1), 0) as GrowthRate " +
                "FROM " +
                "    (SELECT COALESCE(SUM(PaidAmount), 0) as Revenue FROM Invoice " +
                "     WHERE YEAR(InvoiceDate) = YEAR(CURDATE()) " +
                "     AND MONTH(InvoiceDate) = MONTH(CURDATE()) " +
                "     AND PaymentStatus != 'VOID') as thisMonth, " +
                "    (SELECT COALESCE(SUM(PaidAmount), 0) as Revenue FROM Invoice " +
                "     WHERE YEAR(InvoiceDate) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) " +
                "     AND MONTH(InvoiceDate) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) " +
                "     AND PaymentStatus != 'VOID') as lastMonth";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                BigDecimal rate = rs.getBigDecimal("GrowthRate");
                return rate != null ? rate : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 5. Doanh thu theo tuần (4 tuần gần nhất) - FIXED
     */
    public List<ReportDTO> getRevenueByWeek() throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    WEEK(InvoiceDate, 1) as WeekNumber, " +
                "    CONCAT('Week ', WEEK(InvoiceDate, 1)) as WeekLabel, " +
                "    COALESCE(SUM(TotalAmount), 0) as TotalAmount, " +
                "    COALESCE(SUM(PaidAmount), 0) as PaidAmount, " +
                "    COUNT(*) as InvoiceCount " +
                "FROM Invoice " +
                "WHERE InvoiceDate >= DATE_SUB(CURDATE(), INTERVAL 4 WEEK) " +
                "AND PaymentStatus != 'VOID' " +
                "GROUP BY WEEK(InvoiceDate, 1), CONCAT('Week ', WEEK(InvoiceDate, 1)) " +
                "ORDER BY WeekNumber DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReportDTO report = new ReportDTO();
                report.setLabel(rs.getString("WeekLabel"));
                report.setTotalInvoiced(rs.getBigDecimal("TotalAmount"));
                report.setTotalPaid(rs.getBigDecimal("PaidAmount"));
                report.setCount(rs.getInt("InvoiceCount"));
                reports.add(report);
            }
        }
        return reports;
    }

    /**
     * 6. So sánh doanh thu năm nay vs năm trước (theo tháng) - FIXED
     */
    public List<ReportDTO> getYearOverYearComparison() throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    MONTH(InvoiceDate) as Month, " +
                "    YEAR(InvoiceDate) as Year, " +
                "    COALESCE(SUM(TotalAmount), 0) as TotalAmount, " +
                "    COALESCE(SUM(PaidAmount), 0) as PaidAmount " +
                "FROM Invoice " +
                "WHERE YEAR(InvoiceDate) IN (YEAR(CURDATE()), YEAR(CURDATE()) - 1) " +
                "AND PaymentStatus != 'VOID' " +
                "GROUP BY YEAR(InvoiceDate), MONTH(InvoiceDate) " +
                "ORDER BY Year DESC, Month ASC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReportDTO report = new ReportDTO();
                report.setMonth(rs.getInt("Month"));
                report.setYear(rs.getInt("Year"));
                report.setTotalInvoiced(rs.getBigDecimal("TotalAmount"));
                report.setTotalPaid(rs.getBigDecimal("PaidAmount")); // FIXED: was TotalPaid
                reports.add(report);
            }
        }
        return reports;
    }

    /**
     * 7. Tỷ lệ thu hồi nợ (Collection Rate)
     */
    public BigDecimal getCollectionRate() throws SQLException {
        String sql = "SELECT " +
                "    COALESCE(SUM(PaidAmount), 0) * 100.0 / " +
                "    NULLIF(COALESCE(SUM(TotalAmount), 1), 0) as CollectionRate " +
                "FROM Invoice " +
                "WHERE PaymentStatus != 'VOID'";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                BigDecimal rate = rs.getBigDecimal("CollectionRate");
                return rate != null ? rate : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 8. Top 3 tháng doanh thu cao nhất (All time) - FIXED
     */
    public List<ReportDTO> getTopRevenueMonths(int limit) throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    YEAR(InvoiceDate) as Year, " +
                "    MONTH(InvoiceDate) as Month, " +
                "    DATE_FORMAT(InvoiceDate, '%m/%Y') as MonthYear, " +
                "    COALESCE(SUM(PaidAmount), 0) as TotalRevenue, " +
                "    COUNT(*) as InvoiceCount " +
                "FROM Invoice " +
                "WHERE PaymentStatus != 'VOID' " +
                "GROUP BY YEAR(InvoiceDate), MONTH(InvoiceDate), DATE_FORMAT(InvoiceDate, '%m/%Y') " +
                "ORDER BY TotalRevenue DESC " +
                "LIMIT ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportDTO report = new ReportDTO();
                    report.setYear(rs.getInt("Year"));
                    report.setMonth(rs.getInt("Month"));
                    report.setMonthYear(rs.getString("MonthYear"));
                    report.setAmount(rs.getBigDecimal("TotalRevenue"));
                    report.setCount(rs.getInt("InvoiceCount"));
                    reports.add(report);
                }
            }
        }
        return reports;
    }

    /**
     * 9. Số lượng khách hàng mới theo tháng (6 tháng gần nhất) - FIXED
     */
    public List<ReportDTO> getNewCustomersByMonth() throws SQLException {
        List<ReportDTO> reports = new ArrayList<>();

        String sql = "SELECT " +
                "    YEAR(c.CreatedAt) as Year, " +
                "    MONTH(c.CreatedAt) as Month, " +
                "    DATE_FORMAT(c.CreatedAt, '%m/%Y') as MonthYear, " +
                "    COUNT(DISTINCT c.CustomerID) as NewCustomers " +
                "FROM Customer c " +
                "WHERE c.CreatedAt >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
                "GROUP BY YEAR(c.CreatedAt), MONTH(c.CreatedAt), DATE_FORMAT(c.CreatedAt, '%m/%Y') " +
                "ORDER BY Year DESC, Month DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ReportDTO report = new ReportDTO();
                report.setYear(rs.getInt("Year"));
                report.setMonth(rs.getInt("Month"));
                report.setMonthYear(rs.getString("MonthYear"));
                report.setCount(rs.getInt("NewCustomers"));
                reports.add(report);
            }
        }
        return reports;
    }
}
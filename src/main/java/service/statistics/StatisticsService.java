package service.statistics;

import dao.statistics.StatisticsDAO;
import model.dto.ReportDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Statistics Service - Simplified
 */
public class StatisticsService {

    private final StatisticsDAO statisticsDAO;

    public StatisticsService() {
        this.statisticsDAO = new StatisticsDAO();
    }

    public StatisticsService(StatisticsDAO statisticsDAO) {
        this.statisticsDAO = statisticsDAO;
    }

    public ReportDTO getKPISummary() throws Exception {
        try {
            return statisticsDAO.getKPISummary();
        } catch (SQLException e) {
            throw new Exception("Error getting KPI summary: " + e.getMessage(), e);
        }
    }

    public int getTotalCustomers() throws Exception {
        try {
            return statisticsDAO.getTotalCustomers();
        } catch (SQLException e) {
            throw new Exception("Error getting customer count: " + e.getMessage(), e);
        }
    }

    public BigDecimal getCollectionRate() throws Exception {
        try {
            return statisticsDAO.getCollectionRate();
        } catch (SQLException e) {
            throw new Exception("Error getting collection rate: " + e.getMessage(), e);
        }
    }

    public BigDecimal getRevenueGrowthRate() throws Exception {
        try {
            return statisticsDAO.getRevenueGrowthRate();
        } catch (SQLException e) {
            throw new Exception("Error getting growth rate: " + e.getMessage(), e);
        }
    }

    public List<ReportDTO> getTopRevenueMonths() throws Exception {
        try {
            return statisticsDAO.getTopRevenueMonths();
        } catch (SQLException e) {
            throw new Exception("Error getting top months: " + e.getMessage(), e);
        }
    }

    public StatisticsSummary getDashboardStatistics() throws Exception {
        StatisticsSummary summary = new StatisticsSummary();

        summary.setKpiSummary(getKPISummary());
        summary.setTotalCustomers(getTotalCustomers());
        summary.setCollectionRate(getCollectionRate());
        summary.setGrowthRate(getRevenueGrowthRate());
        summary.setTopMonths(getTopRevenueMonths());

        return summary;
    }

    public static class StatisticsSummary {
        private ReportDTO kpiSummary;
        private int totalCustomers;
        private BigDecimal collectionRate;
        private BigDecimal growthRate;
        private List<ReportDTO> topMonths;

        public ReportDTO getKpiSummary() { return kpiSummary; }
        public void setKpiSummary(ReportDTO kpiSummary) { this.kpiSummary = kpiSummary; }

        public int getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }

        public BigDecimal getCollectionRate() { return collectionRate; }
        public void setCollectionRate(BigDecimal collectionRate) { this.collectionRate = collectionRate; }

        public BigDecimal getGrowthRate() { return growthRate; }
        public void setGrowthRate(BigDecimal growthRate) { this.growthRate = growthRate; }

        public List<ReportDTO> getTopMonths() { return topMonths; }
        public void setTopMonths(List<ReportDTO> topMonths) { this.topMonths = topMonths; }
    }
}
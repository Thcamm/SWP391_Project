package service.statistics;

import dao.statistics.StatisticsDAO;
import model.dto.ReportDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class StatisticsService {

    private final StatisticsDAO statisticsDAO;

    public StatisticsService() {
        this.statisticsDAO = new StatisticsDAO();
    }

    /**
     * Lấy KPI Summary
     */
    public ReportDTO getKPISummary() throws Exception {
        try {
            return statisticsDAO.getKPISummary();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy KPI: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tổng số khách hàng
     */
    public int getTotalCustomers() throws Exception {
        try {
            return statisticsDAO.getTotalCustomers();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy số lượng khách hàng: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tỷ lệ thanh toán đúng hạn
     */
    public BigDecimal getOnTimePaymentRate() throws Exception {
        try {
            return statisticsDAO.getOnTimePaymentRate();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy tỷ lệ thanh toán: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tốc độ tăng trưởng
     */
    public BigDecimal getRevenueGrowthRate() throws Exception {
        try {
            return statisticsDAO.getRevenueGrowthRate();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy tốc độ tăng trưởng: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy doanh thu theo tuần
     */
    public List<ReportDTO> getRevenueByWeek() throws Exception {
        try {
            return statisticsDAO.getRevenueByWeek();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy doanh thu theo tuần: " + e.getMessage(), e);
        }
    }

    /**
     * So sánh năm trước vs năm nay
     */
    public List<ReportDTO> getYearOverYearComparison() throws Exception {
        try {
            return statisticsDAO.getYearOverYearComparison();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi so sánh năm: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy tỷ lệ thu hồi nợ
     */
    public BigDecimal getCollectionRate() throws Exception {
        try {
            return statisticsDAO.getCollectionRate();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy tỷ lệ thu hồi: " + e.getMessage(), e);
        }
    }

    /**
     * Top tháng doanh thu cao
     */
    public List<ReportDTO> getTopRevenueMonths(int limit) throws Exception {
        try {
            return statisticsDAO.getTopRevenueMonths(limit);
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy top tháng: " + e.getMessage(), e);
        }
    }

    /**
     * Khách hàng mới theo tháng
     */
    public List<ReportDTO> getNewCustomersByMonth() throws Exception {
        try {
            return statisticsDAO.getNewCustomersByMonth();
        } catch (SQLException e) {
            throw new Exception("Lỗi khi lấy khách hàng mới: " + e.getMessage(), e);
        }
    }
}
package dao.carservice;

import common.DbContext;
import model.servicetype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarServiceDAO extends DbContext {

    // Lấy tất cả services
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM Service_Type ORDER BY ServiceID";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                services.add(extractServiceFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all services: " + e.getMessage());
            e.printStackTrace();
        }
        return services;
    }

    // Lấy service theo ID
    public Service getServiceById(int id) {
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM Service_Type WHERE ServiceID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractServiceFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting service by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Thêm service mới
    public boolean addService(Service service) {
        String sql = "INSERT INTO Service_Type (ServiceName, Category, UnitPrice) VALUES (?, ?, ?)";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, service.getServiceName());
            pstmt.setString(2, service.getCategory());
            pstmt.setDouble(3, service.getPrice());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Lấy ID vừa tạo
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        service.setServiceTypeID(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding service: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật service
    public boolean updateService(Service service) {
        String sql = "UPDATE Service_Type SET ServiceName = ?, Category = ?, UnitPrice = ? WHERE ServiceID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, service.getServiceName());
            pstmt.setString(2, service.getCategory());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setInt(4, service.getServiceTypeID());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error updating service: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Xóa service
    public boolean deleteService(int id) {
        String sql = "DELETE FROM Service_Type WHERE ServiceID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting service: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm services với phân trang
    public List<Service> searchServices(String keyword, int limit, int offset) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM Service_Type " +
                "WHERE ServiceName LIKE ? OR Category LIKE ? " +
                "ORDER BY ServiceID LIMIT ? OFFSET ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, "%" + keyword + "%");
            st.setString(2, "%" + keyword + "%");
            st.setInt(3, limit);
            st.setInt(4, offset);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    services.add(extractServiceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching services: " + e.getMessage());
            e.printStackTrace();
        }
        return services;
    }

    // Tìm kiếm services theo keyword (không phân trang)
    public List<Service> searchServicesByKeyword(String keyword) {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM Service_Type " +
                "WHERE ServiceName LIKE ? OR Category LIKE ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractServiceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching services by keyword: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Lấy services theo category
    public List<Service> getAllServicesByCategory(String category) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM Service_Type WHERE Category = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, category);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    services.add(extractServiceFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting services by category: " + e.getMessage());
            e.printStackTrace();
        }
        return services;
    }

    // Lấy tất cả categories
    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT Category FROM Service_Type WHERE Category IS NOT NULL ORDER BY Category";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("Category"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    // Đếm tổng số services
    public int getTotalServicesCount() {
        String sql = "SELECT COUNT(*) as total FROM Service_Type";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total services count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Đếm số services theo keyword
    public int getSearchResultsCount(String keyword) {
        String sql = "SELECT COUNT(*) as total FROM Service_Type WHERE ServiceName LIKE ? OR Category LIKE ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, "%" + keyword + "%");
            st.setString(2, "%" + keyword + "%");

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting search results count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method để extract Service từ ResultSet
    private Service extractServiceFromResultSet(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setServiceTypeID(rs.getInt("ServiceID"));
        service.setServiceName(rs.getString("ServiceName"));
        service.setCategory(rs.getString("Category"));
        service.setPrice(rs.getDouble("UnitPrice"));
        return service;
    }
}
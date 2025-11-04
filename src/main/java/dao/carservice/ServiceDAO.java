package dao.carservice;

import common.DbContext;
import model.servicetype.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO extends DbContext {

    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        // Sửa tên bảng và cột cho đúng với database
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM service_type ORDER BY ServiceID";

        System.out.println("=== DEBUG: getAllServices ===");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Database connected successfully!");

            while (rs.next()) {
                Service service = new Service();
                service.setServiceTypeID(rs.getInt("ServiceID"));
                service.setServiceName(rs.getString("ServiceName"));
                service.setCategory(rs.getString("Category"));
                service.setPrice(rs.getDouble("UnitPrice"));
                services.add(service);

                System.out.println("Loaded: " + service.getServiceName() + " - " + service.getPrice());
            }

            System.out.println("Total services loaded: " + services.size());

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return services;
    }

    // Lấy service type theo ID
    public Service getServiceById(int id) {
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM service_type WHERE ServiceID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Service service = new Service();
                service.setServiceTypeID(rs.getInt("ServiceID"));
                service.setServiceName(rs.getString("ServiceName"));
                service.setCategory(rs.getString("Category"));
                service.setPrice(rs.getDouble("UnitPrice"));
                return service;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Thêm service type mới
    public boolean addService(Service service) {
        String sql = "INSERT INTO service_type (ServiceName, Category, UnitPrice) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
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
            e.printStackTrace();
        }

        return false;
    }

    // Cập nhật service type
    public boolean updateService(Service service) {
        String sql = "UPDATE service_type SET ServiceName = ?, Category = ?, UnitPrice = ? WHERE ServiceID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, service.getServiceName());
            pstmt.setString(2, service.getCategory());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setInt(4, service.getServiceTypeID());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xóa service type
    public boolean deleteService(int id) {
        String sql = "DELETE FROM service_type WHERE ServiceID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
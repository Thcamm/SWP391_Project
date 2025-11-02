package dao.carservice;

import common.DbContext;
import model.servicetype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarServiceDAO extends DbContext {

    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM Service_Type";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                services.add(extractServiceFromResultSet(rs));
            }
        }
        return services;
    }

    public List<Service> searchServices(String keyword, int limit, int offset) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT ServiceID, ServiceName, Category, UnitPrice FROM Service_Type WHERE ServiceName LIKE ? LIMIT ? OFFSET ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "%" + keyword + "%");
            st.setInt(2, limit);
            st.setInt(3, offset);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    services.add(extractServiceFromResultSet(rs));
                }
            }
        }
        return services;
    }



    public List<Service> getAllServicesByCategory(String category) throws SQLException {
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
        }
        return services;
    }

    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT Category FROM Service_Type WHERE Category IS NOT NULL";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("Category"));
            }
        }
        return categories;
    }

    private Service extractServiceFromResultSet(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setServiceTypeID(rs.getInt("ServiceID"));
        service.setServiceName(rs.getString("ServiceName"));
        service.setCategory(rs.getString("Category"));
        service.setPrice(rs.getDouble("UnitPrice"));
        return service;
    }
    public List<Service> searchServicesByKeyword(String keyword) throws SQLException {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT ServiceID, ServiceName, UnitPrice FROM service_type WHERE ServiceName LIKE ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceTypeID(rs.getInt("ServiceID"));
                    s.setServiceName(rs.getString("ServiceName"));
                    s.setPrice(rs.getDouble("UnitPrice"));
                    list.add(s);
                }
            }
        }
        return list;
    }

}
package dao.carservice;

import common.DbContext;
import model.servicetype.Service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CarServiceDAO {

    public void getAllServices() {
        String sql = "SELECT * FROM ServiceType";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            List<Service> services = new ArrayList<>();
            while (rs.next()) {
                Service service = new Service();
                service.setServiceTypeID(rs.getInt("ServiceTypeID"));
                service.setServiceName(rs.getString("ServiceName"));
                service.setCategory(rs.getString("Category"));
                service.setPrice(rs.getDouble("Price"));
                services.add(service);
            }
            // Xử lý danh sách dịch vụ theo nhu cầu của bạn
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách dịch vụ", e);
        }
    }


    public void getAllServicesByCategory(String category) {
        String sql = "SELECT * FROM Service WHERE Category = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setString(1, category);
            ResultSet rs = st.executeQuery();
            List<Service> services = new ArrayList<>();
            while (rs.next()) {
                Service service = new Service();
                service.setServiceTypeID(rs.getInt("ServiceTypeID"));
                service.setServiceName(rs.getString("ServiceName"));
                service.setCategory(rs.getString("Category"));
                service.setPrice(rs.getDouble("Price"));
                services.add(service);
            }
            // Xử lý danh sách dịch vụ theo nhu cầu của bạn
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách dịch vụ theo loại", e);
        }
    }
    public void getAllCategories() {
        String sql = "SELECT DISTINCT Category FROM ServiceType";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            List<String> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(rs.getString("Category"));
            }
            // Xử lý danh sách loại dịch vụ theo nhu cầu của bạn
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách loại dịch vụ", e);
        }
    }
}

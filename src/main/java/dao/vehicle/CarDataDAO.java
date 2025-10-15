package dao.vehicle;

import common.DbContext;
import model.vehicle.CarBrand;
import model.vehicle.CarModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDataDAO extends DbContext {

    public List<CarBrand> getAllBrands() throws SQLException {
        List<CarBrand> list = new ArrayList<>();
        String sql = "SELECT BrandID, BrandName FROM CarBrands ORDER BY BrandName";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                CarBrand brand = new CarBrand();
                brand.setBrandId(rs.getInt("BrandID"));
                brand.setBrandName(rs.getString("BrandName"));
                list.add(brand);
            }
        }
        return list;
    }

    public List<CarModel> getModelsByBrandId(int brandId) throws SQLException {
        List<CarModel> list = new ArrayList<>();
        String sql = "SELECT ModelID, ModelName, BrandID FROM CarModels WHERE BrandID = ? ORDER BY ModelName";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, brandId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    CarModel model = new CarModel();
                    model.setModelId(rs.getInt("ModelID"));
                    model.setModelName(rs.getString("ModelName"));
                    model.setBrandId(rs.getInt("BrandID"));
                    list.add(model);
                }
            }
        }
        return list;
    }
}
//package dao.vehicle;
//
//import common.DbContext;
//import model.vehicle.CarBrand;
//import model.vehicle.CarModel;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class CarDataDAO extends DbContext {
//
//    public List<CarBrand> getAllBrands() throws SQLException {
//        List<CarBrand> list = new ArrayList<>();
//        String sql = "SELECT BrandID, BrandName FROM CarBrands ORDER BY BrandName";
//        try (Connection conn = DbContext.getConnection();
//             PreparedStatement st = conn.prepareStatement(sql);
//             ResultSet rs = st.executeQuery()) {
//            while (rs.next()) {
//                CarBrand brand = new CarBrand();
//                brand.setBrandId(rs.getInt("BrandID"));
//                brand.setBrandName(rs.getString("BrandName"));
//                list.add(brand);
//            }
//        }
//        return list;
//    }
//
//    public List<CarModel> getModelsByBrandId(int brandId) throws SQLException {
//        List<CarModel> list = new ArrayList<>();
//        String sql = "SELECT ModelID, ModelName, BrandID FROM CarModels WHERE BrandID = ? ORDER BY ModelName";
//        try (Connection conn = DbContext.getConnection();
//             PreparedStatement st = conn.prepareStatement(sql)) {
//            st.setInt(1, brandId);
//            try (ResultSet rs = st.executeQuery()) {
//                while (rs.next()) {
//                    CarModel model = new CarModel();
//                    model.setModelId(rs.getInt("ModelID"));
//                    model.setModelName(rs.getString("ModelName"));
//                    model.setBrandId(rs.getInt("BrandID"));
//                    list.add(model);
//                }
//            }
//        }
//        return list;
//    }
//
//    public int getBrandIdByName(String brandName) throws SQLException {
//        String sql = "SELECT BrandID FROM CarBrands WHERE BrandName = ?";
//        try (Connection conn = getConnection();
//             PreparedStatement st = conn.prepareStatement(sql)) {
//            st.setString(1, brandName);
//            ResultSet rs = st.executeQuery();
//            if (rs.next()) return rs.getInt("BrandID");
//        }
//        return -1;
//    }
//
//    public int insertBrandIfNotExist(String brandName) throws SQLException {
//        String sql = "INSERT INTO CarBrands (BrandName) VALUES (?)";
//        try (Connection conn = getConnection();
//             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            st.setString(1, brandName);
//            st.executeUpdate();
//            ResultSet rs = st.getGeneratedKeys();
//            if (rs.next()) return rs.getInt(1);
//        }
//        return getBrandIdByName(brandName); // fallback
//    }
//
//    public int getModelIdByNameAndBrand(String modelName, int brandId) throws SQLException {
//        String sql = "SELECT ModelID FROM CarModels WHERE ModelName = ? AND BrandID = ?";
//        try (Connection conn = getConnection();
//             PreparedStatement st = conn.prepareStatement(sql)) {
//            st.setString(1, modelName);
//            st.setInt(2, brandId);
//            ResultSet rs = st.executeQuery();
//            if (rs.next()) return rs.getInt("ModelID");
//        }
//        return -1;
//    }
//
//    public int insertModelIfNotExist(String modelName, int brandId) throws SQLException {
//        String sql = "INSERT INTO CarModels (ModelName, BrandID) VALUES (?, ?)";
//        try (Connection conn = getConnection();
//             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            st.setString(1, modelName);
//            st.setInt(2, brandId);
//            st.executeUpdate();
//            ResultSet rs = st.getGeneratedKeys();
//            if (rs.next()) return rs.getInt(1);
//        }
//        return getModelIdByNameAndBrand(modelName, brandId);
//    }
//}
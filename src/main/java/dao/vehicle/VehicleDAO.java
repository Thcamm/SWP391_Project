package dao.vehicle;

import common.DbContext;
import model.vehicle.Vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {
    public int getVehicleCountByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Vehicle WHERE CustomerID = ?";
        try (Connection conn = DbContext.getConnection(); // Thay thế bằng cách lấy connection của bạn
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Lấy danh sách xe của khách hàng theo phân trang (limit/offset).
     */
    public List<Vehicle> getVehiclesByCustomerIdPaginated(int customerId, int limit, int offset) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        // Lưu ý: Cú pháp LIMIT/OFFSET có thể khác nhau giữa các CSDL (Đây là cho MySQL/PostgreSQL)
        // Nếu dùng SQL Server, bạn sẽ cần dùng OFFSET/FETCH
        String sql = "SELECT * FROM Vehicle WHERE CustomerID = ? LIMIT ? OFFSET ?";

        try (Connection conn = DbContext.getConnection(); // Thay thế bằng cách lấy connection của bạn
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Giả sử bạn có một hàm helper để map ResultSet
                    // hoặc bạn map trực tiếp ở đây
                    Vehicle vehicle = new Vehicle();
                    vehicle.setVehicleID(rs.getInt("VehicleID"));
                    vehicle.setLicensePlate(rs.getString("LicensePlate"));
                    vehicle.setYearManufacture(rs.getInt("YearManufacture"));
                    vehicle.setBrand(rs.getString("Brand"));
                    vehicle.setModel(rs.getString("Model"));
                    vehicle.setCustomerID(rs.getInt("CustomerID"));
                    vehicles.add(vehicle);
                }
            }
        }
        return vehicles;
    }
    public List<Vehicle> getVehiclesByCustomerId(int customerId) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT VehicleID, CustomerID, LicensePlate, Brand, Model, YearManufacture FROM Vehicle WHERE CustomerID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, customerId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return list;
    }

    public int insertVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicle (CustomerID, LicensePlate, Brand, Model, YearManufacture) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, vehicle.getCustomerID());
            st.setString(2, vehicle.getLicensePlate());
            st.setString(3, vehicle.getBrand());
            st.setString(4, vehicle.getModel());
            st.setInt(5, vehicle.getYearManufacture());

            if (st.executeUpdate() > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public boolean checkLicensePlateExists(String licensePlate) throws SQLException {
        String sql = "SELECT 1 FROM vehicle WHERE LicensePlate = ? LIMIT 1";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, licensePlate);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean checkLicensePlateExists(String licensePlate, int vehicleIdToExclude) throws SQLException {
        String sql = "SELECT 1 FROM vehicle WHERE LicensePlate = ? AND VehicleID != ? LIMIT 1";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, licensePlate);
            st.setInt(2, vehicleIdToExclude);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        }
    }

//    public int getVehicleIdByLicensePlate(String licensePlate) {
//        String sql = "SELECT VehicleID FROM vehicle WHERE LicensePlate = ?";
//        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
//            st.setString(1, licensePlate);
//            ResultSet rs = st.executeQuery();
//            if (rs.next()) {
//                int vehicleID = rs.getInt("VehicleID");
//                return vehicleID;
//            }
//            return -1;
//        } catch (SQLException e) {
//            throw new RuntimeException("Lỗi khi lấy VehicleID theo biển số xe", e);
//        }
//    }

    private Vehicle extractVehicleFromResultSet(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleID(rs.getInt("VehicleID"));
        v.setCustomerID(rs.getInt("CustomerID"));
        v.setLicensePlate(rs.getString("LicensePlate"));
        v.setBrand(rs.getString("Brand"));
        v.setModel(rs.getString("Model"));
        v.setYearManufacture(rs.getInt("YearManufacture"));
        return v;
    }

    public Vehicle getVehicleById(int vehicleId) throws SQLException {
        String sql = "SELECT VehicleID, CustomerID, LicensePlate, Brand, Model, YearManufacture FROM Vehicle WHERE VehicleID = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, vehicleId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return extractVehicleFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public boolean updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE Vehicle SET Brand = ?, Model = ?, YearManufacture = ?, LicensePlate = ? WHERE VehicleID = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, vehicle.getBrand());
            st.setString(2, vehicle.getModel());
            st.setInt(3, vehicle.getYearManufacture());
            st.setString(4, vehicle.getLicensePlate());
            st.setInt(5, vehicle.getVehicleID());

            return st.executeUpdate() > 0;
        }
    }
    public boolean updateVehicleForCustomer(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE Vehicle SET CustomerID = ?, Brand = ?, Model = ?, " +
                "YearManufacture = ?, LicensePlate = ? WHERE VehicleID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, vehicle.getCustomerID());
            ps.setString(2, vehicle.getBrand());
            ps.setString(3, vehicle.getModel());
            ps.setInt(4, vehicle.getYearManufacture());
            ps.setString(5, vehicle.getLicensePlate());
            ps.setInt(6, vehicle.getVehicleID());

            int rowsAffected = ps.executeUpdate();

            System.out.println("✅ Updated vehicle ID " + vehicle.getVehicleID() +
                    " - Rows affected: " + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating vehicle: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    public boolean deleteVehicle(int vehicleId, int customerId) throws SQLException {
        String sql = "DELETE FROM Vehicle WHERE VehicleID = ? AND CustomerID = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, vehicleId);
            st.setInt(2, customerId);

            return st.executeUpdate() > 0;
        }
    }
    public List<String> getAllLicensePlates() throws SQLException {
        List<String> plates = new ArrayList<>();
        String sql = "SELECT LicensePlate FROM Vehicle";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                plates.add(rs.getString("LicensePlate"));
            }
        }
        return plates;
    }
    public int getVehicleIdByLicensePlate(String licensePlate) throws SQLException {
        String sql = "SELECT VehicleID FROM vehicle WHERE LicensePlate = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, licensePlate);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt("VehicleID");
            }
        }
        return -1;
    }
    public List<String> getAllBrands() throws SQLException {
        String sql = "SELECT DISTINCT Brand FROM Vehicle ORDER BY Brand";
        List<String> brands = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                brands.add(rs.getString("Brand"));
            }
        }
        return brands;
    }

    public int countVehicles(int customerId, String keyword, String brandFilter) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Vehicle WHERE CustomerID = ?");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (Brand LIKE ? OR Model LIKE ? OR LicensePlate LIKE ?)");
        }
        if (brandFilter != null && !brandFilter.isEmpty()) {
            sql.append(" AND Brand = ?");
        }

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, customerId);
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (brandFilter != null && !brandFilter.isEmpty()) {
                ps.setString(idx++, brandFilter);
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    public List<Vehicle> searchVehicles(int customerId, String keyword, String brandFilter, int page, int pageSize) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM Vehicle WHERE CustomerID = ?");
        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (Brand LIKE ? OR Model LIKE ? OR LicensePlate LIKE ?)");
        }
        if (brandFilter != null && !brandFilter.isEmpty()) {
            sql.append(" AND Brand = ?");
        }
        sql.append(" ORDER BY VehicleID DESC LIMIT ? OFFSET ?");

        List<Vehicle> list = new ArrayList<>();
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, customerId);
            if (keyword != null && !keyword.isEmpty()) {
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
                ps.setString(idx++, "%" + keyword + "%");
            }
            if (brandFilter != null && !brandFilter.isEmpty()) {
                ps.setString(idx++, brandFilter);
            }
            ps.setInt(idx++, pageSize);
            ps.setInt(idx, (page - 1) * pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleID(rs.getInt("VehicleID"));
                v.setBrand(rs.getString("Brand"));
                v.setModel(rs.getString("Model"));
                v.setYearManufacture(rs.getInt("YearManufacture"));
                v.setLicensePlate(rs.getString("LicensePlate"));
                list.add(v);
            }
        }
        return list;
    }
    public List<Vehicle> searchVehiclesByPlate(int customerId, String licensePlate) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle WHERE CustomerID = ? AND LicensePlate LIKE ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, "%" + licensePlate + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Vehicle v = new Vehicle();
                    v.setVehicleID(rs.getInt("VehicleID"));
                    v.setCustomerID(rs.getInt("CustomerID"));
                    v.setBrand(rs.getString("Brand"));
                    v.setModel(rs.getString("Model"));
                    v.setLicensePlate(rs.getString("LicensePlate"));
                    vehicles.add(v);
                }
            }
        }
        return vehicles;
    }

//    public List<String> getAllLicensePlates() throws SQLException {
//        List<String> plates = new ArrayList<>();
//        String sql = "SELECT LicensePlate FROM Vehicle";
//        try (Connection conn = DbContext.getConnection();
//             PreparedStatement st = conn.prepareStatement(sql);
//             ResultSet rs = st.executeQuery()) {
//            while (rs.next()) {
//                plates.add(rs.getString("LicensePlate"));
//            }
//        }
//        return plates;
//    }








    public List<Vehicle> searchVehicles(String keyword, int customerId, int limit, int offset) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM Vehicle " +
                "WHERE customerID = ? AND (licensePlate LIKE ? OR brand LIKE ? OR model LIKE ?) " +
                "LIMIT ? OFFSET ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setInt(1, customerId);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            ps.setInt(5, limit);
            ps.setInt(6, offset);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleID(rs.getInt("vehicleID"));
                v.setLicensePlate(rs.getString("licensePlate"));
                v.setBrand(rs.getString("brand"));
                v.setModel(rs.getString("model"));
                v.setYearManufacture(rs.getInt("yearManufacture"));
                list.add(v);
            }
        }
        return list;
    }
    public List<Vehicle> searchVehiclesByCustomerAndKeyword(int customerId, String keyword) throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT VehicleID, Brand, Model, LicensePlate FROM Vehicle "
                + "WHERE CustomerID=? AND (Brand LIKE ? OR Model LIKE ? OR LicensePlate LIKE ?)";
        try(Connection conn = DbContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            String k = "%" + keyword + "%";
            ps.setString(2, k);
            ps.setString(3, k);
            ps.setString(4, k);

            try(ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    Vehicle v = new Vehicle();
                    v.setVehicleID(rs.getInt("VehicleID"));
                    v.setBrand(rs.getString("Brand"));
                    v.setModel(rs.getString("Model"));
                    v.setLicensePlate(rs.getString("LicensePlate"));
                    list.add(v);
                }
            }
        }
        return list;
    }


}
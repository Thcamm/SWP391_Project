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

public class VehicleDAO extends DbContext {

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

    public int getVehicleIdByLicensePlate(String licensePlate) {
        String sql = "SELECT VehicleID FROM vehicle WHERE LicensePlate = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setString(1, licensePlate);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int vehicleID = rs.getInt("VehicleID");
                // Xử lý vehicleID theo nhu cầu của bạn
                return vehicleID;
            }
            return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy VehicleID theo biển số xe", e);
        }
    }
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
    public boolean deleteVehicle(int vehicleId, int customerId) throws SQLException {
        String sql = "DELETE FROM Vehicle WHERE VehicleID = ? AND CustomerID = ?";
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, vehicleId);
            st.setInt(2, customerId);

            return st.executeUpdate() > 0;
        }
    }
}
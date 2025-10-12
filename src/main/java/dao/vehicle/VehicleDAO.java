package dao.vehicle;

import common.DbContext;
import model.vehicle.Vehicle;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class VehicleDAO extends DbContext {
    public List<Vehicle> getVehiclesByCustomerId(int customerId) {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicle WHERE CustomerID = ?";

        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {

            st.setInt(1, customerId);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Vehicle v = new Vehicle();
                v.setVehicleID(rs.getInt("VehicleID"));
                v.setCustomerID(rs.getInt("CustomerID"));
                v.setLicensePlate(rs.getString("LicensePlate"));
                v.setBrand(rs.getString("Brand"));
                v.setModel(rs.getString("Model"));
                v.setYearManufacture(rs.getInt("YearManufacture"));
                list.add(v);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
    public boolean insertVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (CustomerID, LicensePlate, Brand, Model, YearManufacture) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setInt(1, vehicle.getCustomerID());
            st.setString(2, vehicle.getLicensePlate());
            st.setString(3, vehicle.getBrand());
            st.setString(4, vehicle.getModel());
            st.setInt(5, vehicle.getYearManufacture());

            int rowsAffected = st.executeUpdate();
            return rowsAffected > 0; // Trả về true nếu chèn thành công
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi chèn xe mới", e);
        }
    }
    public int getAllVehiclesCount() {
        String sql = "SELECT COUNT(*) FROM vehicle";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1); // Trả về số lượng xe
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm số lượng xe", e);
        }
        return 0; // Trả về 0 nếu có lỗi hoặc không có xe
    }
    public boolean checkLicensePlateExists(String licensePlate) {
        String sql = "SELECT COUNT(*) FROM vehicle WHERE LicensePlate = ?";
        try (PreparedStatement st = DbContext.getConnection().prepareStatement(sql)) {
            st.setString(1, licensePlate);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Trả về true nếu tồn tại
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra biển số xe", e);
        }
        return false; // Trả về false nếu không tồn tại
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
            } return -1;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy VehicleID theo biển số xe", e);
        }
    }
}

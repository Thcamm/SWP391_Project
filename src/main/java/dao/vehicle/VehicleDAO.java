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
}

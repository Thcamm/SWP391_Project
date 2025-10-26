package service.vehicle;

import dao.vehicle.VehicleDAO;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class VehicleService {

    private final VehicleDAO vehicleDAO;

    public VehicleService(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    public boolean validateLicensePlateFormat(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }
        String regex = "^(?=.*[A-Z])(?=.*\\d)[A-Z0-9- ]{4,15}$";
        return Pattern.matches(regex, licensePlate.toUpperCase().replaceAll("\\.", ""));
    }

    public boolean isLicensePlateTaken(String licensePlate, int currentVehicleId) throws SQLException {
        return vehicleDAO.checkLicensePlateExists(licensePlate, currentVehicleId);
    }
}
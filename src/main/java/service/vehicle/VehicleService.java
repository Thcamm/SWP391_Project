package service.vehicle;

import dao.vehicle.VehicleDAO;

import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.List;

public class VehicleService {

    private final VehicleDAO vehicleDAO;

    public VehicleService(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    /**
     * Chuẩn hoá: bỏ mọi ký tự không phải chữ hoặc số, uppercase.
     * Ví dụ: "36A.36363" -> "36A36363", "36A-36363" -> "36A36363"
     */
    public String normalizeLicensePlate(String licensePlate) {
        if (licensePlate == null) return null;
        return licensePlate.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    /**
     * Validate format sơ bộ cho biển VN (tùy ông bạn muốn mở rộng).
     * Đây là validate đơn giản: có chữ và số, chiều dài hợp lý.
     */
    public boolean validateLicensePlateFormat(String licensePlate) {
        if (licensePlate == null) return false;
        String normalized = normalizeLicensePlate(licensePlate);
        // đảm bảo có ít nhất 3 ký tự, ít nhất 1 chữ và 1 số
        if (normalized.length() < 4 || normalized.length() > 12) return false;
        boolean hasLetter = Pattern.compile("[A-Z]").matcher(normalized).find();
        boolean hasDigit = Pattern.compile("\\d").matcher(normalized).find();
        return hasLetter && hasDigit;
    }

    /**
     * Kiểm tra biển đã tồn tại chưa, loại trừ vehicleId hiện tại (nếu update).
     * vehicleIdToExclude = -1 hoặc 0 => không loại trừ.
     */
    public boolean isLicensePlateTaken(String licensePlate, int vehicleIdToExclude) throws SQLException {
        String norm = normalizeLicensePlate(licensePlate);
        List<String> all = vehicleDAO.getAllLicensePlates();
        for (String dbPlate : all) {
            if (norm.equals(normalizeLicensePlate(dbPlate))) {
                // nếu trùng, lấy id của biển trong db
                int foundId = vehicleDAO.getVehicleIdByLicensePlate(dbPlate);
                if (vehicleIdToExclude <= 0) return true;
                if (foundId != vehicleIdToExclude) return true;
            }
        }
        return false;
    }
}

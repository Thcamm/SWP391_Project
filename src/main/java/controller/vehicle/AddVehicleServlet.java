package controller.vehicle;

import dao.vehicle.VehicleDAO;
import model.customer.Customer;
import model.vehicle.Vehicle;
import service.vehicle.VehicleService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AddVehicleServlet", urlPatterns = {"/customer/addVehicle"})
public class AddVehicleServlet extends HttpServlet {

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final VehicleService vehicleService = new VehicleService(vehicleDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // JSP sẽ tự fetch brand/model qua API
        request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        String contextPath = request.getContextPath();

        if (customer == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // ====== FIX: Lấy đúng tên parameter từ form ======
        String brand = request.getParameter("brand");           // ✅ ĐÚNG
        String model = request.getParameter("model");           // ✅ ĐÚNG
        String yearStr = request.getParameter("year");          // ✅ ĐÚNG
        String licensePlate = request.getParameter("licensePlate");

        // ====== DEBUG: In ra console để kiểm tra ======
        System.out.println("=== DEBUG AddVehicle ===");
        System.out.println("Brand: " + brand);
        System.out.println("Model: " + model);
        System.out.println("Year: " + yearStr);
        System.out.println("License Plate: " + licensePlate);
        System.out.println("========================");

        // ====== Validate: Kiểm tra null/empty ======
        if (brand == null || brand.trim().isEmpty()) {
            request.setAttribute("error", "Brand is required.");
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            return;
        }

        if (model == null || model.trim().isEmpty()) {
            request.setAttribute("error", "Model is required.");
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            return;
        }

        if (yearStr == null || yearStr.trim().isEmpty()) {
            request.setAttribute("error", "Year is required.");
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            return;
        }

        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            request.setAttribute("error", "License plate is required.");
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            return;
        }

        // ====== BƯỚC 1: Chuẩn hóa biển số để so sánh và lưu nhất quán ======
        String normalizedPlate = normalizePlate(licensePlate);

        // ====== BƯỚC 2: Validate format cơ bản ======
        if (!vehicleService.validateLicensePlateFormat(licensePlate)) {
            request.setAttribute("error", "Invalid license plate format.");
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            return;
        }

        // ====== BƯỚC 3: Kiểm tra trùng biển số (kể cả dạng khác nhau) ======
        try {
            if (vehicleService.isLicensePlateTaken(normalizedPlate, -1)) {
                request.setAttribute("error", "This license plate already exists in the system.");
                request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error while checking license plate.");
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            return;
        }

        // ====== BƯỚC 4: Kiểm tra năm sản xuất ======
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Year must be a valid number.");
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            return;
        }

        int currentYear = java.time.Year.now().getValue();
        if (year < 1900 || year > currentYear + 1) {
            request.setAttribute("error", "Invalid year of manufacture.");
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            return;
        }

        // ====== BƯỚC 5: Lưu vào database ======
        Vehicle v = new Vehicle();
        v.setCustomerID(customer.getCustomerId());
        v.setBrand(brand.trim());
        v.setModel(model.trim());
        v.setYearManufacture(year);
        v.setLicensePlate(normalizedPlate); // Lưu bản chuẩn hóa

        try {
            // insertVehicle() trả về vehicleId (int), không phải boolean
            int newVehicleId = vehicleDAO.insertVehicle(v);

            if (newVehicleId > 0) {
                System.out.println("✅ Vehicle saved successfully with ID: " + newVehicleId);
                session.setAttribute("success", "New vehicle added successfully!");
                response.sendRedirect(contextPath + "/customer/garage");
            } else {
                System.out.println("❌ Failed to save vehicle! (returned ID: " + newVehicleId + ")");
                request.setAttribute("error", "Failed to save vehicle. Please try again.");
                request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ SQL Exception: " + e.getMessage());
            request.setAttribute("error", "Database error while saving vehicle: " + e.getMessage());
            request.getRequestDispatcher("/view/customer/addVehicle.jsp").forward(request, response);
        }
    }

    // ====== HÀM CHUẨN HÓA BIỂN SỐ ======
    private String normalizePlate(String plate) {
        if (plate == null) return "";
        return plate.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        // Xóa mọi ký tự không phải chữ/số, viết hoa hết => 36A.36363 -> 36A36363
    }
}
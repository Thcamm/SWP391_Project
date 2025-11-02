package controller.employee.customerservice;

import dao.vehicle.CarDataDAO;
import dao.vehicle.VehicleDAO;
import model.vehicle.CarBrand;
import model.vehicle.CarModel;
import model.vehicle.Vehicle;
import service.vehicle.VehicleService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/customerservice/addVehicle"})
public class AddVehicle extends HttpServlet {

    private final CarDataDAO carDAO = new CarDataDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final VehicleService vehicleService = new VehicleService(vehicleDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String action = request.getParameter("action");
        if ("getModels".equals(action)) {
            handleGetModels(request, response);
            return;
        }

        // Mặc định: load brands cho modal
        response.setContentType("text/html;charset=UTF-8");
        try {
            List<CarBrand> brands = carDAO.getAllBrands();
            request.setAttribute("brands", brands);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải dữ liệu hãng xe.");
        }
        request.getRequestDispatcher("/view/customerservice/add-vehicle.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String action = request.getParameter("action");

        // CRITICAL: Phải check action === "saveVehicle" để trả JSON
        if (action == null || action.isEmpty() || "saveVehicle".equals(action)) {
            handleSaveVehicle(request, response);
            return;
        }

        // Fallback: trả JSON error
        PrintWriter out = response.getWriter();
        out.write("{\"success\":false,\"message\":\"Invalid action\"}");

    }

    private void handleGetModels(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String brandIdStr = request.getParameter("brandId");
            if (brandIdStr == null || brandIdStr.isEmpty()) {
                out.write("[]");
                return;
            }

            int brandId = Integer.parseInt(brandIdStr);
            List<CarModel> models = carDAO.getModelsByBrandId(brandId);

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < models.size(); i++) {
                CarModel m = models.get(i);
                sb.append("{\"id\":").append(m.getModelId())
                        .append(",\"name\":\"").append(escapeJson(m.getModelName())).append("\"}");
                if (i < models.size() - 1) sb.append(",");
            }
            sb.append("]");
            out.write(sb.toString());

        } catch (NumberFormatException e) {
            out.write("[]");
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Database error\"}");
        }
    }
    private void handleSaveVehicle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // === DEBUG: Log request info ===
            System.out.println("=== handleSaveVehicle called ===");
            System.out.println("Content-Type: " + request.getContentType());
            System.out.println("Content-Length: " + request.getContentLength());
            System.out.println("Method: " + request.getMethod());

            // Debug: Log ALL parameters
            System.out.println("=== All Parameters ===");
            java.util.Map<String, String[]> paramMap = request.getParameterMap();
            System.out.println("Parameter Map Size: " + paramMap.size());

            for (java.util.Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                System.out.println(entry.getKey() + ": " + java.util.Arrays.toString(entry.getValue()));
            }

            // Lấy parameters
            String customerIdStr = request.getParameter("customerId");
            String brandIdStr = request.getParameter("brandId");
            String modelName = request.getParameter("modelName");
            String yearStr = request.getParameter("yearManufacture");
            String licensePlate = request.getParameter("licensePlate");

            // Debug individual values
            System.out.println("=== Individual Parameter Values ===");
            System.out.println("customerId: [" + customerIdStr + "]");
            System.out.println("brandId: [" + brandIdStr + "]");
            System.out.println("modelName: [" + modelName + "]");
            System.out.println("year: [" + yearStr + "]");
            System.out.println("licensePlate: [" + licensePlate + "]");

            // Validate parameters
            List<String> missingFields = new ArrayList<>();

            if (customerIdStr == null || customerIdStr.trim().isEmpty()) missingFields.add("customerId");
            if (brandIdStr == null || brandIdStr.trim().isEmpty()) missingFields.add("brandId");
            if (modelName == null || modelName.trim().isEmpty()) missingFields.add("modelName");
            if (yearStr == null || yearStr.trim().isEmpty()) missingFields.add("year");
            if (licensePlate == null || licensePlate.trim().isEmpty()) missingFields.add("licensePlate");

            if (!missingFields.isEmpty()) {
                String errorMsg = "{\"success\":false,\"message\":\"Thiếu thông tin: " + String.join(", ", missingFields) + "\"}";
                System.err.println("❌ Validation failed: " + errorMsg);
                out.write(errorMsg);
                out.flush();
                return;
            }

            int customerId = Integer.parseInt(customerIdStr);

            // Validate license plate format
            if (!vehicleService.validateLicensePlateFormat(licensePlate)) {
                String errorMsg = "{\"success\":false,\"message\":\"Biển số không hợp lệ\"}";
                System.err.println("❌ Invalid license plate: " + errorMsg);
                out.write(errorMsg);
                out.flush();
                return;
            }

            // Check if license plate already exists
            if (vehicleService.isLicensePlateTaken(licensePlate, 0)) {
                String errorMsg = "{\"success\":false,\"message\":\"Biển số đã tồn tại\"}";
                System.err.println("❌ License plate taken: " + errorMsg);
                out.write(errorMsg);
                out.flush();
                return;
            }

            // Validate year
            int year;
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                String errorMsg = "{\"success\":false,\"message\":\"Năm sản xuất không hợp lệ\"}";
                System.err.println("❌ Invalid year: " + errorMsg);
                out.write(errorMsg);
                out.flush();
                return;
            }

            int currentYear = java.time.Year.now().getValue();
            if (year < 2000 || year > currentYear) {
                String errorMsg = "{\"success\":false,\"message\":\"Năm sản xuất phải từ 2000 đến " + currentYear + "\"}";
                System.err.println("❌ Year out of range: " + errorMsg);
                out.write(errorMsg);
                out.flush();
                return;
            }

            // Get brand name
            int brandId = Integer.parseInt(brandIdStr);
            CarBrand brand = carDAO.getBrandById(brandId);
            if (brand == null) {
                String errorMsg = "{\"success\":false,\"message\":\"Hãng xe không tồn tại\"}";
                System.err.println("❌ Brand not found: " + errorMsg);
                out.write(errorMsg);
                out.flush();
                return;
            }
            String brandName = brand.getBrandName();

            System.out.println("✓ All validations passed, creating vehicle...");

            // Create new vehicle
            Vehicle newVehicle = new Vehicle();
            newVehicle.setCustomerID(customerId);
            newVehicle.setBrand(brandName);
            newVehicle.setModel(modelName);
            newVehicle.setYearManufacture(year);
            newVehicle.setLicensePlate(licensePlate);

            int newId = vehicleDAO.insertVehicle(newVehicle);

            System.out.println("✓ Vehicle created with ID: " + newId);

            // Return success JSON
            String json = "{"
                    + "\"success\":true,"
                    + "\"vehicleId\":" + newId + ","
                    + "\"brand\":\"" + escapeJson(brandName) + "\","
                    + "\"model\":\"" + escapeJson(modelName) + "\","
                    + "\"licensePlate\":\"" + escapeJson(licensePlate) + "\""
                    + "}";

            System.out.println("=== Sending Response ===");
            System.out.println(json);

            out.write(json);
            out.flush();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ SQL Exception: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorMsg = "{\"success\":false,\"message\":\"Lỗi cơ sở dữ liệu: " + escapeJson(e.getMessage()) + "\"}";
            out.write(errorMsg);
            out.flush();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.err.println("❌ Number Format Exception: " + e.getMessage());
            String errorMsg = "{\"success\":false,\"message\":\"Dữ liệu không hợp lệ\"}";
            out.write(errorMsg);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Unexpected Exception: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String errorMsg = "{\"success\":false,\"message\":\"Lỗi không xác định: " + escapeJson(e.getMessage()) + "\"}";
            out.write(errorMsg);
            out.flush();
        }
    }

    // Helper method to escape JSON strings
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
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

        String action = request.getParameter("action");

        if ("getModels".equals(action)) {
            handleGetModels(request, response);
            return;
        }

        // Default: load brands for modal
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

        String action = request.getParameter("action");

        if ("saveVehicle".equals(action)) {
            handleSaveVehicle(request, response);
            return;
        }

        // Invalid action
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"success\":false,\"message\":\"Invalid action\"}");
    }

    /**
     * Handle get models by brand ID
     */
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

            // Build JSON array
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < models.size(); i++) {
                CarModel model = models.get(i);
                json.append("{")
                        .append("\"id\":").append(model.getModelId()).append(",")
                        .append("\"name\":\"").append(escapeJson(model.getModelName())).append("\"")
                        .append("}");

                if (i < models.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");

            out.write(json.toString());

            System.out.println("✅ Loaded " + models.size() + " models for brand " + brandId);

        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid brandId format");
            out.write("[]");

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Database error\"}");
        } finally {
            out.close();
        }
    }

    /**
     * Handle save new vehicle
     */
    private void handleSaveVehicle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Set response type FIRST
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String jsonResponse = null;

        try {
            // Get parameters
            String customerIdStr = request.getParameter("customerId");
            String brandIdStr = request.getParameter("brandId");
            String modelName = request.getParameter("modelName");
            String yearStr = request.getParameter("yearManufacture");
            String licensePlate = request.getParameter("licensePlate");

            System.out.println("=== Add Vehicle Request ===");
            System.out.println("customerId: " + customerIdStr);
            System.out.println("brandId: " + brandIdStr);
            System.out.println("modelName: " + modelName);
            System.out.println("year: " + yearStr);
            System.out.println("licensePlate: " + licensePlate);

            // Validation
            List<String> missingFields = new ArrayList<>();
            if (isEmpty(customerIdStr)) missingFields.add("customerId");
            if (isEmpty(brandIdStr)) missingFields.add("brandId");
            if (isEmpty(modelName)) missingFields.add("modelName");
            if (isEmpty(yearStr)) missingFields.add("year");
            if (isEmpty(licensePlate)) missingFields.add("licensePlate");

            if (!missingFields.isEmpty()) {
                jsonResponse = buildErrorJson("Thiếu thông tin: " + String.join(", ", missingFields));
                System.err.println("❌ Validation failed: missing fields");
                out.write(jsonResponse);
                return;
            }

            // Parse integers
            int customerId = Integer.parseInt(customerIdStr);
            int brandId = Integer.parseInt(brandIdStr);
            int year = Integer.parseInt(yearStr);

            // Validate license plate format
            if (!vehicleService.validateLicensePlateFormat(licensePlate)) {
                jsonResponse = buildErrorJson("Biển số không hợp lệ");
                System.err.println("❌ Invalid license plate format");
                out.write(jsonResponse);
                return;
            }

            // Check if license plate exists
            if (vehicleService.isLicensePlateTaken(licensePlate, 0)) {
                jsonResponse = buildErrorJson("Biển số đã tồn tại");
                System.err.println("❌ License plate already exists");
                out.write(jsonResponse);
                return;
            }

            // Validate year
            int currentYear = java.time.Year.now().getValue();
            if (year < 2000 || year > currentYear) {
                jsonResponse = buildErrorJson("Năm sản xuất phải từ 2000 đến " + currentYear);
                System.err.println("❌ Year out of range");
                out.write(jsonResponse);
                return;
            }

            // Get brand name
            CarBrand brand = carDAO.getBrandById(brandId);
            if (brand == null) {
                jsonResponse = buildErrorJson("Hãng xe không tồn tại");
                System.err.println("❌ Brand not found");
                out.write(jsonResponse);
                return;
            }

            String brandName = brand.getBrandName();

            // Create vehicle object
            Vehicle newVehicle = new Vehicle();
            newVehicle.setCustomerID(customerId);
            newVehicle.setBrand(brandName);
            newVehicle.setModel(modelName);
            newVehicle.setYearManufacture(year);
            newVehicle.setLicensePlate(licensePlate.toUpperCase());

            // Insert into database
            int newVehicleId = vehicleDAO.insertVehicle(newVehicle);

            if (newVehicleId <= 0) {
                jsonResponse = buildErrorJson("Không thể thêm xe vào database");
                System.err.println("❌ Failed to insert vehicle");
                out.write(jsonResponse);
                return;
            }

            // Build success response
            jsonResponse = buildSuccessJson(
                    newVehicleId,
                    brandName,
                    modelName,
                    licensePlate.toUpperCase(),
                    year
            );

            System.out.println("✅ Vehicle added successfully with ID: " + newVehicleId);
            System.out.println("Response: " + jsonResponse);

            out.write(jsonResponse);

        } catch (NumberFormatException e) {
            System.err.println("❌ Number format error: " + e.getMessage());
            e.printStackTrace();
            jsonResponse = buildErrorJson("Dữ liệu không hợp lệ");
            out.write(jsonResponse);

        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse = buildErrorJson("Lỗi cơ sở dữ liệu: " + e.getMessage());
            out.write(jsonResponse);

        } catch (Exception e) {
            System.err.println("❌ Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse = buildErrorJson("Lỗi không xác định: " + e.getMessage());
            out.write(jsonResponse);

        } finally {
            // CRITICAL: Always close PrintWriter
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Check if string is null or empty
     */
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Build error JSON response
     */
    private String buildErrorJson(String message) {
        return "{\"success\":false,\"message\":\"" + escapeJson(message) + "\"}";
    }

    /**
     * Build success JSON response
     */
    private String buildSuccessJson(int vehicleId, String brand, String model, String licensePlate, int year) {
        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"success\":true,")
                .append("\"vehicleId\":").append(vehicleId).append(",")
                .append("\"brand\":\"").append(escapeJson(brand)).append("\",")
                .append("\"model\":\"").append(escapeJson(model)).append("\",")
                .append("\"licensePlate\":\"").append(escapeJson(licensePlate)).append("\",")
                .append("\"year\":").append(year)
                .append("}");
        return json.toString();
    }

    /**
     * Escape JSON string
     */
    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
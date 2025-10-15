package controller.vehicle;

import dao.vehicle.CarDataDAO;
import dao.vehicle.VehicleDAO;
import model.customer.Customer;
import model.vehicle.CarBrand;
import model.vehicle.CarModel;
import model.vehicle.Vehicle;
import service.vehicle.VehicleService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AddVehicleServlet", urlPatterns = {"/customer/addVehicle"})
public class AddVehicleServlet extends HttpServlet {

    private final CarDataDAO carDAO = new CarDataDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<CarBrand> brands = carDAO.getAllBrands();
            request.setAttribute("brands", brands);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load car brands data.");
        }
        request.getRequestDispatcher("/view/vehicle/addVehicle.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        try {
            if ("selectModel".equals(action)) {
                handleSelectModel(request, response);
            } else {
                handleSaveVehicle(request, response);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred. Please try again.");
            request.getRequestDispatcher("/view/vehicle/addVehicle.jsp").forward(request, response);
        }
    }

    private void handleSelectModel(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        int brandId = Integer.parseInt(request.getParameter("brandId"));
        List<CarBrand> brands = carDAO.getAllBrands();
        List<CarModel> models = carDAO.getModelsByBrandId(brandId);

        request.setAttribute("brands", brands);
        request.setAttribute("models", models);
        request.setAttribute("selectedBrandId", brandId);
        brands.stream()
                .filter(b -> b.getBrandId() == brandId)
                .findFirst()
                .ifPresent(b -> request.setAttribute("selectedBrandName", b.getBrandName()));

        request.getRequestDispatcher("/view/vehicle/addVehicle.jsp").forward(request, response);
    }

    private void handleSaveVehicle(HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String brandName = request.getParameter("brandName");
        String modelName = request.getParameter("modelName");
        String yearStr = request.getParameter("yearManufacture");
        String licensePlate = request.getParameter("licensePlate");

        if (!vehicleService.validateLicensePlateFormat(licensePlate)) {
            request.setAttribute("error", "Invalid license plate format.");
            repopulateFormOnError(request, response, brandName);
            return;
        }

        if (vehicleService.isLicensePlateTaken(licensePlate, 0)) {
            request.setAttribute("error", "This license plate already exists.");
            repopulateFormOnError(request, response, brandName);
            return;
        }

        int year = 0;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Year must be a valid number.");
            repopulateFormOnError(request, response, brandName);
            return;
        }

        int currentYear = java.time.Year.now().getValue();
        if (year < 2000 || year > currentYear) {
            request.setAttribute("error", "Year of manufacture must be between 2000 and " + currentYear + ".");
            repopulateFormOnError(request, response, brandName);
            return;
        }

        Vehicle newVehicle = new Vehicle();
        newVehicle.setCustomerID(customer.getCustomerId());
        newVehicle.setBrand(brandName);
        newVehicle.setModel(modelName);
        newVehicle.setYearManufacture(year);
        newVehicle.setLicensePlate(licensePlate);

        vehicleDAO.insertVehicle(newVehicle);
        session.setAttribute("success", "New vehicle added successfully!");
        response.sendRedirect(contextPath + "/customer/garage");
    }

    private void repopulateFormOnError(HttpServletRequest request, HttpServletResponse response, String brandName) throws SQLException, ServletException, IOException {
        List<CarBrand> brands = carDAO.getAllBrands();
        request.setAttribute("brands", brands);

        if (brandName != null && !brandName.isEmpty()) {
            int brandId = brands.stream()
                    .filter(b -> b.getBrandName().equals(brandName))
                    .findFirst().map(CarBrand::getBrandId).orElse(0);
            if (brandId > 0) {
                List<CarModel> models = carDAO.getModelsByBrandId(brandId);
                request.setAttribute("models", models);
                request.setAttribute("selectedBrandId", brandId);
                request.setAttribute("selectedBrandName", brandName);
            }
        }

        request.setAttribute("prevModel", request.getParameter("modelName"));
        request.setAttribute("prevYear", request.getParameter("yearManufacture"));
        request.setAttribute("prevLicensePlate", request.getParameter("licensePlate"));

        request.getRequestDispatcher("/view/vehicle/addVehicle.jsp").forward(request, response);
    }
}
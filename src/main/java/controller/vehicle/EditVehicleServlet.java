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
import java.util.Optional;

@WebServlet(name = "EditVehicleServlet", urlPatterns = {"/customer/editVehicle"})
public class EditVehicleServlet extends HttpServlet {

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final CarDataDAO carDAO = new CarDataDAO();
    private final VehicleService vehicleService = new VehicleService(vehicleDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        String contextPath = request.getContextPath();

        if (customer == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(request.getParameter("id"));
            Vehicle vehicleToEdit = vehicleDAO.getVehicleById(vehicleId);

            if (vehicleToEdit != null && vehicleToEdit.getCustomerID() == customer.getCustomerId()) {
                List<CarBrand> brands = carDAO.getAllBrands();
                Optional<CarBrand> currentBrand = brands.stream()
                        .filter(b -> b.getBrandName().equalsIgnoreCase(vehicleToEdit.getBrand()))
                        .findFirst();

                if (currentBrand.isPresent()) {
                    int currentBrandId = currentBrand.get().getBrandId();
                    List<CarModel> models = carDAO.getModelsByBrandId(currentBrandId);
                    request.setAttribute("models", models);
                    request.setAttribute("selectedBrandId", currentBrandId);
                }

                request.setAttribute("vehicle", vehicleToEdit);
                request.setAttribute("brands", brands);
                request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
            } else {
                response.sendRedirect(contextPath + "/customer/garage");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(contextPath + "/customer/garage");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            if ("selectModel".equals(action)) {
                handleSelectModel(request, response);
            } else {
                handleUpdateVehicle(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            HttpSession session = request.getSession();
            session.setAttribute("error", "An error occurred while updating.");
            response.sendRedirect(request.getContextPath() + "/customer/garage");
        }
    }

    private void handleSelectModel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int brandId = Integer.parseInt(request.getParameter("brandId"));
        int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
        Vehicle vehicleToEdit = vehicleDAO.getVehicleById(vehicleId);
        List<CarBrand> brands = carDAO.getAllBrands();
        List<CarModel> models = carDAO.getModelsByBrandId(brandId);
        request.setAttribute("vehicle", vehicleToEdit);
        request.setAttribute("brands", brands);
        request.setAttribute("models", models);
        request.setAttribute("selectedBrandId", brandId);
        request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
    }

    private void handleUpdateVehicle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
        String brandName = request.getParameter("brandName");
        String modelName = request.getParameter("modelName");
        int year = Integer.parseInt(request.getParameter("yearManufacture"));
        String licensePlate = request.getParameter("licensePlate");

        if (!vehicleService.validateLicensePlateFormat(licensePlate)) {
            request.setAttribute("error", "Invalid license plate format.");
            repopulateFormOnError(request, response, vehicleId);
            return;
        }

        if (vehicleService.isLicensePlateTaken(licensePlate, vehicleId)) {
            request.setAttribute("error", "This license plate is already used by another vehicle.");
            repopulateFormOnError(request, response, vehicleId);
            return;
        }

        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setVehicleID(vehicleId);
        updatedVehicle.setCustomerID(customer.getCustomerId());
        updatedVehicle.setBrand(brandName);
        updatedVehicle.setModel(modelName);
        updatedVehicle.setYearManufacture(year);
        updatedVehicle.setLicensePlate(licensePlate);

        vehicleDAO.updateVehicle(updatedVehicle);

        session.setAttribute("success", "Vehicle information updated successfully!");
        response.sendRedirect(request.getContextPath() + "/customer/garage");
    }

    private void repopulateFormOnError(HttpServletRequest request, HttpServletResponse response, int vehicleId) throws Exception {
        Vehicle vehicleToEdit = vehicleDAO.getVehicleById(vehicleId);
        List<CarBrand> brands = carDAO.getAllBrands();

        vehicleToEdit.setLicensePlate(request.getParameter("licensePlate"));
        vehicleToEdit.setYearManufacture(Integer.parseInt(request.getParameter("yearManufacture")));

        request.setAttribute("vehicle", vehicleToEdit);
        request.setAttribute("brands", brands);

        Optional<CarBrand> currentBrand = brands.stream()
                .filter(b -> b.getBrandName().equalsIgnoreCase(request.getParameter("brandName"))).findFirst();

        if (currentBrand.isPresent()) {
            int currentBrandId = currentBrand.get().getBrandId();
            List<CarModel> models = carDAO.getModelsByBrandId(currentBrandId);
            request.setAttribute("models", models);
            request.setAttribute("selectedBrandId", currentBrandId);
        }

        request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
    }
}
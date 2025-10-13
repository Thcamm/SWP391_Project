package controller.vehicle;

import dao.vehicle.CarDataDAO;
import dao.vehicle.VehicleDAO;
import model.customer.Customer;
import model.vehicle.CarBrand;
import model.vehicle.CarModel;
import model.vehicle.Vehicle;

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

@WebServlet(name = "EditVehicleServlet", urlPatterns = {"/editVehicle"})
public class EditVehicleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect("login");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(request.getParameter("id"));
            VehicleDAO vehicleDAO = new VehicleDAO();
            CarDataDAO carDAO = new CarDataDAO();

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
                request.getRequestDispatcher("/view/vehicle/editVehicle.jsp").forward(request, response);
            } else {
                response.sendRedirect("garage");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("garage");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect("login");
            return;
        }

        int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
        VehicleDAO vehicleDAO = new VehicleDAO();
        CarDataDAO carDAO = new CarDataDAO();

        try {
            if ("selectModel".equals(action)) {
                int brandId = Integer.parseInt(request.getParameter("brandId"));

                // Tải lại mọi thứ cần thiết cho form
                Vehicle vehicleToEdit = vehicleDAO.getVehicleById(vehicleId);
                List<CarBrand> brands = carDAO.getAllBrands();
                List<CarModel> models = carDAO.getModelsByBrandId(brandId);

                request.setAttribute("vehicle", vehicleToEdit);
                request.setAttribute("brands", brands);
                request.setAttribute("models", models);
                request.setAttribute("selectedBrandId", brandId); // Cập nhật brand đã chọn

                request.getRequestDispatcher("/view/vehicle/editVehicle.jsp").forward(request, response);

            } else {
                String brandName = request.getParameter("brandName");
                String modelName = request.getParameter("modelName");
                int year = Integer.parseInt(request.getParameter("yearManufacture"));
                String licensePlate = request.getParameter("licensePlate");

                Vehicle updatedVehicle = new Vehicle();
                updatedVehicle.setVehicleID(vehicleId);
                updatedVehicle.setCustomerID(customer.getCustomerId());
                updatedVehicle.setBrand(brandName);
                updatedVehicle.setModel(modelName);
                updatedVehicle.setYearManufacture(year);
                updatedVehicle.setLicensePlate(licensePlate);
                vehicleDAO.updateVehicle(updatedVehicle);

                session.setAttribute("success", "Vehicle information updated successfully!");
                response.sendRedirect("garage");
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while updating.");
            response.sendRedirect("garage");
        }
    }
}
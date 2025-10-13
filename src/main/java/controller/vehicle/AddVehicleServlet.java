package controller.vehicle;

import dao.vehicle.CarDataDAO;
import dao.vehicle.VehicleDAO;
import model.customer.Customer;
import model.user.User;
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

@WebServlet(name = "AddVehicleServlet", urlPatterns = {"/addVehicle"})
public class AddVehicleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CarDataDAO carDAO = new CarDataDAO();
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

        HttpSession session = request.getSession();
        CarDataDAO carDAO = new CarDataDAO();
        VehicleDAO vehicleDAO = new VehicleDAO();

        try {
            if ("selectModel".equals(action)) {
                int brandId = Integer.parseInt(request.getParameter("brandId"));

                List<CarBrand> brands = carDAO.getAllBrands();
                List<CarModel> models = carDAO.getModelsByBrandId(brandId);

                request.setAttribute("brands", brands);
                request.setAttribute("models", models);
                request.setAttribute("selectedBrandId", brandId);

                request.getRequestDispatcher("/view/vehicle/addVehicle.jsp").forward(request, response);

            } else { // Mặc định là 'saveVehicle'
                Customer customer = (Customer) session.getAttribute("customer");
                if (customer == null) {
                    response.sendRedirect("login");
                    return;
                }

                String brandName = request.getParameter("brandName");
                String modelName = request.getParameter("modelName");
                int year = Integer.parseInt(request.getParameter("yearManufacture"));
                String licensePlate = request.getParameter("licensePlate");

                if (vehicleDAO.checkLicensePlateExists(licensePlate)) {
                    request.setAttribute("error", "This license plate already exists.");
                    doGet(request, response); // Tải lại form với dữ liệu cũ
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
                response.sendRedirect("garage");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred. Please try again.");
            doGet(request, response);
        }
    }
}
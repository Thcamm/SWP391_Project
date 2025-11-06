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

@WebServlet(name = "EditVehicleServlet", urlPatterns = {"/customer/editVehicle"})
public class EditVehicleServlet extends HttpServlet {

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final VehicleService vehicleService = new VehicleService(vehicleDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Vehicle v = vehicleDAO.getVehicleById(id);
            if (v == null || v.getCustomerID() != customer.getCustomerId()) {
                response.sendRedirect(request.getContextPath() + "/customer/garage");
                return;
            }
            request.setAttribute("vehicle", v);
            request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/customer/garage");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            String brand = request.getParameter("brandName");
            String model = request.getParameter("modelName");
            String yearStr = request.getParameter("yearManufacture");
            String licensePlate = request.getParameter("licensePlate");

            if (!vehicleService.validateLicensePlateFormat(licensePlate)) {
                request.setAttribute("error", "Invalid license plate format.");
                request.setAttribute("vehicle", vehicleDAO.getVehicleById(vehicleId));
                request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
                return;
            }

            if (vehicleService.isLicensePlateTaken(licensePlate, vehicleId)) {
                request.setAttribute("error", "This license plate is used by another vehicle.");
                request.setAttribute("vehicle", vehicleDAO.getVehicleById(vehicleId));
                request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
                return;
            }

            int year = Integer.parseInt(yearStr);
            int currentYear = java.time.Year.now().getValue();
            if (year < 1900 || year > currentYear) {
                request.setAttribute("error", "Invalid year.");
                request.setAttribute("vehicle", vehicleDAO.getVehicleById(vehicleId));
                request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
                return;
            }

            Vehicle updated = new Vehicle();
            updated.setVehicleID(vehicleId);
            updated.setCustomerID(customer.getCustomerId());
            updated.setBrand(brand);
            updated.setModel(model);
            updated.setYearManufacture(year);
            updated.setLicensePlate(licensePlate);

            vehicleDAO.updateVehicle(updated);
            session.setAttribute("success", "Vehicle updated!");
            response.sendRedirect(request.getContextPath() + "/customer/garage");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error.");
            request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid input.");
            request.getRequestDispatcher("/view/customer/editVehicle.jsp").forward(request, response);
        }
    }
}

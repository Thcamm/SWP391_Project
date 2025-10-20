package controller.employee.customerservice;

import dao.carservice.CarServiceDAO;
import dao.vehicle.VehicleDAO;
import model.customer.Customer;
import model.workorder.ServiceRequest;
import model.servicetype.Service;
import model.vehicle.Vehicle;
import service.carservice.ServiceRequestService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CustomerCreateRequestServlet", urlPatterns = {"/customerservice/createRequest"})
public class CreateRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        VehicleDAO vehicleDAO = new VehicleDAO();
        CarServiceDAO carServiceDAO = new CarServiceDAO();
        try {
            List<Vehicle> vehicles = vehicleDAO.getVehiclesByCustomerId(customer.getCustomerId());
            List<Service> services = carServiceDAO.getAllServices();

            request.setAttribute("vehicles", vehicles);
            request.setAttribute("services", services);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load data for the form. Please try again later.");
        }
        request.getRequestDispatcher("/view/customer/createRequest.jsp").forward(request, response);
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
            int serviceId = Integer.parseInt(request.getParameter("serviceId"));

            ServiceRequest newRequest = new ServiceRequest();
            newRequest.setCustomerID(customer.getCustomerId());
            newRequest.setVehicleID(vehicleId);
            newRequest.setServiceID(serviceId);

            ServiceRequestService requestService = new ServiceRequestService();
            boolean success = requestService.createRequestAndNotify(newRequest);

            if (success) {
                session.setAttribute("success", "Your service request was sent successfully! A manager will review it shortly.");
            } else {
                session.setAttribute("error", "Failed to create the service request. Please try again.");
            }
            response.sendRedirect(request.getContextPath() + "/customer/createRequest");

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred. Please check your selections and try again.");
            response.sendRedirect(request.getContextPath() + "/customer/createRequest");
        }
    }
}
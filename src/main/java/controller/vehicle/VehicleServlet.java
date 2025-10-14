package controller.vehicle;

import dao.vehicle.VehicleDAO;
import model.customer.Customer; // Assuming you have a Customer model
import model.user.User;
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

@WebServlet(name = "VehicleServlet", urlPatterns = {"/customer/garage"})
public class VehicleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Customer customer = (Customer) session.getAttribute("customer"); // Assuming customer info is also in session

        // 1. Security Check: Ensure user is logged in and is a customer
        if (user == null || customer == null) {
            response.sendRedirect("login");
            return;
        }

        VehicleDAO vehicleDAO = new VehicleDAO();
        try {
            // 2. Fetch data from DAO using the customer's ID
            List<Vehicle> vehicleList = vehicleDAO.getVehiclesByCustomerId(customer.getCustomerId());

            // 3. Set the data as a request attribute to be used by the JSP
            request.setAttribute("vehicleList", vehicleList);

            // 4. Forward to the JSP page for display
            request.getRequestDispatcher("/view/vehicle/garage.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Sorry, we couldn't retrieve your vehicles due to a database issue.");
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // To be implemented in the next parts of the feature
    }
}
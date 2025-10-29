package controller.vehicle;

import dao.vehicle.VehicleDAO;
import model.customer.Customer;
import model.user.User;
import model.vehicle.Vehicle;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "VehicleServlet", urlPatterns = {"/customer/garage"})
public class VehicleServlet extends HttpServlet {

    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Customer customer = (Customer) session.getAttribute("customer");

        if (user == null || customer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            List<Vehicle> vehicleList = vehicleDAO.getVehiclesByCustomerId(customer.getCustomerId());
            request.setAttribute("vehicleList", vehicleList);
            // pull any flash messages
            Object success = session.getAttribute("success");
            Object error = session.getAttribute("error");
            if (success != null) { request.setAttribute("success", success); session.removeAttribute("success"); }
            if (error != null) { request.setAttribute("error", error); session.removeAttribute("error"); }

            request.getRequestDispatcher("/view/customer/garage.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Sorry, can not load vehicles now.");
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }
}

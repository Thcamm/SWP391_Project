package controller.vehicle;

import dao.vehicle.VehicleDAO;
import model.customer.Customer;
import model.user.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "DeleteVehicleServlet", urlPatterns = {"/customer/deleteVehicle"})
public class DeleteVehicleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("customer");
        String contextPath = request.getContextPath();

        if (customer == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String redirectTo = contextPath + "/customer/garage";

        try {
            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            VehicleDAO vehicleDAO = new VehicleDAO();

            boolean success = vehicleDAO.deleteVehicle(vehicleId, customer.getCustomerId());

            if (success) {
                session.setAttribute("success", "Vehicle deleted successfully.");
            } else {
                session.setAttribute("error", "Failed to delete vehicle. It may have already been removed or you don't have permission.");
            }

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while deleting the vehicle.");
        }

        response.sendRedirect(redirectTo);
    }
}
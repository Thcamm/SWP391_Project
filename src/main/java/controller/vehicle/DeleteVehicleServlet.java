package controller.vehicle;

import dao.vehicle.VehicleDAO;
import model.customer.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "DeleteVehicleServlet", urlPatterns = {"/customer/deleteVehicle"})
public class DeleteVehicleServlet extends HttpServlet {

    private final VehicleDAO vehicleDAO = new VehicleDAO();

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

        try {
            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            boolean ok = vehicleDAO.deleteVehicle(vehicleId, customer.getCustomerId());
            if (ok) session.setAttribute("success", "Vehicle deleted.");
            else session.setAttribute("error", "Delete failed or not permitted.");
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            session.setAttribute("error", "Error deleting vehicle.");
        }
        response.sendRedirect(contextPath + "/customer/garage");
    }
}

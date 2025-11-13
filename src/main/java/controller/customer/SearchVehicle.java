package controller.customer;


import com.google.gson.Gson;
import dao.customer.CustomerDAO;
import dao.vehicle.VehicleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.customer.Customer;
import model.servicetype.Service;
import model.user.User;
import model.vehicle.Vehicle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/customer/vehicles-search")
public class SearchVehicle extends HttpServlet {

    private VehicleDAO dao = new VehicleDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get customer information
        CustomerDAO customerDAO = new CustomerDAO();
        int customerId;
        customerId = customerDAO.getCustomerIdByUserId(user.getUserId());
        if (customerId == 0) {
            session.setAttribute("message", "Customer information not found!");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customer/repair-list");
            return;
        }

        String query = request.getParameter("query");


        if (query == null) query = "";

        int limit = 10;
        int offset = 0;

        try {

            List<Vehicle> vehicles = dao.searchVehicles(query, customerId, limit, offset);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(vehicles));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}



package controller.employee.customerservice;

import com.google.gson.Gson;
import dao.vehicle.VehicleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.servicetype.Service;
import model.vehicle.Vehicle;

import java.io.IOException;
import java.util.List;

@WebServlet("/vehicles/search")
public class SearchVehicle extends HttpServlet {

    private VehicleDAO dao = new VehicleDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String customerIdParam = request.getParameter("customerId");

        if (query == null) query = "";

        int limit = 10;
        int offset = 0;

        try {
            if (customerIdParam == null || customerIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing customerId parameter\"}");
                return;
            }

            int customerId = Integer.parseInt(customerIdParam);

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


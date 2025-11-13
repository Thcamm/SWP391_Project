package controller.employee.customerservice;

import com.google.gson.Gson;
import dao.vehicle.VehicleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.vehicle.Vehicle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/customerservice/vehicles-search")
public class SearchVehicle extends HttpServlet {

    private VehicleDAO dao = new VehicleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String customerIdRaw = request.getParameter("id");
        String query = request.getParameter("query");
        if (query == null) query = "";

        Integer customerId = null;

        // Parse customer ID if provided
        if (customerIdRaw != null && !customerIdRaw.trim().isEmpty()) {
            try {
                customerId = Integer.parseInt(customerIdRaw);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Invalid customer ID\"}");
                return;
            }
        }

        int limit = 10;
        int offset = 0;

        try {
            List<Vehicle> vehicles = dao.searchVehicles(query, customerId, limit, offset);

            // Return empty array if no vehicles found (not error)
            if (vehicles == null) {
                vehicles = new ArrayList<>();
            }

            response.getWriter().write(new Gson().toJson(vehicles));

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
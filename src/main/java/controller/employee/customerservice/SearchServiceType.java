package controller.employee.customerservice;

import com.google.gson.Gson;
import dao.carservice.CarServiceDAO;
import model.servicetype.Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/services/search")
public class SearchServiceType extends HttpServlet {

    private CarServiceDAO serviceDAO = new CarServiceDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        if (query == null) query = "";

        int limit = 20;
        int offset = 0;

        try {
            List<Service> services = serviceDAO.searchServices(query, limit, offset);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(services));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }

    }
}

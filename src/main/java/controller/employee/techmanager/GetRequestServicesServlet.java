package controller.employee.techmanager;

import com.google.gson.Gson;
import dao.carservice.ServiceRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.workorder.ServiceRequestDetail;

import java.io.IOException;
import java.util.List;

/**
 * AJAX endpoint to get services for a ServiceRequest
 * Used by service-requests-new.jsp to load services dynamically
 */
@WebServlet("/techmanager/get-request-services")
public class GetRequestServicesServlet extends HttpServlet {

    private final ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String requestIdParam = request.getParameter("requestId");
            if (requestIdParam == null || requestIdParam.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"Missing requestId\"}");
                return;
            }

            int requestId = Integer.parseInt(requestIdParam);

            // Get all ServiceRequestDetail for this request
            List<ServiceRequestDetail> services = serviceRequestDAO.getServiceRequestDetails(requestId);

            // Return as JSON
            String jsonResponse = gson.toJson(services);
            response.getWriter().write(jsonResponse);

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid requestId format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}

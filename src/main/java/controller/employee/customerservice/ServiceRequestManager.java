package controller.employee.customerservice;

import dao.carservice.ServiceRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dto.ServiceRequestViewDTO;
import model.user.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = { "/customerservice/requests" })
public class ServiceRequestManager extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            ServiceRequestDAO dao = new ServiceRequestDAO();
            List<ServiceRequestViewDTO> requestList = dao.getAllServiceRequestsForView();
            request.setAttribute("serviceRequestList", requestList);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load service requests.");
        }

        request.getRequestDispatcher("/view/customerservice/serviceRequest.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String newStatus = request.getParameter("newStatus");

            if (newStatus == null || newStatus.trim().isEmpty()) {
                session.setAttribute("errorMessage", "Status cannot be empty.");
            } else {
                ServiceRequestDAO dao = new ServiceRequestDAO();
                boolean success = dao.updateServiceRequestStatus(requestId, newStatus);

                if (success) {
                    session.setAttribute("successMessage", "Request #" + requestId + " status updated successfully.");
                } else {
                    session.setAttribute("errorMessage", "Failed to update status for request #" + requestId);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "An error occurred while updating status.");
        }

        response.sendRedirect(request.getContextPath() + "/customerservice/requestRequests");
    }
}

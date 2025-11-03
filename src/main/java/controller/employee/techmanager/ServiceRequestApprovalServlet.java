package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.carservice.ServiceRequestDAO;
import dao.employee.admin.AdminDAO;
import model.servicetype.ServiceRequestViewDTO;
import service.carservice.ServiceRequestService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * TechManager: View and Approve Pending ServiceRequests
 */
@WebServlet("/techmanager/service-requests")
public class ServiceRequestApprovalServlet extends HttpServlet {

    private ServiceRequestDAO serviceRequestDAO;
    private ServiceRequestService serviceRequestService;
    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        this.serviceRequestDAO = new ServiceRequestDAO();
        this.serviceRequestService = new ServiceRequestService();
        this.adminDAO = new AdminDAO();
    }

    /**
     * Handles HTTP GET requests.
     * Typically used to retrieve data or display a user interface.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get pending service requests
            List<ServiceRequestViewDTO> pendingRequests = serviceRequestDAO.getPendingServiceRequests();

            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("totalPending", pendingRequests.size());

            // Forward to JSP
            request.getRequestDispatcher("/view/techmanager/service-requests.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            System.err.println("Error loading pending service requests: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to load service requests: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("approve".equals(action)) {
            handleApproval(request, response);
        } else if ("reject".equals(action)) {
            handleRejection(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/techmanager/service-requests");
        }
    }

    private void handleApproval(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String taskDescription = request.getParameter("taskDescription");

            // Get current TechManager's EmployeeID from session
            HttpSession session = request.getSession();
            String userName = (String) session.getAttribute("userName");

            if (userName == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            Integer techManagerEmployeeId = adminDAO.getEmployeeIdByUsername(userName);

            if (techManagerEmployeeId == null) {
                request.setAttribute("errorMessage", "TechManager employee record not found");
                doGet(request, response);
                return;
            }

            // Call service to approve and create WorkOrder
            int workOrderId = serviceRequestService.approveServiceRequestAndCreateWorkOrder(
                    requestId,
                    techManagerEmployeeId,
                    taskDescription);

            if (workOrderId > 0) {
                // Success
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Service Request approved. WorkOrder #" + workOrderId + " created.",
                                "UTF-8")
                        +
                        "&type=success");
            } else if (workOrderId == -2) {
                // Already approved
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Service Request is not in PENDING status.", "UTF-8") +
                        "&type=warning");
            } else {
                // Failed
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Failed to approve Service Request.", "UTF-8") +
                        "&type=error");
            }

        } catch (Exception e) {
            System.err.println("Error approving service request: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8") +
                    "&type=error");
        }
    }

    private void handleRejection(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String reason = request.getParameter("rejectionReason");

            boolean success = serviceRequestDAO.updateServiceRequestStatus(requestId, "REJECTED");

            if (success) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Service Request #" + requestId + " rejected.", "UTF-8") +
                        "&type=success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Failed to reject Service Request.", "UTF-8") +
                        "&type=error");
            }

        } catch (Exception e) {
            System.err.println("Error rejecting service request: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8") +
                    "&type=error");
        }
    }
}

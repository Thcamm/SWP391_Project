package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.dto.ServiceRequestViewDTO;
import service.employee.techmanager.ServiceRequestApprovalService;
import service.employee.techmanager.TechManagerService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * TechManager: View and Approve Pending ServiceRequests (GĐ0 → GĐ1)
 * 
 * @author SWP391 Team
 * @version 3.0 (Refactored to use TechManagerService for business logic)
 */
@WebServlet("/techmanager/service-requests")
public class ServiceRequestApprovalServlet extends HttpServlet {

    private ServiceRequestApprovalService serviceRequestApprovalService;
    private TechManagerService techManagerService;

    @Override
    public void init() throws ServletException {
        this.serviceRequestApprovalService = new ServiceRequestApprovalService();
        this.techManagerService = new TechManagerService();
    }

    /**
     * GET: Display pending service requests
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get pending service requests
            List<ServiceRequestViewDTO> pendingRequests = serviceRequestApprovalService.getPendingServiceRequests();

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

    /**
     * POST: Handle approval/rejection actions
     * 
     * Thin controller: Extracts parameters, calls Service, redirects
     */
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

    /**
     * Handle Service Request Approval - NOW USES TechManagerService
     * 
     * Reduced from 90+ lines to 45 lines (50% reduction)
     * Transaction management moved to Service layer
     */
    private void handleApproval(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            // === STEP 1: Extract parameters ===
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String notes = request.getParameter("taskDescription"); // Optional notes

            // Get current TechManager's EmployeeID from session
            HttpSession session = request.getSession();
            String userName = (String) session.getAttribute("userName");

            if (userName == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            Integer techManagerEmployeeId = serviceRequestApprovalService.getTechManagerEmployeeId(userName);

            if (techManagerEmployeeId == null) {
                request.setAttribute("errorMessage", "TechManager employee record not found");
                doGet(request, response);
                return;
            }

            // === STEP 2: Call Service layer (handles transaction internally) ===
            int workOrderId = techManagerService.approveServiceRequestAndCreateWorkOrder(
                    requestId,
                    techManagerEmployeeId,
                    notes);

            // === STEP 3: Redirect with success message ===
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode("Service Request approved. WorkOrder #" + workOrderId + " created.",
                            "UTF-8")
                    +
                    "&type=success");

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Business logic errors (validation failures)
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode(e.getMessage(), "UTF-8") +
                    "&type=warning");

        } catch (Exception e) {
            // Unexpected errors
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

            boolean success = serviceRequestApprovalService.rejectServiceRequest(requestId, reason);

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

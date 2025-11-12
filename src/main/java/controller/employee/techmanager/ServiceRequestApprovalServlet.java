package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.employee.techmanager.PendingServiceRequestDTO;
import service.employee.techmanager.ServiceRequestApprovalService;
import service.employee.techmanager.TechManagerService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * TechManager: View and Approve Pending ServiceRequests (GĐ0 → GĐ1 + GĐ2
 * MERGED)
 * 
 * @author SWP391 Team
 * @version 4.0 (Merged Approve + Classify into single screen - NO GSON, Pure Servlet)
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
            List<PendingServiceRequestDTO> pendingRequests = serviceRequestApprovalService.getPendingServiceRequests();

            System.out.println("=== [DEBUG] GET /service-requests ===");
            System.out.println("Total pending requests: " + pendingRequests.size());
            for (PendingServiceRequestDTO req : pendingRequests) {
                System.out.println("  - Request #" + req.getRequestId() +
                        " | Status: " + req.getStatus() +
                        " | Customer: " + req.getCustomerName());

                // Load services for each request
                List<model.workorder.ServiceRequestDetail> services = serviceRequestApprovalService
                        .getServicesForRequest(req.getRequestId());
                req.setServices(services);
                System.out.println("    → Loaded " + services.size() + " service(s)");
            }
            System.out.println("=====================================");

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
     * POST: Handle approval-classify/rejection actions
     * 
     * LUỒNG 4.0: Only approve-classify action supported (merged workflow)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("approve-classify".equals(action)) {
            handleApprovalAndClassify(request, response);
        } else if ("reject".equals(action)) {
            handleRejection(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/techmanager/service-requests");
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

    /**
     * NEW LUỒNG 4.0: Handle Approval + Classification in ONE step
     * GĐ1 + GĐ2 MERGED
     * 
     * Approve ServiceRequest → Create N WODs → Immediately classify each WOD source
     * → Done
     * NO REDIRECT to Triage page
     * NO JSON, NO Gson - Pure servlet approach
     */
    private void handleApprovalAndClassify(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            // === STEP 1: Extract parameters ===
            int requestId = Integer.parseInt(request.getParameter("requestId"));

            // Parse radio buttons: source_123=REQUEST, source_456=DIAGNOSTIC, ...
            Map<String, String> classifications = new java.util.HashMap<>();
            java.util.Enumeration<String> paramNames = request.getParameterNames();
            
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (paramName.startsWith("source_")) {
                    String serviceId = paramName.substring(7); // Remove "source_" prefix
                    String source = request.getParameter(paramName);
                    classifications.put(serviceId, source);
                    System.out.println("    → Service " + serviceId + " = " + source);
                }
            }

            System.out.println("📝 Received " + classifications.size() + " classifications for Request #" + requestId);

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

            // === STEP 2: Call Service to Approve + Classify ===
            // This will:
            // 1. Create WorkOrder
            // 2. Create N WorkOrderDetails
            // 3. Immediately set source for each WOD based on classifications Map
            techManagerService.approveAndClassifyServiceRequest(
                    requestId,
                    techManagerEmployeeId,
                    classifications);

            // === STEP 3: STAY on same page with success message ===
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode(
                            "Service Request #" + requestId + " approved and classified successfully!", "UTF-8")
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
            System.err.println("Error approving and classifying service request: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8") +
                    "&type=error");
        }
    }
}

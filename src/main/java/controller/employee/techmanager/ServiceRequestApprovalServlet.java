//package controller.employee.techmanager;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import model.employee.techmanager.PendingServiceRequestDTO;
//import service.employee.techmanager.ServiceRequestApprovalService;
//import service.employee.techmanager.TechManagerService;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * TechManager: View and Approve Pending ServiceRequests (LUỒNG MỚI - GĐ 1)
// *
// * @author SWP391 Team
// * @version REFACTORED - Triage Workflow (GĐ 1 → Redirect to GĐ 2)
// */
//@WebServlet("/techmanager/service-requests")
//public class ServiceRequestApprovalServlet extends HttpServlet {
//
//    private ServiceRequestApprovalService serviceRequestApprovalService;
//    private TechManagerService techManagerService;
//
//    @Override
//    public void init() throws ServletException {
//        this.serviceRequestApprovalService = new ServiceRequestApprovalService();
//        this.techManagerService = new TechManagerService();
//    }
//
//    /**
//     * GET: Display pending service requests (LUỒNG MỚI - GĐ 1)
//     *
//     * Simplified - No need to pre-load services (classification happens in GĐ 2)
//     */
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        try {
//            // Get pending service requests
//            List<PendingServiceRequestDTO> pendingRequests = serviceRequestApprovalService.getPendingServiceRequests();
//
//            System.out.println("=== [LUỒNG MỚI - GĐ 1] GET /service-requests ===");
//            System.out.println("Total pending requests: " + pendingRequests.size());
//            for (PendingServiceRequestDTO req : pendingRequests) {
//                System.out.println("  - Request #" + req.getRequestId() +
//                        " | Status: " + req.getStatus() +
//                        " | Customer: " + req.getCustomerName());
//
//                // Pre-load services for preview (read-only display)
//                List<model.workorder.ServiceRequestDetail> services = serviceRequestApprovalService
//                        .getServicesForRequest(req.getRequestId());
//                req.setServices(services);
//                System.out.println("    → Loaded " + services.size() + " service(s) for preview");
//            }
//            System.out.println("==============================================");
//
//            request.setAttribute("pendingRequests", pendingRequests);
//            request.setAttribute("totalPending", pendingRequests.size());
//
//            // Forward to JSP
//            request.getRequestDispatcher("/view/techmanager/service-requests.jsp")
//                    .forward(request, response);
//
//        } catch (SQLException e) {
//            System.err.println("Error loading pending service requests: " + e.getMessage());
//            e.printStackTrace();
//            request.setAttribute("errorMessage", "Failed to load service requests: " + e.getMessage());
//            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
//        }
//    }
//
//    /**
//     * POST: Handle approval/rejection actions
//     *
//     * LUỒNG MỚI (GĐ 1): Approve → Redirect to Triage (GĐ 2)
//     */
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        request.setCharacterEncoding("UTF-8");
//        String action = request.getParameter("action");
//
//        if ("approve".equals(action)) {
//            handleApproval(request, response);
//        } else if ("reject".equals(action)) {
//            handleRejection(request, response);
//        } else {
//            response.sendRedirect(request.getContextPath() + "/techmanager/service-requests");
//        }
//    }
//
//    private void handleRejection(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//
//        try {
//            int requestId = Integer.parseInt(request.getParameter("requestId"));
//            String reason = request.getParameter("rejectionReason");
//
//            boolean success = serviceRequestApprovalService.rejectServiceRequest(requestId, reason);
//
//            if (success) {
//                response.sendRedirect(request.getContextPath() +
//                        "/techmanager/service-requests?message=" +
//                        java.net.URLEncoder.encode("Service Request #" + requestId + " rejected.", "UTF-8") +
//                        "&type=success");
//            } else {
//                response.sendRedirect(request.getContextPath() +
//                        "/techmanager/service-requests?message=" +
//                        java.net.URLEncoder.encode("Failed to reject Service Request.", "UTF-8") +
//                        "&type=error");
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error rejecting service request: " + e.getMessage());
//            e.printStackTrace();
//            response.sendRedirect(request.getContextPath() +
//                    "/techmanager/service-requests?message=" +
//                    java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8") +
//                    "&type=error");
//        }
//    }
//
//    /**
//     * LUỒNG MỚI (GĐ 1): Handle Approval → Redirect to Triage (GĐ 2)
//     *
//     * OLD LOGIC DELETED:
//     * - No more immediate classification
//     * - No more staying on same page
//     *
//     * NEW LOGIC (Direct Classification):
//     * 1. TechManager classifies each service (REQUEST/DIAGNOSTIC) during approval
//     * 2. Create WorkOrder with PENDING status
//     * 3. Create WorkOrderDetails with source set immediately
//     */
//    private void handleApproval(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//
//        try {
//            int requestId = Integer.parseInt(request.getParameter("requestId"));
//
//            // Get current TechManager's EmployeeID from session
//            HttpSession session = request.getSession();
//            String userName = (String) session.getAttribute("userName");
//
//            if (userName == null) {
//                response.sendRedirect(request.getContextPath() + "/login");
//                return;
//            }
//
//            Integer techManagerEmployeeId = serviceRequestApprovalService.getTechManagerEmployeeId(userName);
//
//            if (techManagerEmployeeId == null) {
//                response.sendRedirect(request.getContextPath() +
//                        "/techmanager/service-requests?message=" +
//                        java.net.URLEncoder.encode("TechManager employee record not found", "UTF-8") +
//                        "&type=error");
//                return;
//            }
//
//            // === GĐ 1: Approve ServiceRequest & Create N WODs with source classification
//            // ===
//
//            // Parse source classifications from request
//            String totalServicesStr = request.getParameter("totalServices");
//            if (totalServicesStr == null) {
//                throw new IllegalArgumentException("Missing totalServices parameter");
//            }
//
//            int totalServices = Integer.parseInt(totalServicesStr);
//            Map<Integer, String> sourceClassifications = new HashMap<>();
//            Map<Integer, java.math.BigDecimal> estimateHoursMap = new HashMap<>();
//
//            for (int i = 0; i < totalServices; i++) {
//                String serviceDetailIdStr = request.getParameter("serviceDetailId_" + i);
//                String source = request.getParameter("source_" + i);
//                String estimateHoursStr = request.getParameter("estimateHours_" + i);
//
//                if (serviceDetailIdStr != null && source != null) {
//                    int serviceDetailId = Integer.parseInt(serviceDetailIdStr);
//
//                    if (!source.equals("REQUEST") && !source.equals("DIAGNOSTIC")) {
//                        throw new IllegalArgumentException("Invalid source value: " + source);
//                    }
//
//                    sourceClassifications.put(serviceDetailId, source);
//
//                    // Parse estimate hours, default to 2.0 if invalid
//                    java.math.BigDecimal estimateHours = java.math.BigDecimal.valueOf(2.0);
//                    if (estimateHoursStr != null && !estimateHoursStr.trim().isEmpty()) {
//                        try {
//                            estimateHours = new java.math.BigDecimal(estimateHoursStr);
//                        } catch (NumberFormatException e) {
//                            System.err.println(
//                                    "Invalid estimateHours for service " + serviceDetailId + ": " + estimateHoursStr);
//                        }
//                    }
//                    estimateHoursMap.put(serviceDetailId, estimateHours);
//
//                    System.out.println("  [Classification] Service Detail #" + serviceDetailId + " → " + source + " ("
//                            + estimateHours + " hrs)");
//                }
//            }
//
//            if (sourceClassifications.isEmpty()) {
//                throw new IllegalArgumentException("No source classifications provided");
//            }
//
//            int workOrderId = techManagerService.approveServiceRequest(requestId, techManagerEmployeeId,
//                    sourceClassifications, estimateHoursMap);
//
//            if (workOrderId <= 0) {
//                throw new SQLException("Failed to create WorkOrder");
//            }
//
//            System.out.println(
//                    "✓ [GĐ 1] ServiceRequest #" + requestId + " approved → WorkOrder #" + workOrderId + " created with "
//                            + sourceClassifications.size() + " classified services");
//
//            // === REDIRECT to service requests page with success message ===
//            response.sendRedirect(request.getContextPath() +
//                    "/techmanager/service-requests?message=" +
//                    java.net.URLEncoder.encode("ServiceRequest approved! WorkOrder #" + workOrderId + " created.",
//                            "UTF-8")
//                    +
//                    "&type=success");
//
//        } catch (NumberFormatException e) {
//            System.err.println("Invalid request ID: " + e.getMessage());
//            response.sendRedirect(request.getContextPath() +
//                    "/techmanager/service-requests?message=" +
//                    java.net.URLEncoder.encode("Invalid request ID", "UTF-8") +
//                    "&type=error");
//
//        } catch (SQLException e) {
//            System.err.println("Error approving service request: " + e.getMessage());
//            e.printStackTrace();
//            response.sendRedirect(request.getContextPath() +
//                    "/techmanager/service-requests?message=" +
//                    java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8") +
//                    "&type=error");
//
//        } catch (Exception e) {
//            System.err.println("Unexpected error: " + e.getMessage());
//            e.printStackTrace();
//            response.sendRedirect(request.getContextPath() +
//                    "/techmanager/service-requests?message=" +
//                    java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8") +
//                    "&type=error");
//        }
//    }
//}
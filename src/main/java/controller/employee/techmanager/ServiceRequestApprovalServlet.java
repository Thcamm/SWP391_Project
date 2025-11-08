package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import common.DbContext;
import model.dto.ServiceRequestViewDTO;
import service.employee.techmanager.ServiceRequestApprovalService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * TechManager: View and Approve Pending ServiceRequests (GĐ0 → GĐ1)
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored to 3-tier architecture)
 */
@WebServlet("/techmanager/service-requests")
public class ServiceRequestApprovalServlet extends HttpServlet {

    private ServiceRequestApprovalService serviceRequestApprovalService;

    @Override
    public void init() throws ServletException {
        this.serviceRequestApprovalService = new ServiceRequestApprovalService();
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

        Connection conn = null;
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

            Integer techManagerEmployeeId = serviceRequestApprovalService.getTechManagerEmployeeId(userName);

            if (techManagerEmployeeId == null) {
                request.setAttribute("errorMessage", "TechManager employee record not found");
                doGet(request, response);
                return;
            }

            // Transaction: Approve & Create WorkOrder with DIAGNOSIS detail
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            int workOrderId = serviceRequestApprovalService.approveServiceRequest(
                    conn, requestId, taskDescription, techManagerEmployeeId);

            // Success - commit transaction
            conn.commit();

            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode("Service Request approved. WorkOrder #" + workOrderId + " created.",
                            "UTF-8")
                    +
                    "&type=success");

        } catch (IllegalArgumentException | IllegalStateException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode(e.getMessage(), "UTF-8") +
                    "&type=warning");
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            System.err.println("Error approving service request: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8") +
                    "&type=error");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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

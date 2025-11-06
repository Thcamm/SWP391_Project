package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import common.DbContext;
import dao.carservice.ServiceRequestDAO;
import dao.employee.admin.AdminDAO;
import dao.workorder.WorkOrderDAO;
import dao.workorder.WorkOrderDetailDAO;
import model.dto.ServiceRequestViewDTO;
import model.workorder.WorkOrder;
import model.workorder.WorkOrderDetail;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * TechManager: View and Approve Pending ServiceRequests
 */
@WebServlet("/techmanager/service-requests")
public class ServiceRequestApprovalServlet extends HttpServlet {

    private ServiceRequestDAO serviceRequestDAO;
    private WorkOrderDAO workOrderDAO;
    private WorkOrderDetailDAO workOrderDetailDAO;
    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        this.serviceRequestDAO = new ServiceRequestDAO();
        this.workOrderDAO = new WorkOrderDAO();
        this.workOrderDetailDAO = new WorkOrderDetailDAO();
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

            Integer techManagerEmployeeId = adminDAO.getEmployeeIdByUsername(userName);

            if (techManagerEmployeeId == null) {
                request.setAttribute("errorMessage", "TechManager employee record not found");
                doGet(request, response);
                return;
            }

            // ===== GIAI ĐOẠN 1: Approve & Create WorkOrder with DIAGNOSIS detail =====
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Check ServiceRequest status
            model.workorder.ServiceRequest serviceRequest = serviceRequestDAO.getServiceRequestForUpdate(conn,
                    requestId);
            if (serviceRequest == null) {
                conn.rollback();
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Service Request not found.", "UTF-8") +
                        "&type=error");
                return;
            }

            if (!"PENDING".equals(serviceRequest.getStatus())) {
                conn.rollback();
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Service Request is not in PENDING status.", "UTF-8") +
                        "&type=warning");
                return;
            }

            // Step 2: Update ServiceRequest to APPROVE
            boolean statusUpdated = serviceRequestDAO.updateServiceRequestStatus(conn, requestId, "APPROVE");
            if (!statusUpdated) {
                conn.rollback();
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Failed to update Service Request status.", "UTF-8") +
                        "&type=error");
                return;
            }

            // Step 3: Create WorkOrder
            WorkOrder workOrder = new WorkOrder();
            workOrder.setTechManagerId(techManagerEmployeeId);
            workOrder.setRequestId(requestId);
            workOrder.setEstimateAmount(BigDecimal.ZERO);
            workOrder.setStatus(WorkOrder.Status.IN_PROCESS);

            int workOrderId = workOrderDAO.createWorkOrder(conn, workOrder);
            if (workOrderId <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Failed to create WorkOrder.", "UTF-8") +
                        "&type=error");
                return;
            }

            // Step 4: Create initial DIAGNOSIS WorkOrderDetail
            WorkOrderDetail diagnosisDetail = new WorkOrderDetail();
            diagnosisDetail.setWorkOrderId(workOrderId);
            diagnosisDetail.setSource(WorkOrderDetail.Source.REQUEST);
            diagnosisDetail.setTaskDescription(
                    taskDescription != null && !taskDescription.trim().isEmpty()
                            ? taskDescription
                            : "Chẩn đoán tổng quát tình trạng xe");
            diagnosisDetail.setApprovalStatus(WorkOrderDetail.ApprovalStatus.APPROVED);
            diagnosisDetail.setEstimateHours(BigDecimal.valueOf(1.0));
            diagnosisDetail.setEstimateAmount(BigDecimal.ZERO);

            int detailId = workOrderDetailDAO.createWorkOrderDetail(conn, diagnosisDetail);
            if (detailId <= 0) {
                conn.rollback();
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/service-requests?message=" +
                        java.net.URLEncoder.encode("Failed to approve Service Request.", "UTF-8") +
                        "&type=error");
                return;
            }

            // Success - commit transaction
            conn.commit();

            response.sendRedirect(request.getContextPath() +
                    "/techmanager/service-requests?message=" +
                    java.net.URLEncoder.encode("Service Request approved. WorkOrder #" + workOrderId + " created.",
                            "UTF-8")
                    +
                    "&type=success");

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

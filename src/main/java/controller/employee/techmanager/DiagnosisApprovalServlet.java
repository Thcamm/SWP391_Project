package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import common.DbContext;
import dao.employee.admin.AdminDAO;
import dao.vehicle.VehicleDiagnosticDAO;
import dao.workorder.TaskAssignmentDAO;
import dao.workorder.WorkOrderDetailDAO;
import model.vehicle.VehicleDiagnostic;
import model.workorder.WorkOrderDetail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Tech Manager Phase 2: Approve/Reject Diagnosis
 * 
 * Flow:
 * 1. Tech Manager reviews diagnosis created by Technician
 * 2. If approve:
 * - Create WorkOrderDetail with source=DIAGNOSTIC
 * - Set ApprovalStatus=APPROVED (or PENDING if need customer approval)
 * 3. If reject:
 * - Update TaskAssignment status back to ASSIGNED
 * - Add rejection note
 */
@WebServlet("/techmanager/approve-diagnosis")
public class DiagnosisApprovalServlet extends HttpServlet {

    private TaskAssignmentDAO taskAssignmentDAO;
    private VehicleDiagnosticDAO diagnosticDAO;
    private WorkOrderDetailDAO workOrderDetailDAO;
    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        this.taskAssignmentDAO = new TaskAssignmentDAO();
        this.diagnosticDAO = new VehicleDiagnosticDAO();
        this.workOrderDetailDAO = new WorkOrderDetailDAO();
        this.adminDAO = new AdminDAO();
    }

    /**
     * POST: Approve or Reject Diagnosis
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");

        if (userId == null || userName == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Get parameters
            int assignmentId = Integer.parseInt(request.getParameter("assignmentId"));
            String action = request.getParameter("action"); // "approve" or "reject"
            // String notes = request.getParameter("notes"); // Optional rejection notes -
            // TODO: Use when NotificationDAO is ready

            // Get TechManager EmployeeID
            Integer techManagerId = adminDAO.getEmployeeIdByUsername(userName);
            if (techManagerId == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not a valid Tech Manager");
                return;
            }

            Connection conn = DbContext.getConnection();
            try {
                conn.setAutoCommit(false);

                // Load TaskAssignment
                var task = taskAssignmentDAO.getTaskById(assignmentId);
                if (task == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Task not found");
                    return;
                }

                // Load VehicleDiagnostic
                List<VehicleDiagnostic> diagnostics = diagnosticDAO.getDiagnosticsByAssignment(conn, assignmentId);
                if (diagnostics.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No diagnostic found");
                    return;
                }
                VehicleDiagnostic diagnostic = diagnostics.get(0);

                if ("approve".equals(action)) {
                    // === APPROVE DIAGNOSIS ===

                    // Get WorkOrderDetail info from task
                    WorkOrderDetail originalDetail = workOrderDetailDAO.getWorkOrderDetailById(task.getDetailID());
                    if (originalDetail == null) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Original WorkOrderDetail not found");
                        return;
                    }

                    // Create new WorkOrderDetail for the repair work based on diagnosis
                    WorkOrderDetail detail = new WorkOrderDetail();
                    detail.setWorkOrderId(originalDetail.getWorkOrderId());
                    detail.setSource(WorkOrderDetail.Source.DIAGNOSTIC);
                    detail.setDiagnosticId(diagnostic.getVehicleDiagnosticID());
                    detail.setTaskDescription(diagnostic.getIssueFound());
                    detail.setEstimateAmount(diagnostic.getTotalEstimate());
                    detail.setApprovalStatus(WorkOrderDetail.ApprovalStatus.PENDING);

                    workOrderDetailDAO.createWorkOrderDetail(conn, detail);

                    // TODO: Create notification for customer about pending quote

                    conn.commit();

                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/diagnosis-review?message=Diagnosis approved and sent to customer&type=success");

                } else if ("reject".equals(action)) {
                    // === REJECT DIAGNOSIS ===

                    // Update task status back to ASSIGNED
                    taskAssignmentDAO.updateTaskStatus(assignmentId,
                            model.employee.technician.TaskAssignment.TaskStatus.ASSIGNED);

                    // TODO: Create notification for technician about rejection with notes

                    conn.commit();

                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/diagnosis-review?message=Diagnosis rejected and sent back to technician&type=warning");

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    return;
                }

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.close();
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid assignment ID");
        } catch (SQLException e) {
            throw new ServletException("Database error during diagnosis approval", e);
        }
    }
}

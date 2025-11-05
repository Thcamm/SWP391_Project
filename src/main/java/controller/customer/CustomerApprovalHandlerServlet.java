package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import common.DbContext;
import dao.vehicle.VehicleDiagnosticDAO;
import model.vehicle.VehicleDiagnostic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Customer Approval Handler Servlet - Phase 3 & 4
 * 
 * Handles customer approval/rejection of diagnostic quotes (VehicleDiagnostic).
 * 
 * Workflow:
 * 1. Customer views diagnostic quote (from email/notification link)
 * 2. Customer approves or rejects the quote
 * 3. If APPROVED:
 * - Update VehicleDiagnostic.Status = 'APPROVED'
 * - Database trigger automatically creates WorkOrderDetail (Phase 4 Bridge)
 * - Database stored procedure copies approved DiagnosticParts to WorkOrderPart
 * 4. If REJECTED:
 * - Update VehicleDiagnostic.Status = 'REJECTED'
 * - Tech Manager can see this in rejected-tasks page
 * 
 * @version 1.0
 * @since 2025-11-04
 */
@WebServlet("/customer/approve-quote")
public class CustomerApprovalHandlerServlet extends HttpServlet {

    private VehicleDiagnosticDAO diagnosticDAO;

    @Override
    public void init() throws ServletException {
        this.diagnosticDAO = new VehicleDiagnosticDAO();
    }

    /**
     * GET: Display diagnostic quote for customer to approve/reject
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String diagnosticIdParam = request.getParameter("diagnosticId");

        if (diagnosticIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing diagnostic ID");
            return;
        }

        try {
            int diagnosticId = Integer.parseInt(diagnosticIdParam);

            // Load diagnostic with parts
            VehicleDiagnostic diagnostic = diagnosticDAO.getByIdWithParts(diagnosticId);

            if (diagnostic == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Diagnostic quote not found");
                return;
            }

            // Check if already approved/rejected
            if (diagnostic.getStatus() != VehicleDiagnostic.DiagnosticStatus.SUBMITTED) {
                request.setAttribute("alreadyProcessed", true);
                request.setAttribute("status", diagnostic.getStatus().name());
            }

            request.setAttribute("diagnostic", diagnostic);
            request.getRequestDispatcher("/view/customer/approve-quote.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid diagnostic ID format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading diagnostic: " + e.getMessage());
        }
    }

    /**
     * POST: Process customer approval/rejection
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customerId");

        // Allow both authenticated and unauthenticated access (via token/link)
        // If not logged in, token validation would happen here

        try {
            int diagnosticId = Integer.parseInt(request.getParameter("diagnosticId"));
            String action = request.getParameter("action"); // "approve" or "reject"
            String rejectionReason = request.getParameter("reason"); // Optional for reject

            if (!"approve".equals(action) && !"reject".equals(action)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                return;
            }

            Connection conn = DbContext.getConnection();
            try {
                conn.setAutoCommit(false);

                // Load diagnostic
                VehicleDiagnostic diagnostic = diagnosticDAO.getById(diagnosticId);

                if (diagnostic == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Diagnostic not found");
                    return;
                }

                // Check if already processed
                if (diagnostic.getStatus() != VehicleDiagnostic.DiagnosticStatus.SUBMITTED) {
                    response.sendRedirect(request.getContextPath() +
                            "/customer/approve-quote?diagnosticId=" + diagnosticId +
                            "&message=This quote has already been " + diagnostic.getStatus().name().toLowerCase());
                    return;
                }

                if ("approve".equals(action)) {
                    // ===== APPROVE DIAGNOSIS =====

                    // Update VehicleDiagnostic status
                    diagnostic.setStatus(VehicleDiagnostic.DiagnosticStatus.APPROVED);
                    diagnosticDAO.update(conn, diagnostic);

                    // ===== PHASE 4: AUTO-CREATE WorkOrderDetail =====
                    // This is handled by database trigger: trg_vd_on_status_approved
                    // The trigger calls: SP_OnVehicleDiagnosticApproved(diagnosticId)
                    // Which creates:
                    // 1. WorkOrderDetail (source=DIAGNOSTIC, diagnostic_id=diagnosticId)
                    // 2. WorkOrderPart entries for all approved DiagnosticParts

                    conn.commit();

                    // Success - redirect with message
                    response.sendRedirect(request.getContextPath() +
                            "/customer/dashboard?message=Quote approved successfully. Repair work will be scheduled soon.&type=success");

                } else if ("reject".equals(action)) {
                    // ===== REJECT DIAGNOSIS =====

                    diagnostic.setStatus(VehicleDiagnostic.DiagnosticStatus.REJECTED);

                    // Store rejection reason in IssueFound or a Notes field
                    if (rejectionReason != null && !rejectionReason.trim().isEmpty()) {
                        String updatedIssue = diagnostic.getIssueFound() +
                                "\n\n[CUSTOMER REJECTION REASON]: " + rejectionReason.trim();
                        diagnostic.setIssueFound(updatedIssue);
                    }

                    diagnosticDAO.update(conn, diagnostic);

                    conn.commit();

                    // Success - redirect with message
                    response.sendRedirect(request.getContextPath() +
                            "/customer/dashboard?message=Quote rejected. Our team will contact you shortly.&type=info");
                }

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.close();
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid diagnostic ID");
        } catch (SQLException e) {
            throw new ServletException("Database error during quote approval", e);
        }
    }
}

package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.workorder.TaskAssignmentDAO;
import model.employee.Employee;
import service.employee.techmanager.DiagnosisAssignmentService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * GIAI ĐOẠN 1: TechManager assigns DIAGNOSIS tasks to Technicians (GĐ1)
 * After approving ServiceRequest and creating WorkOrder,
 * TechManager uses this servlet to assign diagnosis task to a Technician
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored to 3-tier architecture)
 */
@WebServlet("/techmanager/assign-diagnosis")
public class DiagnosisAssignmentServlet extends HttpServlet {

    private DiagnosisAssignmentService diagnosisAssignmentService;

    @Override
    public void init() throws ServletException {
        this.diagnosisAssignmentService = new DiagnosisAssignmentService();
    }

    /**
     * GET: Display form to assign diagnosis tasks
     * Shows: WorkOrderDetails needing diagnosis + List of available Technicians
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get TechManager's EmployeeID from session
            HttpSession session = request.getSession();
            String userName = (String) session.getAttribute("userName");

            if (userName == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            Integer techManagerEmployeeId = diagnosisAssignmentService.getTechManagerEmployeeId(userName);
            if (techManagerEmployeeId == null) {
                request.setAttribute("errorMessage", "TechManager employee record not found");
                request.getRequestDispatcher("/view/error.jsp").forward(request, response);
                return;
            }

            // Get WorkOrderDetails that need diagnosis assignment
            List<TaskAssignmentDAO.WorkOrderDetailWithInfo> pendingDetails = diagnosisAssignmentService
                    .getPendingDiagnosisTasks(techManagerEmployeeId);

            // Get list of available Technicians
            List<Employee> technicians = diagnosisAssignmentService.getAvailableTechnicians();

            request.setAttribute("pendingDetails", pendingDetails);
            request.setAttribute("technicians", technicians);
            request.setAttribute("totalPending", pendingDetails.size());

            // Forward to JSP
            request.getRequestDispatcher("/view/techmanager/assign-diagnosis.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            System.err.println("Error loading diagnosis assignment data: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to load data: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    /**
     * POST: Create TaskAssignment for DIAGNOSIS
     * Assigns a Technician to perform diagnosis on a WorkOrderDetail
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            // Get parameters
            int detailId = Integer.parseInt(request.getParameter("detailId"));
            int technicianId = Integer.parseInt(request.getParameter("technicianId"));
            String priority = request.getParameter("priority");
            String notes = request.getParameter("notes");

            // Get scheduling parameters
            String plannedStartStr = request.getParameter("plannedStart");
            String plannedEndStr = request.getParameter("plannedEnd");

            // Parse scheduling times (format: yyyy-MM-dd'T'HH:mm from HTML5 datetime-local
            // input)
            LocalDateTime plannedStart = null;
            LocalDateTime plannedEnd = null;

            if (plannedStartStr != null && !plannedStartStr.trim().isEmpty()) {
                try {
                    plannedStart = LocalDateTime.parse(plannedStartStr);
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/assign-diagnosis?message=" +
                            java.net.URLEncoder.encode("Invalid planned start time format.", "UTF-8") +
                            "&type=error");
                    return;
                }
            }

            if (plannedEndStr != null && !plannedEndStr.trim().isEmpty()) {
                try {
                    plannedEnd = LocalDateTime.parse(plannedEndStr);
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/assign-diagnosis?message=" +
                            java.net.URLEncoder.encode("Invalid planned end time format.", "UTF-8") +
                            "&type=error");
                    return;
                }
            }

            // Assign diagnosis task via service
            try {
                int assignmentId = diagnosisAssignmentService.assignDiagnosisTask(
                        detailId, technicianId, priority, notes, plannedStart, plannedEnd);

                if (assignmentId > 0) {
                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/assign-diagnosis?message=" +
                            java.net.URLEncoder.encode("Diagnosis task assigned successfully!", "UTF-8") +
                            "&type=success");
                } else {
                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/assign-diagnosis?message=" +
                            java.net.URLEncoder.encode("Failed to assign diagnosis task.", "UTF-8") +
                            "&type=error");
                }
            } catch (IllegalArgumentException e) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-diagnosis?message=" +
                        java.net.URLEncoder.encode(e.getMessage(), "UTF-8") +
                        "&type=error");
            }

        } catch (NumberFormatException e) {
            System.err.println("Invalid parameters: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/assign-diagnosis?message=" +
                    java.net.URLEncoder.encode("Invalid input data.", "UTF-8") +
                    "&type=error");
        } catch (Exception e) {
            System.err.println("Error assigning diagnosis task: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/assign-diagnosis?message=" +
                    java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8") +
                    "&type=error");
        }
    }
}

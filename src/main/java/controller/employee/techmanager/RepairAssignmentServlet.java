package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import model.employee.techmanager.ApprovedRepairDTO;
import model.employee.techmanager.TechnicianDTO;
import service.employee.techmanager.RepairAssignmentService;

/**
 * Phase 3: Repair Assignment (GĐ4)
 * - Show approved Repair (customer approved)
 * - Assign repair tasks to technicians
 * 
 * @author SWP391 Team
 * @version 2.0 (Refactored to 3-tier architecture)
 */
@WebServlet("/techmanager/assign-repair")
public class RepairAssignmentServlet extends HttpServlet {

    private RepairAssignmentService repairAssignmentService;

    @Override
    public void init() throws ServletException {
        this.repairAssignmentService = new RepairAssignmentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get approved Repair waiting for repair assignment
            List<ApprovedRepairDTO> approvedRepairs = repairAssignmentService.getApprovedRepairs();

            // Get available technicians for repair
            List<TechnicianDTO> availableTechnicians = repairAssignmentService.getAvailableTechnicians();

            request.setAttribute("approvedRepair", approvedRepairs);
            request.setAttribute("availableTechnicians", availableTechnicians);

            // Handle messages
            String message = request.getParameter("message");
            String type = request.getParameter("type");
            if (message != null) {
                request.setAttribute("message", message);
                request.setAttribute("messageType", type != null ? type : "info");
            }

            request.getRequestDispatcher("/view/techmanager/assign-repair.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error loading repair assignments: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String detailIdStr = request.getParameter("detailId");
        String technicianIdStr = request.getParameter("technicianId");
        String plannedStartStr = request.getParameter("plannedStart");
        String plannedEndStr = request.getParameter("plannedEnd");

        if (detailIdStr == null || technicianIdStr == null) {
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/assign-repair?message=Missing required fields&type=error");
            return;
        }

        try {
            int detailId = Integer.parseInt(detailIdStr);
            int technicianId = Integer.parseInt(technicianIdStr);

            // Parse scheduling times
            LocalDateTime plannedStart = null;
            LocalDateTime plannedEnd = null;

            if (plannedStartStr != null && !plannedStartStr.trim().isEmpty()) {
                try {
                    plannedStart = LocalDateTime.parse(plannedStartStr);
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/assign-repair?message=Invalid planned start time format&type=error");
                    return;
                }
            }

            if (plannedEndStr != null && !plannedEndStr.trim().isEmpty()) {
                try {
                    plannedEnd = LocalDateTime.parse(plannedEndStr);
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/assign-repair?message=Invalid planned end time format&type=error");
                    return;
                }
            }

            // Assign repair task via service
            String resultMessage = repairAssignmentService.assignRepairTask(detailId, technicianId, plannedStart,
                    plannedEnd);

            if (resultMessage.contains("successfully")) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-repair?message=" + resultMessage + "&type=success");
            } else if (resultMessage.contains("already assigned")) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-repair?message=" + resultMessage + "&type=warning");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-repair?message=" + resultMessage + "&type=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/assign-repair?message=Error: " + e.getMessage() + "&type=error");
        }
    }
}

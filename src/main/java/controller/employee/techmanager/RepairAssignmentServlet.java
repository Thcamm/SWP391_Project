package controller.employee.techmanager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import dao.workorder.RepairAssignmentDAO;
import model.employee.techmanager.ApprovedRepairDTO;
import model.employee.techmanager.TechnicianDTO;

/**
 * Phase 3: Repair Assignment
 * - Show approved Repair (customer approved)
 * - Assign repair tasks to technicians
 * 
 * NEW: Supports task scheduling with planned_start and planned_end times
 */
@WebServlet("/techmanager/assign-repair")
public class RepairAssignmentServlet extends HttpServlet {

    private RepairAssignmentDAO repairAssignmentDAO;

    @Override
    public void init() throws ServletException {
        this.repairAssignmentDAO = new RepairAssignmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get approved Repair waiting for repair assignment
            List<ApprovedRepairDTO> approvedRepairs = repairAssignmentDAO.getApprovedRepairs();

            // Get available technicians for repair
            List<TechnicianDTO> availableTechnicians = repairAssignmentDAO.getAvailableTechnicians();

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

            // NEW: Parse scheduling times
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

            // Validate: planned_end must be after planned_start
            if (plannedStart != null && plannedEnd != null) {
                if (!plannedEnd.isAfter(plannedStart)) {
                    response.sendRedirect(request.getContextPath() +
                            "/techmanager/assign-repair?message=Planned end time must be after planned start time&type=error");
                    return;
                }
            }

            // Check if already assigned
            if (repairAssignmentDAO.hasRepairTaskAssigned(detailId)) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-repair?message=This task has already been assigned&type=warning");
                return;
            }

            // Create repair task assignment with scheduling
            boolean success = repairAssignmentDAO.createRepairTask(detailId, technicianId, plannedStart, plannedEnd);

            if (success) {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-repair?message=Repair task assigned successfully&type=success");
            } else {
                response.sendRedirect(request.getContextPath() +
                        "/techmanager/assign-repair?message=Failed to assign repair task&type=error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() +
                    "/techmanager/assign-repair?message=Error: " + e.getMessage() + "&type=error");
        }
    }
}

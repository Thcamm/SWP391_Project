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
import model.employee.technician.TaskAssignment;
import model.vehicle.VehicleDiagnostic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tech Manager Phase 2: Review Completed Diagnosis
 * 
 * Flow:
 * 1. Load completed DIAGNOSIS tasks assigned by this TechManager
 * 2. Load VehicleDiagnostic data for each task
 * 3. Display for review and approval
 */
@WebServlet("/techmanager/diagnosis-review")
public class DiagnosisReviewServlet extends HttpServlet {

    private TaskAssignmentDAO taskAssignmentDAO;
    private VehicleDiagnosticDAO diagnosticDAO;
    private AdminDAO adminDAO;

    @Override
    public void init() throws ServletException {
        this.taskAssignmentDAO = new TaskAssignmentDAO();
        this.diagnosticDAO = new VehicleDiagnosticDAO();
        this.adminDAO = new AdminDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userName = (String) session.getAttribute("userName");

        if (userId == null || userName == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 1. Get TechManager EmployeeID
            Integer techManagerId = adminDAO.getEmployeeIdByUsername(userName);
            if (techManagerId == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Not a valid Tech Manager");
                return;
            }

            // 2. Get completed DIAGNOSIS tasks for this TechManager
            // ✅ REUSE TaskAssignmentDAO [TECH_MANAGER ONLY] method
            List<TaskAssignment> completedTasks = 
                taskAssignmentDAO.getCompletedDiagnosisTasksForTechManager(techManagerId);

            // 3. Load VehicleDiagnostic data for each task
            List<DiagnosisReviewDTO> reviewList = new ArrayList<>();
            
            Connection conn = DbContext.getConnection();
            try {
                for (TaskAssignment task : completedTasks) {
                    DiagnosisReviewDTO dto = new DiagnosisReviewDTO();
                    dto.setTask(task);
                    
                    // ✅ REUSE VehicleDiagnosticDAO
                    List<VehicleDiagnostic> diagnostics = 
                        diagnosticDAO.getDiagnosticsByAssignment(conn, task.getAssignmentID());
                    
                    if (!diagnostics.isEmpty()) {
                        // Take the latest diagnostic (first in list due to ORDER BY CreatedAt DESC)
                        VehicleDiagnostic diagnostic = diagnostics.get(0);
                        
                        // Load full diagnostic with parts
                        VehicleDiagnostic fullDiagnostic = 
                            diagnosticDAO.getDiagnosticWithFullInfo(conn, diagnostic.getVehicleDiagnosticID());
                        
                        dto.setDiagnostic(fullDiagnostic);
                        reviewList.add(dto);
                    }
                }
            } finally {
                if (conn != null) conn.close();
            }

            // 4. Set attributes and forward to JSP
            request.setAttribute("reviewList", reviewList);
            request.setAttribute("techManagerId", techManagerId);
            
            // Check for messages
            String message = request.getParameter("message");
            if (message != null) {
                request.setAttribute("message", message);
            }

            request.getRequestDispatcher("/view/techmanager/diagnosis-review.jsp")
                   .forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error loading diagnosis review: " + e.getMessage());
        }
    }

    /**
     * DTO class to combine TaskAssignment and VehicleDiagnostic for display
     */
    public static class DiagnosisReviewDTO {
        private TaskAssignment task;
        private VehicleDiagnostic diagnostic;

        public TaskAssignment getTask() {
            return task;
        }

        public void setTask(TaskAssignment task) {
            this.task = task;
        }

        public VehicleDiagnostic getDiagnostic() {
            return diagnostic;
        }

        public void setDiagnostic(VehicleDiagnostic diagnostic) {
            this.diagnostic = diagnostic;
        }

        public boolean hasDiagnostic() {
            return diagnostic != null;
        }
    }
}

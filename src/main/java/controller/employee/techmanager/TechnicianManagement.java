package controller.employee.techmanager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import dao.employee.EmployeeDAO;
import model.dto.TechnicianDTO;
import model.employee.Employee;

/**
 * Team Management - View All Technicians
 * 
 * Tech Manager views ALL technicians in the system (both Active & Inactive).
 * Not limited to technicians under their management.
 * 
 * @author SWP391 Team
 * @version 2.0
 */
@WebServlet("/techmanager/team-management")
public class TechnicianManagement extends HttpServlet {

    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        employeeDAO = new EmployeeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // DEBUG: Print all session attributes
        System.out.println("[MyTeamServlet DEBUG] ========== SESSION ATTRIBUTES ==========");
        java.util.Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attrName = attributeNames.nextElement();
            Object attrValue = session.getAttribute(attrName);
            System.out.println("[MyTeamServlet DEBUG] " + attrName + " = " + attrValue);
        }
        System.out.println("[MyTeamServlet DEBUG] ======================================");

        try {
            // Get current Tech Manager's Employee record
            Integer userId = (Integer) session.getAttribute("userID");

            // Check if userID is null (try alternative attribute name)
            if (userId == null) {
                userId = (Integer) session.getAttribute("userId");
            }

            System.out.println("[MyTeamServlet DEBUG] Session userId: " + userId);

            if (userId == null) {
                request.setAttribute("errorMessage", "User ID not found in session. Please login again.");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            Employee currentEmployee = employeeDAO.getEmployeeByUserId(userId);

            System.out.println("[MyTeamServlet DEBUG] Current Employee: " +
                    (currentEmployee != null ? "ID=" + currentEmployee.getEmployeeId() : "NULL"));

            if (currentEmployee == null) {
                request.setAttribute("errorMessage", "Employee record not found for User ID: " + userId);
                request.getRequestDispatcher("/view/techmanager/team-management.jsp").forward(request, response);
                return;
            }

            // Fetch ALL technicians in the system (not filtered by ManagedBy)
            System.out.println("[MyTeamServlet DEBUG] Fetching ALL technicians in system");

            List<TechnicianDTO> technicians = employeeDAO.getAllTechniciansDebug();

            System.out.println("[MyTeamServlet DEBUG] Total technicians found: " + technicians.size());

            // Calculate statistics
            long activeCount = technicians.stream().filter(TechnicianDTO::isActive).count();
            long inactiveCount = technicians.size() - activeCount;

            System.out.println("[MyTeamServlet DEBUG] Active: " + activeCount + ", Inactive: " + inactiveCount);

            // Set attributes
            request.setAttribute("technicians", technicians);
            request.setAttribute("totalTechnicians", technicians.size());
            request.setAttribute("activeTechnicians", activeCount);
            request.setAttribute("inactiveTechnicians", inactiveCount);

            // Forward to JSP
            request.getRequestDispatcher("/view/techmanager/team-management.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/view/techmanager/team-management.jsp").forward(request, response);
        }
    }
}

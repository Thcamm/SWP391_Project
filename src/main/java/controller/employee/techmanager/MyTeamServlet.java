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
 * UC-TM-11: My Team - View Technician Team
 * 
 * Tech Manager views all technicians under their management (both Active &
 * Inactive).
 * 
 * @author SWP391 Team
 * @version 1.0
 */
@WebServlet("/techmanager/my-team")
public class MyTeamServlet extends HttpServlet {

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

        try {
            // Get current Tech Manager's Employee record
            Integer userId = (Integer) session.getAttribute("userID");

            // Check if userID is null (try alternative attribute name)
            if (userId == null) {
                userId = (Integer) session.getAttribute("userId");
            }

            if (userId == null) {
                request.setAttribute("errorMessage", "User ID not found in session. Please login again.");
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            Employee currentEmployee = employeeDAO.getEmployeeByUserId(userId);

            if (currentEmployee == null) {
                request.setAttribute("errorMessage", "Employee record not found for User ID: " + userId);
                request.getRequestDispatcher("/view/techmanager/my-team.jsp").forward(request, response);
                return;
            }

            // Fetch all technicians managed by this TM
            List<TechnicianDTO> technicians = employeeDAO.getAllTechniciansByManager(
                    currentEmployee.getEmployeeId());

            // Calculate statistics
            long activeCount = technicians.stream().filter(TechnicianDTO::isActive).count();
            long inactiveCount = technicians.size() - activeCount;

            // Set attributes
            request.setAttribute("technicians", technicians);
            request.setAttribute("totalTechnicians", technicians.size());
            request.setAttribute("activeTechnicians", activeCount);
            request.setAttribute("inactiveTechnicians", inactiveCount);
            request.setAttribute("currentManagerName", session.getAttribute("fullName"));

            // Forward to JSP
            request.getRequestDispatcher("/view/techmanager/my-team.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/view/techmanager/my-team.jsp").forward(request, response);
        }
    }
}

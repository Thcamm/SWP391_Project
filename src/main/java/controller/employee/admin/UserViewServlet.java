package controller.employee.admin;

// Required imports from Jakarta Servlet API

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import service.employee.AdminService;
import model.employee.admin.UserDisplay;
import java.io.IOException;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller: The package name of the Servlet.
 * - UserViewServlet: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns
 * /UserViewServlet and /UserViewServlet/*.
 */
@WebServlet("/admin/users/view/*")
public class UserViewServlet extends BaseAdminServlet {
    /**
     * Handles HTTP GET requests.
     * Typically used to retrieve data or display a user interface.
     */
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user ID");
            return;
        }

        try {
            String userIdStr = pathInfo.substring(1);
            int userId = Integer.parseInt(userIdStr);

            UserDisplay user = adminService.getUserById(userId);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("currentUser", getCurrentUser(request));
            request.getRequestDispatcher("/view/admin/user-details.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format");
        } catch (Exception e) {
            handleError(request, response, "Error viewing user: " + e.getMessage());
        }
    }

    /**
     * Handles HTTP POST requests.
     * Typically used to receive form data and execute business logic (e.g., saving
     * to a database).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "POST method is not supported.");
    }
}

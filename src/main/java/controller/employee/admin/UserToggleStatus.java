package controller.employee.admin;

// Required imports from Jakarta Servlet API

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.employee.admin.UserDisplay;
import service.employee.AdminService;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserToggleStatus: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns
 * /UserToggleStatus and /UserToggleStatus/*.
 */
@WebServlet("/admin/users/toggle/*")
public class UserToggleStatus extends BaseAdminServlet {
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        this.adminService = new AdminService();
    }

    /**
     * Handles HTTP GET requests.
     * Typically used to retrieve data or display a user interface.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not supported. Use POST.");
    }

    /**
     * Handles HTTP POST requests.
     * Typically used to receive form data and execute business logic (e.g., saving
     * to a database).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String redirectUrl = request.getContextPath() + "/admin/users";

        if (pathInfo == null || pathInfo.equals("/")) {
            redirectWithMessage(response, redirectUrl, "Missing user ID", "error");
            return;
        }

        try {
            String userIdStr = pathInfo.substring(1);
            int userId = Integer.parseInt(userIdStr);
            String currentUser = getCurrentUser(request);

            // Lấy trạng thái hiện tại để đảo ngược
            UserDisplay user = adminService.getUserById(userId);
            if (user == null) {
                redirectWithMessage(response, redirectUrl, "User not found", "error");
                return;
            }

            boolean newStatus = !user.isActiveStatus(); // Đảo ngược trạng thái
            if (currentUser != null && currentUser.equals(user.getUserName()) && !newStatus) {
                redirectWithMessage(response, redirectUrl, "Không thể vô hiệu hóa chính mình", "error");
                return;
            }

            boolean success = adminService.toggleUserStatus(userId, newStatus, currentUser);

            String message = success ? "Changed user status successfully" : "Cannot change user status";
            String messageType = success ? "success" : "error";
            redirectWithMessage(response, redirectUrl, message, messageType);

        } catch (NumberFormatException e) {
            redirectWithMessage(response, redirectUrl, "Invalid user ID", "error");
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(response, redirectUrl, "System error: " + e.getMessage(), "error");
        }
    }
}

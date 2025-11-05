package controller.employee.admin;

// Required imports from Jakarta Servlet API

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
// no direct HttpServlet import needed (extends BaseAdminServlet)
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.employee.admin.UserDisplay;
import model.employee.admin.rbac.Role;
import service.employee.AdminService;
import java.util.ArrayList;
import java.io.IOException;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserEditServlet: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns
 * /UserEditServlet and /UserEditServlet/*.
 */
@WebServlet("/admin/users/edit/*")
public class UserEditServlet extends BaseAdminServlet {

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

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user ID");
            return;
        }

        try {
            String userIdStr = pathInfo.substring(1); // Bỏ dấu "/"
            int userId = Integer.parseInt(userIdStr);

            UserDisplay user = adminService.getUserById(userId);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            ArrayList<Role> availableRoles = adminService.getAvailableRoles();

            request.setAttribute("user", user);
            request.setAttribute("availableRoles", availableRoles);
            request.setAttribute("currentUser", getCurrentUser(request));

            // Make sure any redirect messages are surfaced to the JSP
            handleMessages(request);

            request.getRequestDispatcher("/view/admin/user-edit.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        } catch (Exception e) {
            handleError(request, response, "Error when editing user: " + e.getMessage());
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
        request.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing user ID");
            return;
        }

        String userIdStr = pathInfo.substring(1); // Bỏ dấu "/"
        String redirectUrl = request.getContextPath() + "/admin/users/edit/" + userIdStr;

        try {
            int userId = Integer.parseInt(userIdStr);
            String currentUser = getCurrentUser(request);
            String fullName = request.getParameter("fullName");
            String emailParam = request.getParameter("email");
            String email = isNullOrEmpty(emailParam) ? null : emailParam.trim();
            String phoneNumberParam = request.getParameter("phoneNumber");
            String phoneNumber = isNullOrEmpty(phoneNumberParam) ? null : phoneNumberParam.trim();
            // The form uses 'roleId' as the select name
            String roleParam = request.getParameter("roleId");

            // Validate required fields: fullName and roleId. Email and phone are optional.
            if (isNullOrEmpty(fullName) || isNullOrEmpty(roleParam)) {
                redirectWithMessage(response, redirectUrl, "Please fill in all required fields!", "error");
                return;
            }

            Integer newRoleId = parseIntParameter(roleParam);
            if (newRoleId == null || newRoleId <= 0) {
                redirectWithMessage(response, redirectUrl, "Role ID is invalid!", "error");
                return;
            }

            // Read active status from form (select returns "true" or "false")
            String activeParam = request.getParameter("activeStatus");
            boolean activeStatus = "true".equalsIgnoreCase(activeParam);

            try {
                boolean success = adminService.updateUserBasicInfo(
                        userId,
                        fullName.trim(),
                        email,
                        phoneNumber,
                        newRoleId,
                        activeStatus,
                        currentUser);

                if (success) {
                    // Redirect back to the edit page so the user sees fresh values
                    redirectWithMessage(response, redirectUrl, "Update user information successfully!", "success");
                }
            } catch (Exception e) {
                // Catch specific error from AdminService
                String errorMessage = e.getMessage();
                if (errorMessage == null || errorMessage.isEmpty()) {
                    errorMessage = "Update user failed!";
                }
                redirectWithMessage(response, redirectUrl, errorMessage, "error");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(response, request.getContextPath() + "/admin/users",
                    "System error: " + e.getMessage(), "error");
        }
    }
}

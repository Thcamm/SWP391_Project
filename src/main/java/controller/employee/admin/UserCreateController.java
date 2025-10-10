package controller.employee.admin;

// Required imports from Jakarta Servlet API

import service.employee.AdminService;
import model.rbac.Role;
import java.util.ArrayList;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserCreateController: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns
 * /UserCreateController and /UserCreateController/*.
 */
@WebServlet("/admin/users/create")
public class UserCreateController extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        this.adminService = new AdminService();
        System.out.println("UserCreateController initialized at " + java.time.LocalDateTime.now());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get current user from session
        String currentUser = getCurrentUser(request);

        // Check if user is logged in
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Permission check
        if (!adminService.isAdmin(currentUser)) {
            request.setAttribute("errorMessage", "Bạn không có quyền truy cập trang này!");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        try {
            ArrayList<Role> availableRoles = adminService.getAvailableRoles();

            // Set attributes for JSP
            request.setAttribute("availableRoles", availableRoles);
            request.setAttribute("currentUser", currentUser);

            // Handle messages from redirect
            String message = request.getParameter("message");
            String messageType = request.getParameter("messageType");
            if (message != null) {
                request.setAttribute("message", message);
                request.setAttribute("messageType", messageType);
            }

            System.out.println("UserCreateController - Displaying create form for user: " + currentUser);

            // Forward to create JSP
            request.getRequestDispatcher("/admin/create-user.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in UserCreateController GET: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi tải trang tạo user: " + e.getMessage());
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Get current user from session
        String currentUser = getCurrentUser(request);

        // Check if user is logged in
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Get form parameters
            String fullName = request.getParameter("fullName");
            String userName = request.getParameter("userName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String roleParam = request.getParameter("role");

            // Validate required fields
            if (isNullOrEmpty(fullName) || isNullOrEmpty(userName) ||
                    isNullOrEmpty(email) || isNullOrEmpty(roleParam)) {

                redirectWithMessage(response, request.getContextPath() + "/admin/users/create",
                        "Vui lòng điền đầy đủ thông tin bắt buộc!", "error");
                return;
            }

            // Parse and validate role
            int roleId;
            try {
                roleId = Integer.parseInt(roleParam);
                if (roleId <= 0 || roleId > 5) {
                    redirectWithMessage(response, request.getContextPath() + "/admin/users/create",
                            "Role ID không hợp lệ!", "error");
                    return;
                }
            } catch (NumberFormatException e) {
                redirectWithMessage(response, request.getContextPath() + "/admin/users/create",
                        "Role ID không hợp lệ!", "error");
                return;
            }

            // Create user
            boolean success = adminService.createUser(fullName.trim(), userName.trim(),
                    email.trim(), roleId, currentUser);

            if (success) {
                System.out.println(
                        "UserCreateController - User created successfully: " + userName + " by " + currentUser);
                redirectWithMessage(response, request.getContextPath() + "/admin/users/",
                        "Đã tạo user mới thành công! Username: " + userName +
                                ", Mật khẩu: 123456",
                        "success");
            } else {
                System.out.println("UserCreateController - User creation failed: " + userName);
                redirectWithMessage(response, request.getContextPath() + "/admin/users/create",
                        "Tạo user thất bại! Username có thể đã tồn tại.", "error");
            }

        } catch (Exception e) {
            System.err.println("Error in UserCreateController POST: " + e.getMessage());
            e.printStackTrace();
            redirectWithMessage(response, request.getContextPath() + "/admin/users/create",
                    "Có lỗi xảy ra: " + e.getMessage(), "error");
        }
    }

    // ===== HELPER METHODS =====

    /**
     * Get current user from session
     */
    private String getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            String userName = (String) session.getAttribute("userName");
            if (userName != null && !userName.trim().isEmpty()) {
                return userName;
            }
        }

        return null;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void redirectWithMessage(HttpServletResponse response, String url,
            String message, String messageType) throws IOException {
        String redirectUrl = url + "?message=" + java.net.URLEncoder.encode(message, "UTF-8") +
                "&messageType=" + messageType;
        response.sendRedirect(redirectUrl);
    }
}
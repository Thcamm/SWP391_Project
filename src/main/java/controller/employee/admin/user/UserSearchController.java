package controller.employee.admin.user;

// Required imports from Jakarta Servlet API

import service.employee.AdminService;
import model.employee.admin.rbac.Role;
import java.util.ArrayList;
import model.employee.admin.UserDisplay;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserSearchController: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns
 * /UserSearchController and /UserSearchController/*.
 */
@WebServlet("/admin/users")
public class UserSearchController extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.adminService = new AdminService();
        System.out.println(" UserSearchController initialized at " + java.time.LocalDateTime.now());
        System.out.println(" UserSearchController servlet mapping: /admin/users");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("UserSearchController.doGet() CALLED! URL: " + request.getRequestURL());
        System.out.println("Context Path: " + request.getContextPath());
        System.out.println("Servlet Path: " + request.getServletPath());
        System.out.println("Request URI: " + request.getRequestURI());

        String currentUser = getCurrentUser(request);

        // Check if user is logged in
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Permission check
        if (!adminService.isAdmin(currentUser)) {
            handleUnauthorized(request, response);
            return;
        }

        try {
            // Parse search and filter parameters
            String keyword = request.getParameter("keyword");
            String roleParam = request.getParameter("role");
            String statusParam = request.getParameter("status");
            String sortBy = request.getParameter("sort"); // New sort parameter

            Integer roleId = parseIntParameter(roleParam);
            Boolean activeStatus = parseStatusParameter(statusParam);

            // Use the new search method with sort parameter
            System.out.println("🔧 DEBUG: Using search with sort parameter: " + sortBy);
            ArrayList<UserDisplay> searchResults = adminService.searchUsers(keyword, roleId, activeStatus, sortBy);
            int totalResults = searchResults.size();

            ArrayList<Role> availableRoles = adminService.getAvailableRoles();

            int activeUsersCount = adminService.getActiveUsersCount();
            int inactiveUsersCount = adminService.getInactiveUsersCount();
            int adminUsersCount = adminService.getAdminUsersCount();

            setRequestAttributes(request, searchResults, totalResults, availableRoles,
                    keyword, roleId, statusParam, currentUser,
                    activeUsersCount, inactiveUsersCount, adminUsersCount, sortBy);

            handleMessages(request);

            System.out.println("UserSearchController - User: " + currentUser +
                    ", Results: " + searchResults.size() + "/" + totalResults);

            // Forward to users.jsp
            request.getRequestDispatcher("/admin/users.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in UserSearchController GET: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Lỗi tải trang quản lý users: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String currentUser = getCurrentUser(request);

        // Check if user is logged in
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            String message;
            String messageType;

            switch (action != null ? action : "") {
                case "search":
                    handleSearchFormSubmission(request, response);
                    return;

                case "quickFilter":
                    handleQuickFilter(request, response);
                    return;

                default:
                    message = "Hành động không hợp lệ từ trang users!";
                    messageType = "error";
                    System.out.println("Invalid action in UserSearchController: " + action);
            }

            redirectWithMessage(response, request.getContextPath() + "/admin/users/",
                    message, messageType);

        } catch (Exception e) {
            System.err.println("Error in UserSearchController POST: " + e.getMessage());
            e.printStackTrace();
            redirectWithMessage(response, request.getContextPath() + "/admin/users/",
                    "Có lỗi xảy ra: " + e.getMessage(), "error");
        }
    }

    // ===== HELPER METHODS =====

    private String getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            String userName = (String) session.getAttribute("userName");
            if (userName != null && !userName.trim().isEmpty()) {
                System.out.println("Current user from session: " + userName);
                return userName;
            }

        }

        System.out.println("No user logged in - session is null or empty");
        return null;
    }

    private void handleUnauthorized(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", "Bạn không có quyền truy cập trang quản lý users!");
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    private void handleError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/error.jsp").forward(request, response);
    }

    private Integer parseIntParameter(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseStatusParameter(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        return "active".equalsIgnoreCase(param);
    }

    private void setRequestAttributes(HttpServletRequest request, ArrayList<UserDisplay> users,
            int totalResults, ArrayList<Role> roles, String keyword,
            Integer roleId, String status, String currentUser,
            int activeCount, int inactiveCount, int adminCount, String sortBy) {

        request.setAttribute("users", users);
        request.setAttribute("totalResults", totalResults);
        request.setAttribute("availableRoles", roles);
        request.setAttribute("currentUser", currentUser);

        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("selectedRole", roleId);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("currentSort", sortBy);

        request.setAttribute("activeUsersCount", activeCount);
        request.setAttribute("inactiveUsersCount", inactiveCount);
        request.setAttribute("adminUsersCount", adminCount);

        boolean hasSearchCriteria = (keyword != null && !keyword.trim().isEmpty()) ||
                (roleId != null) || (status != null);
        request.setAttribute("hasSearchCriteria", hasSearchCriteria);
    }

    private void handleMessages(HttpServletRequest request) {
        String message = request.getParameter("message");
        String messageType = request.getParameter("messageType");

        if (message != null) {
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType != null ? messageType : "info");
        }
    }

    private void handleSearchFormSubmission(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String keyword = request.getParameter("keyword");
        String role = request.getParameter("role");
        String status = request.getParameter("status");

        StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/admin/users/?");
        boolean hasParams = false;

        if (keyword != null && !keyword.trim().isEmpty()) {
            redirectUrl.append("keyword=").append(java.net.URLEncoder.encode(keyword, "UTF-8"));
            hasParams = true;
        }

        if (role != null && !role.trim().isEmpty()) {
            if (hasParams)
                redirectUrl.append("&");
            redirectUrl.append("role=").append(role);
            hasParams = true;
        }

        if (status != null && !status.trim().isEmpty()) {
            if (hasParams)
                redirectUrl.append("&");
            redirectUrl.append("status=").append(status);
        }

        response.sendRedirect(redirectUrl.toString());
    }

    private void handleQuickFilter(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String filterType = request.getParameter("filterType");
        String filterValue = request.getParameter("filterValue");

        String redirectUrl = request.getContextPath() + "/admin/users/?" + filterType + "=" + filterValue;
        response.sendRedirect(redirectUrl);
    }

    private void redirectWithMessage(HttpServletResponse response, String url,
            String message, String messageType) throws IOException {
        String redirectUrl = url + "?message=" + java.net.URLEncoder.encode(message, "UTF-8") +
                "&messageType=" + messageType;
        response.sendRedirect(redirectUrl);
    }
}
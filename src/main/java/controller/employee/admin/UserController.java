package controller.employee.admin;

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
 * Unified User Management Controller for Jakarta EE (Using Jakarta Servlet API
 * 6.0+).
 * Handles all user-related operations:
 * - /admin/users - List all users with search/filter
 * - /admin/users/create - Create new user (GET: form, POST: process)
 * - /admin/users/view/{id} - View user details
 * - /admin/users/edit/{id} - Edit user (GET: form, POST: process)
 * - /admin/users/toggle/{id} - Toggle user status
 */
@WebServlet({ "/admin/users", "/admin/users/*" })
public class UserController extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.adminService = new AdminService();
        System.out.println("UserController initialized at " + java.time.LocalDateTime.now());
        System.out.println("UserController servlet mapping: /admin/users, /admin/users/*");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        System.out.println("UserController.doGet() CALLED!");
        System.out.println("   Full URL: " + request.getRequestURL());
        System.out.println("   Path Info: " + pathInfo);
        System.out.println("   Servlet Path: " + request.getServletPath());

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
            // Route based on path
            if (pathInfo == null || pathInfo.equals("/")) {
                // Default: Show user list
                handleUserList(request, response);
            } else if (pathInfo.equals("/create")) {
                // Show create user form
                handleCreateForm(request, response);
            } else if (pathInfo.startsWith("/view/")) {
                // View user details
                handleViewUser(request, response, pathInfo);
            } else if (pathInfo.startsWith("/edit/")) {
                // Edit user form
                handleEditUser(request, response, pathInfo);
            } else {
                // Invalid path
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in UserController GET: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Lỗi xử lý: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String pathInfo = request.getPathInfo();
        String currentUser = getCurrentUser(request);

        // Check if user is logged in
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Route based on path
            if (pathInfo == null || pathInfo.equals("/")) {
                // Handle search/filter from main page
                handleSearch(request, response);
            } else if (pathInfo.equals("/create")) {
                // Process create user
                handleCreateUser(request, response);
            } else if (pathInfo.startsWith("/edit/")) {
                // Process edit user
                handleUpdateUser(request, response, pathInfo);
            } else if (pathInfo.startsWith("/toggle/")) {
                // Toggle user status
                handleToggleStatus(request, response, pathInfo);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error in UserController POST: " + e.getMessage());
            e.printStackTrace();
            redirectWithMessage(response, request.getContextPath() + "/admin/users",
                    "Có lỗi xảy ra: " + e.getMessage(), "error");
        }
    }

    // ===== HANDLER METHODS =====

    /**
     * HANDLE USER LIST: Display all users with search/filter and pagination
     */
    private void handleUserList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Parse search and filter parameters
        String keyword = request.getParameter("keyword");
        String roleParam = request.getParameter("role");
        String statusParam = request.getParameter("status");
        String sortBy = request.getParameter("sort");

        // Parse pagination parameters
        String pageParam = request.getParameter("page");
        String sizeParam = request.getParameter("size");

        int currentPage = parseIntParameter(pageParam) != null ? parseIntParameter(pageParam) : 1;
        int itemsPerPage = parseIntParameter(sizeParam) != null ? parseIntParameter(sizeParam) : 10;

        Integer roleId = parseIntParameter(roleParam);
        Boolean activeStatus = parseStatusParameter(statusParam);

        // Search users with pagination
        System.out.println("DEBUG: Using search with pagination - Page: " + currentPage + ", Size: " + itemsPerPage);
        common.utils.PaginationUtils.PaginationResult<UserDisplay> paginationResult = adminService
                .searchUsersWithPagination(keyword, roleId, activeStatus, sortBy, currentPage, itemsPerPage);

        ArrayList<UserDisplay> searchResults = new ArrayList<>(paginationResult.getPaginatedData());
        int totalResults = paginationResult.getTotalItems();

        ArrayList<Role> availableRoles = adminService.getAvailableRoles();

        int activeUsersCount = adminService.getActiveUsersCount();
        int inactiveUsersCount = adminService.getInactiveUsersCount();
        int adminUsersCount = adminService.getAdminUsersCount();

        setRequestAttributes(request, searchResults, totalResults, availableRoles,
                keyword, roleId, statusParam, getCurrentUser(request),
                activeUsersCount, inactiveUsersCount, adminUsersCount, sortBy);

        // Set pagination attributes
        request.setAttribute("currentPage", paginationResult.getCurrentPage());
        request.setAttribute("totalPages", paginationResult.getTotalPages());
        request.setAttribute("itemsPerPage", paginationResult.getItemsPerPage());
        request.setAttribute("hasNextPage", paginationResult.getCurrentPage() < paginationResult.getTotalPages());
        request.setAttribute("hasPrevPage", paginationResult.getCurrentPage() > 1);

        handleMessages(request);

        System.out.println("UserController - User: " + getCurrentUser(request) +
                ", Results: " + searchResults.size() + "/" + totalResults);

        // Forward to users.jsp
        request.getRequestDispatcher("/admin/users.jsp")
                .forward(request, response);
    }

    /**
     * HANDLE CREATE FORM: Show create user form
     */
    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ArrayList<Role> availableRoles = adminService.getAvailableRoles();

        // Set attributes for JSP
        request.setAttribute("availableRoles", availableRoles);
        request.setAttribute("currentUser", getCurrentUser(request));

        // Handle messages from redirect
        handleMessages(request);

        System.out.println("UserController - Displaying create form for user: " + getCurrentUser(request));

        // Forward to create JSP
        request.getRequestDispatcher("/admin/create-user.jsp")
                .forward(request, response);
    }

    /**
     * HANDLE VIEW USER: Show user details
     */
    private void handleViewUser(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {
        try {
            String userIdStr = pathInfo.substring("/view/".length());
            int userId = Integer.parseInt(userIdStr);

            UserDisplay user = adminService.getUserById(userId);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            request.setAttribute("user", user);
            request.setAttribute("currentUser", getCurrentUser(request));
            request.getRequestDispatcher("/admin/user-details.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }

    /**
     * HANDLE EDIT USER: Show edit form
     */
    private void handleEditUser(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws ServletException, IOException {
        try {
            String userIdStr = pathInfo.substring("/edit/".length());
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
            request.getRequestDispatcher("/admin/user-edit.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID");
        }
    }

    /**
     * HANDLE SEARCH: Process search form submission
     */
    private void handleSearch(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");

        if ("search".equals(action)) {
            handleSearchFormSubmission(request, response);
        } else if ("quickFilter".equals(action)) {
            handleQuickFilter(request, response);
        } else {
            // Default: redirect to user list
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    /**
     * HANDLE CREATE USER: Process create user form
     */
    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Get form parameters
        String fullName = request.getParameter("fullName");
        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        // String phoneNumber = request.getParameter("phoneNumber"); // TODO: Add phone
        // number support
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
            if (roleId <= 0) {
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
                email.trim(), roleId, getCurrentUser(request));

        if (success) {
            System.out.println("UserController - User created successfully: " + userName +
                    " by " + getCurrentUser(request));
            redirectWithMessage(response, request.getContextPath() + "/admin/users",
                    "Đã tạo user mới thành công! Username: " + userName + ", Mật khẩu: 123456",
                    "success");
        } else {
            System.out.println("UserController - User creation failed: " + userName);
            redirectWithMessage(response, request.getContextPath() + "/admin/users/create",
                    "Tạo user thất bại! Username có thể đã tồn tại.", "error");
        }
    }

    /**
     * HANDLE UPDATE USER: Process edit user form
     */
    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
        try {
            String userIdStr = pathInfo.substring("/edit/".length());
            int userId = Integer.parseInt(userIdStr);

            // TODO: Implement user update functionality
            boolean success = false;

            redirectWithMessage(response, request.getContextPath() + "/admin/users",
                    "User update functionality coming soon!", "info");

            if (success) {
                redirectWithMessage(response, request.getContextPath() + "/admin/users",
                        "Cập nhật user thành công!", "success");
            } else {
                redirectWithMessage(response, request.getContextPath() + "/admin/users/edit/" + userId,
                        "Cập nhật user thất bại!", "error");
            }

        } catch (NumberFormatException e) {
            redirectWithMessage(response, request.getContextPath() + "/admin/users",
                    "Invalid user ID", "error");
        }
    }

    /**
     * HANDLE TOGGLE STATUS: Enable/disable user
     */
    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
        try {
            String userIdStr = pathInfo.substring("/toggle/".length());
            int userId = Integer.parseInt(userIdStr);

            String currentUser = getCurrentUser(request);
            // Get current user status first, then toggle it
            UserDisplay user = adminService.getUserById(userId);
            if (user == null) {
                redirectWithMessage(response, request.getContextPath() + "/admin/users",
                        "User not found", "error");
                return;
            }
            boolean newStatus = !user.isActiveStatus(); // Toggle current status
            boolean success = adminService.toggleUserStatus(userId, newStatus, currentUser);

            String message = success ? "Đã thay đổi trạng thái user" : "Không thể thay đổi trạng thái user";
            String messageType = success ? "success" : "error";

            redirectWithMessage(response, request.getContextPath() + "/admin/users", message, messageType);

        } catch (NumberFormatException e) {
            redirectWithMessage(response, request.getContextPath() + "/admin/users",
                    "Invalid user ID", "error");
        }
    }

    // ===== HELPER METHODS =====

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

        StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/admin/users?");
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

        String redirectUrl = request.getContextPath() + "/admin/users?" + filterType + "=" + filterValue;
        response.sendRedirect(redirectUrl);
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
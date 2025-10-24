package controller.employee.admin;

// Required imports from Jakarta Servlet API

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.employee.admin.UserDisplay;
import model.employee.admin.rbac.Role;
import service.employee.AdminService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserListServlet: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns
 * /UserListServlet and /UserListServlet/*.
 */
@WebServlet("/admin/users")
public class UserListServlet extends BaseAdminServlet {
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
        try {
            handleMessages(request); // Xử lý thông báo (nếu có)

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
            common.utils.PaginationUtils.PaginationResult<UserDisplay> paginationResult = adminService
                    .searchUsersWithPagination(keyword, roleId, activeStatus, sortBy, currentPage, itemsPerPage);

            ArrayList<UserDisplay> searchResults = new ArrayList<>(paginationResult.getPaginatedData());
            int totalResults = paginationResult.getTotalItems();

            ArrayList<Role> availableRoles = adminService.getAvailableRoles();

            // Lấy các số liệu thống kê
            int activeUsersCount = adminService.getActiveUsersCount();
            int inactiveUsersCount = adminService.getInactiveUsersCount();
            int adminUsersCount = adminService.getAdminUsersCount();

            // Set attributes for JSP
            setRequestAttributes(request, searchResults, totalResults, availableRoles,
                    keyword, roleId, statusParam, getCurrentUser(request),
                    activeUsersCount, inactiveUsersCount, adminUsersCount, sortBy);

            // Set pagination attributes
            request.setAttribute("currentPage", paginationResult.getCurrentPage());
            request.setAttribute("totalPages", paginationResult.getTotalPages());
            request.setAttribute("itemsPerPage", paginationResult.getItemsPerPage());
            request.setAttribute("hasNextPage", paginationResult.getCurrentPage() < paginationResult.getTotalPages());
            request.setAttribute("hasPrevPage", paginationResult.getCurrentPage() > 1);

            // Forward to users.jsp
            request.getRequestDispatcher("/view/admin/users.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in UserListServlet GET: " + e.getMessage());
            e.printStackTrace();
            handleError(request, response, "Lỗi khi tải danh sách user: " + e.getMessage());
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
        String action = request.getParameter("action");

        try {
            if ("search".equals(action)) {
                handleSearchFormSubmission(request, response);
            } else if ("quickFilter".equals(action)) {
                handleQuickFilter(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        } catch (Exception e) {
            System.err.println("Error in UserListServlet POST: " + e.getMessage());
            e.printStackTrace();
            redirectWithMessage(response, request.getContextPath() + "/admin/users",
                    "Có lỗi xảy ra: " + e.getMessage(), "error");
        }
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
                (roleId != null) || (status != null && !status.equals("all"));
        request.setAttribute("hasSearchCriteria", hasSearchCriteria);
    }

    private void handleSearchFormSubmission(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String keyword = request.getParameter("keyword");
        String role = request.getParameter("role");
        String status = request.getParameter("status");

        StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/admin/users?");
        boolean hasParams = false;

        if (!isNullOrEmpty(keyword)) {
            redirectUrl.append("keyword=").append(java.net.URLEncoder.encode(keyword, "UTF-8"));
            hasParams = true;
        }

        if (!isNullOrEmpty(role)) {
            if (hasParams)
                redirectUrl.append("&");
            redirectUrl.append("role=").append(role);
            hasParams = true;
        }

        if (!isNullOrEmpty(status)) {
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

        if (!isNullOrEmpty(filterType) && !isNullOrEmpty(filterValue)) {
            String redirectUrl = request.getContextPath() + "/admin/users?" + filterType + "=" + filterValue;
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }
}

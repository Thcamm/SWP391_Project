package controller.employee.admin;

// Required imports from Jakarta Servlet API

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import service.employee.AdminService;
import model.employee.admin.rbac.Role;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserCreateEmployeeServlet: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns /UserCreateEmployeeServlet and /UserCreateEmployeeServlet/*.
 */
@WebServlet("/admin/users/create-employee")
public class UserCreateEmployeeServlet extends BaseAdminServlet {
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
            ArrayList<Role> availableRoles = adminService.getAvailableRoles();
            request.setAttribute("availableRoles", availableRoles);
            request.setAttribute("currentUser", getCurrentUser(request));
            handleMessages(request); // Hiển thị lỗi nếu redirect từ POST

            request.getRequestDispatcher("/view/admin/create-employee.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            handleError(request, response, "Lỗi khi tải form: " + e.getMessage());
        }
    }

    /**
     * Handles HTTP POST requests.
     * Typically used to receive form data and execute business logic (e.g., saving to a database).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("fullName");
        String userName = request.getParameter("userName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String gender = request.getParameter("gender");
        String roleParam = request.getParameter("roleId");
        String employeeCode = request.getParameter("employeeCode");
        String salaryParam = request.getParameter("salary");
        String redirectUrl = request.getContextPath() + "/admin/users/create-employee";

        // Validate required fields
        if (isNullOrEmpty(fullName) || isNullOrEmpty(userName) || isNullOrEmpty(email) || isNullOrEmpty(roleParam)) {
            redirectWithMessage(response, redirectUrl, "Vui lòng điền đầy đủ thông tin bắt buộc!", "error");
            return;
        }

        // Parse and validate role
        Integer roleId = parseIntParameter(roleParam);
        if (roleId == null || roleId <= 0) {
            redirectWithMessage(response, redirectUrl, "Role ID không hợp lệ!", "error");
            return;
        }

        // Parse salary
        Double salary = null;
        if (!isNullOrEmpty(salaryParam)) {
            try {
                salary = Double.parseDouble(salaryParam);
                if (salary < 0) {
                    redirectWithMessage(response, redirectUrl, "Lương không được âm!", "error");
                    return;
                }
            } catch (NumberFormatException e) {
                redirectWithMessage(response, redirectUrl, "Định dạng lương không hợp lệ!", "error");
                return;
            }
        }

        try {
            boolean success = adminService.createUser(fullName.trim(), userName.trim(),
                    email.trim(), roleId, gender, getCurrentUser(request),
                    employeeCode != null ? employeeCode.trim() : null, salary);

            if (success) {
                String message = "Đã tạo Employee thành công! Username: " + userName + ", Mật khẩu: 123456.";
                redirectWithMessage(response, request.getContextPath() + "/admin/users", message, "success");
            } else {
                redirectWithMessage(response, redirectUrl, "Tạo Employee thất bại! Username có thể đã tồn tại.", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(response, redirectUrl, "Lỗi hệ thống: " + e.getMessage(), "error");
        }
    }
}

package controller.employee.admin;

// Required imports from Jakarta Servlet API

import controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import service.employee.AdminService;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserCreateCustomerServlet: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns /UserCreateCustomerServlet and /UserCreateCustomerServlet/*.
 */
@WebServlet("/admin/users/create-customer")
public class UserCreateCustomerServlet extends BaseAdminServlet {

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

        request.setAttribute("currentUser", getCurrentUser(request));
        handleMessages(request); // Hiển thị lỗi nếu redirect từ POST

        request.getRequestDispatcher("/view/admin/create-customer.jsp")
                .forward(request, response);
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
        String gender = request.getParameter("gender");

        // Validate required fields
        if (isNullOrEmpty(fullName) || isNullOrEmpty(userName) || isNullOrEmpty(email)) {
            redirectWithMessage(response, request.getContextPath() + "/admin/users/create-customer",
                    "Vui lòng điền đầy đủ thông tin bắt buộc!", "error");
            return;
        }

        try {
            int customerRoleId = 7;
            String currentUser = getCurrentUser(request);

            boolean success = adminService.createUser(fullName.trim(), userName.trim(),
                    email.trim(), customerRoleId, gender, currentUser,
                    null, null); // No employee code or salary

            if (success) {
                String message = "Đã tạo Customer thành công! Username: " + userName + ", Mật khẩu: 123456";
                redirectWithMessage(response, request.getContextPath() + "/admin/users", message, "success");
            } else {
                redirectWithMessage(response, request.getContextPath() + "/admin/users/create-customer",
                        "Tạo Customer thất bại! Username có thể đã tồn tại.", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(response, request.getContextPath() + "/admin/users/create-customer",
                    "Lỗi hệ thống: " + e.getMessage(), "error");
        }
    }
}

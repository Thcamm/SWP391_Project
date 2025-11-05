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
import util.Validate;
import common.constant.IConstant;

/**
 * Basic Servlet Template for Jakarta EE (Using Jakarta Servlet API 6.0+).
 * * Placeholders to be replaced automatically by the IDE:
 * - controller.employee.admin: The package name of the Servlet.
 * - UserCreateEmployeeServlet: The class name of the Servlet.
 * * The @WebServlet annotation maps this Servlet to the URL patterns
 * /UserCreateEmployeeServlet and /UserCreateEmployeeServlet/*.
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
     * Typically used to receive form data and execute business logic (e.g., saving
     * to a database).
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
        String dobStr = request.getParameter("dob"); // Lấy ngày sinh
        String roleParam = request.getParameter("roleId");
        String employeeCode = request.getParameter("employeeCode");
        String salaryParam = request.getParameter("salary");
        String redirectUrl = request.getContextPath() + "/admin/users/create-employee";

        if (Validate.isNullOrEmpty(fullName) || Validate.isNullOrEmpty(userName) || Validate.isNullOrEmpty(roleParam)) {
            redirectWithMessage(response, redirectUrl, "Please fill in all required fields.", "error");
            return;
        }

        if (!Validate.isValidUsername(userName)) {
            redirectWithMessage(response, redirectUrl,
                    "Username must be 3-20 characters long and cannot contain special characters or spaces.", "error");
            return;
        }

        if (!Validate.isValidEmail(email)) {
            redirectWithMessage(response, redirectUrl, "Invalid email format!", "error");
            return;
        }

        if (!Validate.isValidPhoneNumber(phoneNumber)) {
            redirectWithMessage(response, redirectUrl,
                    "Invalid phone number format (must be 10-11 digits, starting with 0)!", "error");
            return;
        }

        if (!Validate.isValidEmployeeCode(employeeCode)) {
            redirectWithMessage(response, redirectUrl,
                    "Invalid employee code (must be 2-10 characters, no special characters)!", "error");
            return;
        }

        if (!Validate.isLengthValid(fullName, IConstant.MAX_FULLNAME_LENGTH)) {
            redirectWithMessage(response, redirectUrl,
                    "Full name must not exceed " + IConstant.MAX_FULLNAME_LENGTH + " characters!", "error");
            return;
        }

        if (!Validate.isValidGender(gender)) {
            redirectWithMessage(response, redirectUrl, "Invalid gender value!", "error");
            return;
        }

        if (!Validate.isValidDateOfBirth(dobStr)) {
            redirectWithMessage(response, redirectUrl,
                    "Invalid date of birth (must be in the format "
                            + IConstant.DATE_FORMAT + ", cannot be in the future or more than 100 years ago)!",
                    "error");
            return;
        }

        Integer roleId = Validate.parseInteger(roleParam);
        if (!Validate.isPositive(roleId)) {
            redirectWithMessage(response, redirectUrl, "Invalid role ID!", "error");
            return;
        }

        Double salary = null;
        if (!Validate.isNullOrEmpty(salaryParam)) {
            salary = Validate.parseDouble(salaryParam);

            if (salary == null) {
                redirectWithMessage(response, redirectUrl, "Invalid salary format (must be a number)!", "error");
                return;
            }
            if (!Validate.isNonNegative(salary)) {
                redirectWithMessage(response, redirectUrl, "Salary must not be negative!", "error");
                return;
            }
        }

        try {
            // Check duplicate username
            if (adminService.isUsernameExists(userName.trim())) {
                redirectWithMessage(response, redirectUrl,
                        "Username '" + userName + "' already exists! Please choose another username.", "error");
                return;
            }

            // Check duplicate email
            if (adminService.isEmailExists(email.trim())) {
                redirectWithMessage(response, redirectUrl,
                        "Email '" + email + "' is already registered! Please use another email.", "error");
                return;
            }

            String generatedPassword = adminService.createUser(
                    fullName.trim(),
                    userName.trim(),
                    email.trim(),
                    roleId,
                    gender,
                    getCurrentUser(request),
                    !Validate.isNullOrEmpty(employeeCode) ? employeeCode.trim() : null,
                    salary);

            if (generatedPassword != null) {
                String message = "Create employee successfully! Username: " + userName + ", Password: "
                        + generatedPassword;
                redirectWithMessage(response, request.getContextPath() + "/admin/users", message, "success");
            } else {
                redirectWithMessage(response, redirectUrl, "Creation failed! Username or Email may already exist.",
                        "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(response, redirectUrl, "System error: " + e.getMessage(), "error");
        }
    }
}

package controller.employee.admin;

import util.Validate;
import common.constant.IConstant;

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
 * * The @WebServlet annotation maps this Servlet to the URL patterns
 * /UserCreateCustomerServlet and /UserCreateCustomerServlet/*.
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
        String gender = request.getParameter("gender");
        String phoneNumber = request.getParameter("phoneNumber");
        String dobStr = request.getParameter("dob");

        String redirectUrl = request.getContextPath() + "/admin/users/create-customer";

        if (Validate.isNullOrEmpty(fullName) || Validate.isNullOrEmpty(userName) || Validate.isNullOrEmpty(email)) {
            redirectWithMessage(response, redirectUrl, "Please enter full name, username, and email.", "error");
            return;
        }
        if (!Validate.isValidUsername(userName)) {
            redirectWithMessage(response, redirectUrl,
                    "Username must be 3-20 characters long, without special characters or spaces!", "error");
            return;
        }

        if (!Validate.isValidEmail(email)) {
            redirectWithMessage(response, redirectUrl, "Invalid email format!", "error");
            return;
        }
        if (!Validate.isValidPhoneNumber(phoneNumber)) {
            redirectWithMessage(response, redirectUrl, "Invalid phone number format!", "error");
            return;
        }

        if (!Validate.isValidGender(gender)) {
            redirectWithMessage(response, redirectUrl, "Invalid gender value!", "error");
            return;
        }

        if (!Validate.isValidDateOfBirth(dobStr)) {
            redirectWithMessage(response, redirectUrl,
                    "Birth date must be in the format " + IConstant.DATE_FORMAT
                            + " and cannot be in the future or more than 100 years ago!",
                    "error");
            return;
        }

        if (!Validate.isLengthValid(fullName, IConstant.MAX_FULLNAME_LENGTH)) {
            redirectWithMessage(response, redirectUrl,
                    "Full name must not exceed " + IConstant.MAX_FULLNAME_LENGTH + " characters!", "error");
            return;
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

            int customerRoleId = 7;
            String currentUser = getCurrentUser(request);

            String generatedPassword = adminService.createUser(
                    fullName.trim(),
                    userName.trim(),
                    email.trim(),
                    customerRoleId,
                    gender,
                    currentUser,
                    null,
                    null
            // dobStr,
            // phoneNumber
            );

            if (generatedPassword != null) {
                String message = "Create Customer successfully! Username: " + userName + ", Password: "
                        + generatedPassword;
                redirectWithMessage(response, request.getContextPath() + "/admin/users", message, "success");
            } else {
                redirectWithMessage(response, redirectUrl,
                        "Create Customer failed! Username or Email may already exist.", "error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectWithMessage(response, redirectUrl, "System error: " + e.getMessage(), "error");
        }
    }
}

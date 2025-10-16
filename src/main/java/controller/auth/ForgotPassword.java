package controller.auth;

import dao.user.PasswordResetDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.user.User;
import dao.user.UserDAO;
import service.user.ResetPasswordService;
import util.MailService;

import java.io.IOException;

@WebServlet(name = "forgotpassword", urlPatterns = {"/forgotpassword"})
public class ForgotPassword extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("forgotpassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Please enter email\"}");
            return;
        }

        try {
            PasswordResetDAO resetDAO = new PasswordResetDAO();
            UserDAO userDAO = new UserDAO();
            MailService mailService = new MailService();

            ResetPasswordService service = new ResetPasswordService(resetDAO, userDAO, mailService);

            // Kiểm tra action sendOtp
            if ("sendOtp".equals(action)) {
                // Kiểm tra email tồn tại
                User user = userDAO.getUserByEmail(email);
                if (user == null) {
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"message\": \"Email not found\"}");
                    return;
                }

                // Lấy base URL
                String baseUrl = request.getScheme() + "://" +
                        request.getServerName() + ":" +
                        request.getServerPort() +
                        request.getContextPath();

                // Gửi email reset password
                boolean emailSent = service.requestPasswordReset(email, baseUrl);

                response.setContentType("application/json");
                if (emailSent) {
                    response.getWriter().write("{\"success\": true, \"message\": \"OTP sent successfully\"}");
                } else {
                    response.getWriter().write("{\"success\": false, \"message\": \"Failed to send email\"}");
                }
                return;
            }

            // Xử lý submit form reset password
            String otp = request.getParameter("otp");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");

            if (otp == null || password == null || confirmPassword == null) {
                request.setAttribute("error", "Please fill in all fields");
                request.getRequestDispatcher("forgotpassword.jsp").forward(request, response);
                return;
            }

            if (!password.equals(confirmPassword)) {
                request.setAttribute("error", "Passwords do not match");
                request.getRequestDispatcher("forgotpassword.jsp").forward(request, response);
                return;
            }

            // Validate OTP và reset password
            boolean success = service.resetPassword(otp, password);

            if (success) {
                request.setAttribute("success", "Password reset successfully. Please login with your new password.");
                response.sendRedirect("login.jsp");
            } else {
                request.setAttribute("error", "Invalid or expired OTP");
                request.getRequestDispatcher("forgotpassword.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }
}

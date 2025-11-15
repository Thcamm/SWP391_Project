package controller.auth;

import dao.user.PasswordResetDAO;
import dao.user.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.user.User;
import service.user.ResetPasswordService;
import util.MailService;
import util.PasswordUtil;

import java.io.IOException;
import java.security.SecureRandom;

@WebServlet(name = "forgotpassword", urlPatterns = {"/forgotpassword"})
public class ForgotPassword extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String FORGOT_PASSWORD_JSP = "/forgotpassword.jsp";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTR_EMAIL = "email";
    private static final int NEW_PASSWORD_LENGTH = 6;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String email = request.getParameter(ATTR_EMAIL);

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute(ATTR_ERROR_MESSAGE, "Please enter your email address");
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            ResetPasswordService service = new ResetPasswordService();

            if ("sendOTP".equals(action)) {
                handleSendOTP(request, response, email, service, userDAO);
            } else if ("verifyOTP".equals(action)) {
                handleVerifyOTPAndResetPassword(request, response, email, service, userDAO);
            } else {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Invalid request");
                request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ATTR_ERROR_MESSAGE, "An error occurred. Please try again later.");
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
        }
    }


    private void handleSendOTP(HttpServletRequest request, HttpServletResponse response,
                               String email, ResetPasswordService service, UserDAO userDAO)
            throws ServletException, IOException {
        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Email does not exist in the system");
                request.setAttribute(ATTR_EMAIL, email);
                request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
                return;
            }

            boolean otpSent = service.sendOTP(email);
            if (otpSent) {
                request.setAttribute(ATTR_SUCCESS_MESSAGE, "OTP code has been sent to your email. Please check your inbox.");
                request.setAttribute("otpSent", true);
            } else {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Unable to send OTP. Please try again later.");
            }
            request.setAttribute(ATTR_EMAIL, email);
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute(ATTR_ERROR_MESSAGE, "Error sending OTP. Please try again later.");
            request.setAttribute(ATTR_EMAIL, email);
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
        }
    }

    private void handleVerifyOTPAndResetPassword(HttpServletRequest request, HttpServletResponse response,
                                                  String email, ResetPasswordService service, UserDAO userDAO)
            throws ServletException, IOException {
        String otp = request.getParameter("otp");

        if (otp == null || otp.trim().isEmpty()) {
            request.setAttribute(ATTR_ERROR_MESSAGE, "Please enter OTP code");
            request.setAttribute(ATTR_EMAIL, email);
            request.setAttribute("otpSent", true);
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
            return;
        }

        try {
            // Verify OTP first
            PasswordResetDAO resetDAO = new PasswordResetDAO();
            if (!resetDAO.verifyOTP(email, otp.trim())) {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Invalid or expired OTP code. Please try again.");
                request.setAttribute(ATTR_EMAIL, email);
                request.setAttribute("otpSent", true);
                request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
                return;
            }

            // Generate new random password
            String newPassword = generateRandomPassword();
            String hashedPassword = PasswordUtil.hashPassword(newPassword);

            // Update password in database
            boolean updateSuccess = userDAO.updatePassword(email, hashedPassword);

            if (!updateSuccess) {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Failed to reset password. Please try again.");
                request.setAttribute(ATTR_EMAIL, email);
                request.setAttribute("otpSent", true);
                request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
                return;
            }

            // Mark OTP as used
            resetDAO.markOTPAsUsed(email, otp.trim());

            // Send new password via email
            User user = userDAO.getUserByEmail(email);
            sendNewPasswordEmail(email, user.getFullName(), newPassword);

            // Redirect to login with success message
            request.getSession().setAttribute(ATTR_SUCCESS_MESSAGE,
                "Password reset successful! Your new password has been sent to your email.");
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute(ATTR_ERROR_MESSAGE, "An error occurred while resetting password. Please try again later.");
            request.setAttribute(ATTR_EMAIL, email);
            request.setAttribute("otpSent", true);
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
        }
    }

    private String generateRandomPassword() {
        String upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "@#$%&*";
        String allChars = upperCase + lowerCase + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(NEW_PASSWORD_LENGTH);

        // Ensure at least one character from each category
        password.append(upperCase.charAt(random.nextInt(upperCase.length())));
        password.append(lowerCase.charAt(random.nextInt(lowerCase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Fill the rest randomly
        for (int i = 4; i < NEW_PASSWORD_LENGTH; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password
        char[] passwordArray = password.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }

    private void sendNewPasswordEmail(String email, String fullName, String newPassword) {
        String subject = "Your New Password - Garage Management System";
        String body = String.format(
                "Dear %s,\n\n" +
                        "Your password has been successfully reset.\n\n" +
                        "Your new password is: %s\n\n" +
                        "For security reasons, please login and change this password immediately.\n\n" +
                        "If you did not request this password reset, please contact us immediately.\n\n" +
                        "Best regards,\n" +
                        "Garage Management System",
                fullName != null ? fullName : "User",
                newPassword
        );

        try {
            MailService.sendEmail(email, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

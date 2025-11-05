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

@WebServlet(name = "forgotpassword", urlPatterns = {"/forgotpassword"})
public class ForgotPassword extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String FORGOT_PASSWORD_JSP = "/forgotpassword.jsp";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_EMAIL = "email";


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
            request.setAttribute(ATTR_ERROR_MESSAGE, "Vui lòng nhập email");
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            ResetPasswordService service = new ResetPasswordService();

            if ("send".equals(action)) {
                handleSendOTP(request, response, email, service, userDAO);
            } else if ("reset".equals(action)) {
                handleResetPassword(request, response, email, service);
            } else {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Yêu cầu không hợp lệ");
                request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ATTR_ERROR_MESSAGE, "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
        }
    }

    private void handleSendOTP(HttpServletRequest request, HttpServletResponse response,
                               String email, ResetPasswordService service, UserDAO userDAO)
            throws ServletException, IOException {
        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Email không tồn tại trong hệ thống");
                request.setAttribute(ATTR_EMAIL, email);
                request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
                return;
            }

            boolean otpSent = service.sendOTP(email);
            if (otpSent) {
                request.setAttribute("successMessage", "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.");
            } else {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Không thể gửi OTP. Vui lòng thử lại sau.");
            }
            request.setAttribute(ATTR_EMAIL, email);
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute(ATTR_ERROR_MESSAGE, "Lỗi khi gửi OTP. Vui lòng thử lại sau.");
            request.setAttribute(ATTR_EMAIL, email);
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response,
                                     String email, ResetPasswordService service)
            throws ServletException, IOException {
        String otp = request.getParameter("otp");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        String validationError = validateResetPasswordInput(otp, newPassword, confirmPassword);
        if (validationError != null) {
            request.setAttribute(ATTR_ERROR_MESSAGE, validationError);
            request.setAttribute(ATTR_EMAIL, email);
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
            return;
        }

        try {
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            boolean success = service.resetPasswordWithOTP(email, otp.trim(), hashedPassword);

            if (success) {
                response.sendRedirect("login.jsp");
            } else {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Mã OTP không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
                request.setAttribute(ATTR_EMAIL, email);
                request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute(ATTR_ERROR_MESSAGE, "Đã có lỗi xảy ra khi đặt lại mật khẩu. Vui lòng thử lại sau.");
            request.setAttribute(ATTR_EMAIL, email);
            request.getRequestDispatcher(FORGOT_PASSWORD_JSP).forward(request, response);
        }
    }

    private String validateResetPasswordInput(String otp, String newPassword, String confirmPassword) {
        if (otp == null || otp.trim().isEmpty()) {
            return "Vui lòng nhập mã OTP";
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return "Vui lòng nhập mật khẩu mới";
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return "Vui lòng xác nhận mật khẩu";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp";
        }
        if (newPassword.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự";
        }
        return null;
    }
}

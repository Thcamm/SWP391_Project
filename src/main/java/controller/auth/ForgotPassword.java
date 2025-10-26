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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String email = request.getParameter("email");

        // Validate email
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập email");
            request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
            return;
        }

        try {
            PasswordResetDAO resetDAO = new PasswordResetDAO();
            UserDAO userDAO = new UserDAO();
            MailService mailService = new MailService();

            ResetPasswordService service = new ResetPasswordService(resetDAO, userDAO, mailService);

            // Xử lý gửi OTP
            if ("send".equals(action)) {
                try {
                    User user = userDAO.getUserByEmail(email);
                    if (user == null) {
                        request.setAttribute("errorMessage", "Email không tồn tại trong hệ thống");
                        request.setAttribute("email", email);
                        request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                        return;
                    }

                    boolean otpSent = service.sendOTP(email);

                    if (otpSent) {
                        request.setAttribute("successMessage", "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư.");
                        request.setAttribute("email", email);
                        request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                    } else {
                        request.setAttribute("errorMessage", "Không thể gửi OTP. Vui lòng thử lại sau.");
                        request.setAttribute("email", email);
                        request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    request.setAttribute("errorMessage", "Lỗi khi gửi OTP. Vui lòng thử lại sau.");
                    request.setAttribute("email", email);
                    request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                }
                return;
            }

            // Xử lý reset password với OTP
            if ("reset".equals(action)) {
                String otp = request.getParameter("otp");
                String newPassword = request.getParameter("newPassword");
                String confirmPassword = request.getParameter("confirmPassword");

                // Validate các trường
                if (otp == null || otp.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập mã OTP");
                    request.setAttribute("email", email);
                    request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                    return;
                }

                if (newPassword == null || newPassword.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu mới");
                    request.setAttribute("email", email);
                    request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                    return;
                }

                if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng xác nhận mật khẩu");
                    request.setAttribute("email", email);
                    request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                    return;
                }

                // Kiểm tra mật khẩu khớp nhau
                if (!newPassword.equals(confirmPassword)) {
                    request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
                    request.setAttribute("email", email);
                    request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                    return;
                }

                // Validate độ dài mật khẩu
                if (newPassword.length() < 6) {
                    request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự");
                    request.setAttribute("email", email);
                    request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                    return;
                }

                try {
                    // Thực hiện reset password\
                    PasswordUtil hashUtil = new PasswordUtil();
                    newPassword = hashUtil.hashPassword(newPassword);
                    boolean success = service.resetPasswordWithOTP(email, otp.trim(), newPassword);

                    if (success) {
                        //request.getSession().setAttribute("successMessage", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
                        response.sendRedirect("login.jsp");
                    } else {
                        request.setAttribute("errorMessage", "Mã OTP không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.");
                        request.setAttribute("email", email);
                        request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    request.setAttribute("errorMessage", "Đã có lỗi xảy ra khi đặt lại mật khẩu. Vui lòng thử lại sau.");
                    request.setAttribute("email", email);
                    request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
                }
                return;
            }

            // Nếu action không hợp lệ
            request.setAttribute("errorMessage", "Yêu cầu không hợp lệ");
            request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã có lỗi xảy ra. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
        }
    }
}

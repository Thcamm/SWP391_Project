package service.user;

import dao.user.PasswordResetDAO;
import dao.user.UserDAO;
import model.user.PasswordResetToken;
import model.user.User;
import util.MailService;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;

public class ResetPasswordService {
    private PasswordResetDAO passwordResetDAO;
    private UserDAO userDAO;
    private MailService mailService;

    public ResetPasswordService(PasswordResetDAO passwordResetDAO, UserDAO userDAO, MailService mailService) {
        this.passwordResetDAO = passwordResetDAO;
        this.userDAO = userDAO;
        this.mailService = mailService;
    }

    // Tạo token ngẫu nhiên
    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Xử lý yêu cầu reset password
    public boolean requestPasswordReset(String email, String baseUrl) {
        try {
            // Tìm user theo email
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                return false;
            }

            // Xóa token cũ của user (nếu có)
            passwordResetDAO.deleteOldTokensByUserId(user.getUserId());

            // Tạo token mới
            String token = generateToken();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

            PasswordResetToken resetToken = new PasswordResetToken(user.getUserId(), token, expiryDate);

            // Lưu token vào database
            if (!passwordResetDAO.saveToken(resetToken)) {
                return false;
            }

            // Tạo link reset
            String resetLink = baseUrl + "/reset-password?token=" + token;

            // Gửi email
            String subject = "Yêu cầu đặt lại mật khẩu";
            String content = "Xin chào " + user.getUserName() + ",\n\n"
                    + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng click vào link sau để đặt lại mật khẩu:\n\n"
                    + resetLink + "\n\n"
                    + "Link này sẽ hết hạn sau 24 giờ.\n\n"
                    + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n"
                    + "Trân trọng!";

            return MailService.sendEmail(email, subject, content);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra token hợp lệ
    public boolean validateToken(String token) {
        PasswordResetToken resetToken = passwordResetDAO.findByToken(token);
        return resetToken != null && !resetToken.isExpired() && !resetToken.isUsed();
    }

    // Reset password
    public boolean resetPassword(String token, String newPassword) {
        try {
            PasswordResetToken resetToken = passwordResetDAO.findByToken(token);

            if (resetToken == null || resetToken.isExpired() || resetToken.isUsed()) {
                return false;
            }

            // Cập nhật mật khẩu mới
            boolean success = userDAO.changeUserPassword(resetToken.getUserId(), newPassword);

            if (success) {
                // Đánh dấu token đã sử dụng
                passwordResetDAO.markTokenAsUsed(token);
                return true;
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package service.user;

import dao.user.PasswordResetDAO;
import dao.user.UserDAO;
import model.user.PasswordResetToken;
import model.user.User;
import util.MailService;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ResetPasswordService {
    private PasswordResetDAO passwordResetDAO;
    private UserDAO userDAO;
    private MailService mailService;

    public ResetPasswordService(PasswordResetDAO passwordResetDAO, UserDAO userDAO, MailService mailService) {
        this.passwordResetDAO = passwordResetDAO;
        this.userDAO = userDAO;
        this.mailService = mailService;
    }

    /**
     * Tạo mã OTP ngẫu nhiên 6 chữ số
     */
    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    /**
     * Gửi OTP qua email
     * @param email Email của user
     * @return true nếu gửi thành công
     */
    public boolean sendOTP(String email) {
        try {
            // Kiểm tra email có tồn tại không
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                System.out.println("User not found with email: " + email);
                return false;
            }

            // Xóa các OTP cũ của user này
            passwordResetDAO.deleteOldTokensByUserId(user.getUserId());

            // Tạo OTP mới
            String otp = generateOTP();
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15); // Hết hạn sau 15 phút

            System.out.println("Generated OTP: " + otp + " for user: " + user.getUserName());

            // Tạo đối tượng PasswordResetToken
            PasswordResetToken resetToken = new PasswordResetToken(user.getUserId(), otp, expiryDate);

            // Lưu OTP vào database
            boolean saved = passwordResetDAO.saveToken(resetToken);
            if (!saved) {
                System.out.println("Failed to save OTP to database");
                return false;
            }

            // Chuẩn bị nội dung email
            String subject = "Mã OTP đặt lại mật khẩu - Garage System";
            String content = buildOTPEmailContent(user.getUserName(), otp);

            // Gửi email
            boolean emailSent = MailService.sendEmail(email, subject, content);

            if (emailSent) {
                System.out.println("OTP email sent successfully to: " + email);
            } else {
                System.out.println("Failed to send OTP email to: " + email);
            }

            return emailSent;

        } catch (SQLException e) {
            System.err.println("SQLException in sendOTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Exception in sendOTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xây dựng nội dung email OTP
     */
    private String buildOTPEmailContent(String userName, String otp) {
        return "Xin chào " + userName + ",\n\n"
                + "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Garage System.\n\n"
                + "Mã OTP của bạn là: " + otp + "\n\n"
                + "Mã này có hiệu lực trong 15 phút.\n\n"
                + "Vui lòng nhập mã OTP này vào trang đặt lại mật khẩu để tiếp tục.\n\n"
                + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n"
                + "Lưu ý: Không chia sẻ mã OTP này với bất kỳ ai!\n\n"
                + "Trân trọng,\n"
                + "Garage System Team";
    }

    /**
     * Xác thực OTP và đặt lại mật khẩu
     * @param email Email của user
     * @param otp Mã OTP
     * @param newPassword Mật khẩu mới
     * @return true nếu thành công
     */
    public boolean resetPasswordWithOTP(String email, String otp, String newPassword) {
        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                return false;
            }

            PasswordResetToken resetToken = passwordResetDAO.findByToken(otp);
            if (resetToken == null) {
                System.out.println("OTP not found: " + otp);
                return false;
            }

            if (resetToken.getUserId() != user.getUserId()) {
                System.out.println("OTP does not belong to this user");
                return false;
            }

            if (resetToken.isExpired()) {
                System.out.println("OTP has expired");
                return false;
            }

            if (resetToken.isUsed()) {
                System.out.println("OTP has already been used");
                return false;
            }

            // Cập nhật mật khẩu mới
            boolean passwordUpdated = userDAO.changeUserPassword(user.getUserId(), newPassword);

            if (passwordUpdated) {
                // Đánh dấu OTP đã được sử dụng
                passwordResetDAO.markTokenAsUsed(otp);
                System.out.println("Password reset successfully for user: " + user.getUserName());

                // Gửi email thông báo đổi mật khẩu thành công
                sendPasswordChangeNotification(email, user.getUserName());

                return true;
            } else {
                System.out.println("Failed to update password");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("SQLException in resetPasswordWithOTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Exception in resetPasswordWithOTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gửi email thông báo đổi mật khẩu thành công
     */
    private void sendPasswordChangeNotification(String email, String userName) {
        try {
            String subject = "Mật khẩu đã được thay đổi - Garage System";
            String content = "Xin chào " + userName + ",\n\n"
                    + "Mật khẩu của bạn đã được thay đổi thành công.\n\n"
                    + "Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.\n\n"
                    + "Trân trọng,\n"
                    + "Garage System Team";

            MailService.sendEmail(email, subject, content);
        } catch (Exception e) {
            System.err.println("Failed to send password change notification: " + e.getMessage());
        }
    }

    /**
     * Kiểm tra OTP có hợp lệ không (không đổi mật khẩu)
     */
    public boolean validateOTP(String email, String otp) {
        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                return false;
            }

            PasswordResetToken resetToken = passwordResetDAO.findByToken(otp);

            return resetToken != null
                    && resetToken.getUserId() == user.getUserId()
                    && !resetToken.isExpired()
                    && !resetToken.isUsed();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
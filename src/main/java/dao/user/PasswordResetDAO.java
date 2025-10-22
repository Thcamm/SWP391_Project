package dao.user;

import common.DbContext;
import model.user.PasswordResetToken;


import java.sql.*;
import java.time.LocalDateTime;

public class PasswordResetDAO extends DbContext {

    // Lưu OTP vào database
    public boolean saveToken(PasswordResetToken token) throws SQLException {
        String sql = "INSERT INTO password_reset_tokens (user_id, token, expiry_date, created_date, is_used) " +
                "VALUES (?, ?, ?, NOW(), FALSE)";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, token.getUserId());
            stmt.setString(2, token.getToken());
            stmt.setTimestamp(3, Timestamp.valueOf(token.getExpiryDate()));

            return stmt.executeUpdate() > 0;
        }
    }

    // Tìm OTP theo token (mã OTP)
    public PasswordResetToken findByToken(String token) {
        String sql = "SELECT * FROM password_reset_tokens WHERE token = ? ORDER BY created_date DESC LIMIT 1";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PasswordResetToken resetToken = new PasswordResetToken();
                resetToken.setId(rs.getInt("id"));
                resetToken.setUserId(rs.getInt("user_id"));
                resetToken.setToken(rs.getString("token"));
                resetToken.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
                resetToken.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());
                resetToken.setUsed(rs.getBoolean("is_used"));
                return resetToken;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Đánh dấu OTP đã được sử dụng
    public boolean markTokenAsUsed(String token) throws SQLException {
        String sql = "UPDATE password_reset_tokens SET is_used = TRUE WHERE token = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            return stmt.executeUpdate() > 0;
        }
    }

    // Xóa các OTP cũ của user
    public boolean deleteOldTokensByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE user_id = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        }
    }

    // Xóa các OTP đã hết hạn (chạy định kỳ để dọn dẹp)
    public void deleteExpiredTokens() {
        String sql = "DELETE FROM password_reset_tokens WHERE expiry_date < NOW()";

        try (Connection conn = DbContext.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
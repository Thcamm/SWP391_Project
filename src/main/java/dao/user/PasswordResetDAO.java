package dao.user;

import java.sql.*;
import java.time.LocalDateTime;
import model.user.PasswordResetToken;
import common.DbContext;
public class PasswordResetDAO extends DbContext{

    public boolean saveToken(PasswordResetToken token) {
        String sql = "INSERT INTO password_reset_tokens (user_id, token, expiry_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DbContext.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, token.getUserId());
            stmt.setString(2, token.getToken());
            stmt.setTimestamp(3, Timestamp.valueOf(token.getExpiryDate()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PasswordResetToken findByToken(String token) {
        String sql = "SELECT * FROM password_reset_tokens WHERE token = ? AND is_used = FALSE";
        try (PreparedStatement stmt = DbContext.getConnection().prepareStatement(sql)) {
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

    public boolean markTokenAsUsed(String token) {
        String sql = "UPDATE password_reset_tokens SET is_used = TRUE WHERE token = ?";
        try (PreparedStatement stmt = DbContext.getConnection().prepareStatement(sql)) {
            stmt.setString(1, token);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOldTokensByUserId(int userId) {
        String sql = "DELETE FROM password_reset_tokens WHERE user_id = ?";
        try (PreparedStatement stmt = DbContext.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

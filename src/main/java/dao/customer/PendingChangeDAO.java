package dao.customer;

import common.DbContext;
import model.customer.PendingChange;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PendingChangeDAO extends DbContext {

    private ObjectMapper objectMapper = new ObjectMapper(); // JSON xử lý FieldsChanged

    // Lấy PendingChange theo token
    public PendingChange getByToken(String token) throws SQLException {
        String sql = "SELECT * FROM PendingChange WHERE Token = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PendingChange pending = new PendingChange();
                    pending.setChangeId(rs.getInt("changeID"));
                    pending.setCustomerId(rs.getInt("CustomerID"));
                    String fieldsJson = rs.getString("FieldsChanged");
                    Map<String, String> fields = objectMapper.readValue(fieldsJson, HashMap.class);
                    pending.setFieldsChanged(fields);
                    pending.setStatus(rs.getString("Status"));
                    Timestamp expiryTs = rs.getTimestamp("TokenExpiry");
                    if (expiryTs != null) pending.setTokenExpiry(expiryTs.toLocalDateTime());
                    pending.setToken(rs.getString("Token"));
                    return pending;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Tạo PendingChange mới
    public void createPendingChange(int customerId, PendingChange pending) throws SQLException {
        String sql = "INSERT INTO PendingChange (CustomerID, FieldsChanged, Token, TokenExpiry, Status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String fieldsJson = objectMapper.writeValueAsString(pending.getFieldsChanged());
            ps.setInt(1, customerId);
            ps.setString(2, fieldsJson);
            ps.setString(3, pending.getToken());
            LocalDateTime expiry = LocalDateTime.now().plusHours(24);
            ps.setTimestamp(4, Timestamp.valueOf(expiry));
            ps.setString(5, "PENDING");
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) pending.setChangeId(rs.getInt(1)); // lưu changeId
            }
            pending.setTokenExpiry(expiry);
            pending.setStatus("PENDING");
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    // Cập nhật trạng thái CONFIRMED / REJECTED
    public void updateStatus(int changeId, String status) throws SQLException {
        String sql = "UPDATE PendingChange SET Status = ? WHERE ChangeID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, changeId);
            ps.executeUpdate();
        }
    }



}

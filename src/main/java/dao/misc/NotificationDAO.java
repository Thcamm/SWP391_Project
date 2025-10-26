package dao.misc;

import common.DbContext;
import model.misc.Notification; // Model mình vừa tạo

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class NotificationDAO extends DbContext {

    public boolean createNotification(Notification notification) throws SQLException {
        // Dựa trên cấu trúc bảng của ông bạn
        String sql = "INSERT INTO notification (UserID, Title, Body, EntityType, EntityID, AppointmentID) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getTitle());
            ps.setString(3, notification.getBody());
            ps.setString(4, notification.getEntityType());
            ps.setInt(5, notification.getEntityId());

            // Xử lý AppointmentID vì nó có thể là NULL
            if (notification.getAppointmentId() != null) {
                ps.setInt(6, notification.getAppointmentId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            return ps.executeUpdate() > 0;
        }
    }

    public List<Notification> getNotificationsByUserId(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE UserID = ? ORDER BY CreatedAt DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification notif = new Notification();
                    notif.setNotificationId(rs.getInt("NotificationID"));
                    notif.setUserId(rs.getInt("UserID"));
                    notif.setTitle(rs.getString("Title"));
                    notif.setBody(rs.getString("Body"));
                    notif.setRead(rs.getBoolean("IsRead"));
                    notif.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                    notif.setEntityType(rs.getString("EntityType"));
                    notif.setEntityId(rs.getInt("EntityID"));
                    notif.setAppointmentId(rs.getObject("AppointmentID", Integer.class));

                    notifications.add(notif);
                }
            }
        }
        return notifications;
    }
}
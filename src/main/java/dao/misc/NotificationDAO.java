//package dao.misc;
//
//import common.DbContext;
//import model.misc.Notification;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//
//public class NotificationDAO extends DbContext {
//
//    public boolean createNotification(Notification notification) throws SQLException {
//        String sql = "INSERT INTO Notification (UserID, Title, Body, EntityType, EntityID) VALUES (?, ?, ?, ?, ?)";
//
//        try (Connection conn = getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, notification.getUserId());
//            ps.setString(2, notification.getTitle());
//            ps.setString(3, notification.getBody());
//            ps.setString(4, notification.getEntityType());
//            ps.setInt(5, notification.getEntityId());
//
//            return ps.executeUpdate() > 0;
//        }
//    }
//}
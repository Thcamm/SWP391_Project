package dao;

import model.User;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class UserDao {
    private DataSource dataSource;

    public UserDao() {
//        try {
//            Context initContext = new InitialContext();
//            Context envContext = (Context) initContext.lookup("java:comp/env");
//            // JNDI name must match the Resource name in context.xml
//            dataSource = (DataSource) envContext.lookup("jdbc/GarageDB");
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Critical error: database connection setup failed
//        }

        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            // Lỗi xảy ra nếu tên này sai hoặc context.xml không được triển khai
            dataSource = (DataSource) envContext.lookup("jdbc/GarageDB");

            // THÊM: Kiểm tra kết nối thành công hay không
            if (dataSource == null) {
                System.err.println("CRITICAL ERROR: JNDI DataSource 'jdbc/GarageDB' is NULL.");
            } else {
                System.out.println("JNDI DataSource 'jdbc/GarageDB' loaded SUCCESSFULLY.");
            }

        } catch (Exception e) {
            // Đặt Breakpoint ở đây và kiểm tra 'e'
            e.printStackTrace();
            System.err.println("CRITICAL ERROR: Failed to initialize JNDI DataSource in UserDao.");
        }
    }

    /**
     * Finds a User by their unique UserID.
     * @param userId The ID of the user.
     * @return An Optional containing the User object or empty if not found.
     * @throws SQLException If a database access error occurs.
     */
    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT UserID, RoleID, FullName, UserName, Email, PhoneNumber, ActiveStatus, CreatedAt, UpdatedAt " +
                "FROM User WHERE UserID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("UserID"),
                            rs.getInt("RoleID"),
                            rs.getString("FullName"),
                            rs.getString("UserName"),
                            rs.getString("Email"),
                            rs.getString("PhoneNumber"),
                            rs.getBoolean("ActiveStatus"),
                            rs.getTimestamp("CreatedAt"),
                            rs.getTimestamp("UpdatedAt")
                    );
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Updates the user's modifiable profile information (FullName, Email, PhoneNumber).
     * @param user The User object containing the new data and ID.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs (e.g., duplicate email constraint violation).
     */
    public boolean updateProfile(User user) throws SQLException {
        String sql = "UPDATE User SET FullName = ?, Email = ?, PhoneNumber = ?, UpdatedAt = NOW() WHERE UserID = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setInt(4, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
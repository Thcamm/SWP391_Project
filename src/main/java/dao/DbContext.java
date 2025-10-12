package dao;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbContext {
    public static void main(String[] args) {
        try {
            System.out.println(getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/garage_mgmt?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Bangkok&useUnicode=true&characterEncoding=UTF-8";
        String user = "root";
        String pass = "admin";
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException ignored) {}
        return DriverManager.getConnection(url, user, pass);
    }
}

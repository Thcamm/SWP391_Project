package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbContext {
    public static void main(String[] args) {

        String url = args.length > 0 ? args[0]
                : "jdbc:mysql://localhost:3306/garage_mgmt?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Bangkok&useUnicode=true&characterEncoding=UTF-8";
        String user = args.length > 1 ? args[1] : "root";
        String pass = args.length > 2 ? args[2] : "admin";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }

        long t0 = System.currentTimeMillis();
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            long dt = System.currentTimeMillis() - t0;
            System.out.println("Connected to MySQL successfully in " + dt + " ms");
            System.out.println("   AutoCommit=" + conn.getAutoCommit() + ", Catalog=" + conn.getCatalog());

        } catch (SQLException e) {
            System.err.println("DB connection FAILED");
            System.err.println("SQLState=" + e.getSQLState() + ", ErrorCode=" + e.getErrorCode());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/garage_mgmt";
        String user = "root";
        String pass = "admin";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        return DriverManager.getConnection(url, user, pass);
    }

}
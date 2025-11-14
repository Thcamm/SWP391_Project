package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DbContext: hỗ trợ cả static-utility và subclassing.
 * - Có thể gọi trực tiếp DbContext.getConnection()
 * - Hoặc extends DbContext rồi dùng this.open() / this.open(false) trong DAO
 */
public class DbContext {

    // Ưu tiên ENV, thiếu thì dùng mặc định
    private static final String ENV_URL = getEnvOrDefault(
            "DB_URL",
            "jdbc:mysql://localhost:3306/garage_mgmt");

    private static final String JDBC_PARAMS = (ENV_URL.contains("?") ? "&" : "?") +
            "useSSL=false" +
            "&allowPublicKeyRetrieval=true" +
            "&serverTimezone=Asia/Bangkok" +
            "&useUnicode=true" +
            "&characterEncoding=UTF-8";

    private static final String JDBC_URL = ENV_URL + JDBC_PARAMS;
    private static final String JDBC_USER = getEnvOrDefault("DB_USER", "root");
    private static final String JDBC_PASS = getEnvOrDefault("DB_PASS", "admin");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }
    }

    protected DbContext() {
    }

    private static String getEnvOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isEmpty()) ? def : v;
    }

    /* ========= STATIC UTILITY ========= */

    /** Lấy connection với autoCommit = true. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
    }

    /** Lấy connection và set autoCommit theo ý. */
    public static Connection getConnection(boolean autoCommit) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            close(conn);
            throw e;
        }
        return conn;
    }

    public static void commit(Connection conn) {
        if (conn != null)
            try {
                conn.commit();
            } catch (SQLException ignored) {
            }
    }

    public static void rollback(Connection conn) {
        if (conn != null)
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
    }

    public static void resetAutoCommit(Connection conn) {
        if (conn != null)
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
    }

    public static void close(Connection conn) {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
    }

    public static void commitAndClose(Connection conn) {
        try {
            commit(conn);
        } finally {
            try {
                resetAutoCommit(conn);
            } finally {
                close(conn);
            }
        }
    }

    public static void rollbackAndClose(Connection conn) {
        try {
            rollback(conn);
        } finally {
            try {
                resetAutoCommit(conn);
            } finally {
                close(conn);
            }
        }
    }

    /* ========= INSTANCE ALIASES (dùng khi extends) ========= */

    /** Alias instance: mở connection (autoCommit=true). */
    protected Connection open() throws SQLException {
        return getConnection();
    }

    /** Alias instance: mở connection và set autoCommit theo ý. */
    protected Connection open(boolean autoCommit) throws SQLException {
        return getConnection(autoCommit);
    }
}
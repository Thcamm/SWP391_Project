package common;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Db {
    private static final String URL = "jdbc:mysql://localhost:3306/garage_mgmt?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Bangkok&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASS = "thuy2005";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException ignored) {}
    }

    public static Connection get() throws SQLException{
        return DriverManager.getConnection(URL,USER,PASS);
    }

    private Db(){}

}

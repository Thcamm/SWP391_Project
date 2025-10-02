package common;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbPing {
    public static void main(String[] args) {
        String expectedDb = "garage_mgmt";
        try (Connection c = DbContext.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT DATABASE() AS db")) {

            String actualDb = rs.next() ? rs.getString("db") : "(unknown)";
            System.out.println("Connected. DATABASE() = " + actualDb);

            if (expectedDb.equalsIgnoreCase(actualDb)) {
                System.out.println("PASS: Đang kết nối đúng database: " + expectedDb);
                System.exit(0);
            } else {
                System.err.println(" FAIL: Đang kết nối NHẦM database! (expected "
                        + expectedDb + ", actual " + actualDb + ")");
                System.exit(2);
            }
        } catch (Exception e) {
            System.err.println(" FAIL: Không kết nối được DB.");
            e.printStackTrace();
            System.exit(1);
        }
    }
}

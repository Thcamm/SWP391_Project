package common.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class để hash và verify password sử dụng BCrypt
 */
public class PasswordHashUtil {

    // Số vòng lặp cho BCrypt (10-12 là phổ biến)
    private static final int BCRYPT_ROUNDS = 10;

    /**
     * Hash password using BCrypt
     * @param plainPassword Password gốc
     * @return BCrypt hashed password
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify password với BCrypt hash
     * @param plainPassword Password người dùng nhập
     * @param hashedPassword Hash lưu trong database
     * @return true nếu khớp, false nếu không khớp
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            System.err.println("❌ Error verifying password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Main method để generate BCrypt hash cho testing
     */
    public static void main(String[] args) {
        // Password để test
        String password = "666666";

        // Generate 3 hash khác nhau (BCrypt mỗi lần hash sẽ khác vì có salt)
        System.out.println("========================================");
        System.out.println("BCrypt Hash Generator");
        System.out.println("========================================");
        System.out.println("Plain Password: " + password);
        System.out.println();

        for (int i = 1; i <= 3; i++) {
            String hash = hashPassword(password);
            System.out.println("Hash " + i + ": " + hash);
            System.out.println("Length: " + hash.length() + " characters");

            // Verify
            boolean verified = verifyPassword(password, hash);
            System.out.println("Verification: " + (verified ? "✅ PASS" : "❌ FAIL"));
            System.out.println();
        }

        System.out.println("========================================");
        System.out.println("SQL INSERT Examples:");
        System.out.println("========================================");
        System.out.println("PasswordHash = '" + hashPassword(password) + "'");
    }
}

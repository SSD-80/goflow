package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private PasswordUtil() {
        // prevent instantiation
    }

    // Generate bcrypt hash
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)); // cost factor 12
    }

    // Verify bcrypt password
    public static boolean checkPassword(String plainPassword, String storedHash) {
        if (storedHash == null) return false;
        return BCrypt.checkpw(plainPassword, storedHash);
    }
}

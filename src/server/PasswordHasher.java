package server;

import java.nio.charset.StandardCharsets;
import java.security.*;

public class PasswordHasher {
    private static final String PEPPER = "goida";
    private static final int SALT_LENGTH = 16;

    public static class HashedPassword {
        private final String hash;
        private final String salt;

        public HashedPassword(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }

        public String getHash() {
            return hash;
        }

        public String getSalt() {
            return salt;
        }
    }

    public static HashedPassword hashPassword(String password) {
        byte[] salt = generateSalt();
        String saltStr = bytesToHex(salt);

        String input = password + saltStr + PEPPER;

        String hash = hashWithSha224(input);

        return new HashedPassword(hash, saltStr);
    }

    public static boolean verifyPassword(String password, String storedHash, String storedSalt) {
        String input = password + storedSalt + PEPPER;
        String computedHash = hashWithSha224(input);
        return computedHash.equals(storedHash);
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static String hashWithSha224(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-224");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Алгоритм SHA-224 не поддерживается", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

package com.novastudent.security;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Secure password hashing utility using BCrypt.
 * BCrypt automatically handles salting and is resistant to rainbow table attacks.
 *
 * BCrypt stores the salt within the hash itself, so no separate salt storage is needed.
 * The default work factor (log rounds) is 10, providing ~100ms hash time.
 */
public class PasswordHasher {

    private static final int BCRYPT_ROUNDS = 10;

    /**
     * Hashes a plain-text password using BCrypt.
     *
     * @param plainPassword The plain-text password to hash
     * @return BCrypt hash string (includes salt)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     *
     * @param plainPassword The plain-text password to verify
     * @param hashedPassword The stored BCrypt hash
     * @return true if the password matches
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}

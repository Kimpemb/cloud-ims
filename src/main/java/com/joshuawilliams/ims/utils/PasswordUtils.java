package com.joshuawilliams.ims.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class PasswordUtils {

    // Hash the password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64); // SHA-256 produces 64 hex characters
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // Verify if the input password matches the stored hashed password
    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

    // Check if a password is already hashed (SHA-256 hash is 64 hexadecimal characters)
    public static boolean isHashed(String password) {
        return password != null && password.matches("^[a-fA-F0-9]{64}$");
    }
}

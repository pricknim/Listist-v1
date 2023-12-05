package edu.codespring.listist.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncrypter {
    private static final Logger LOG = LoggerFactory.getLogger(PasswordEncrypter.class);

    private PasswordEncrypter() {}

    public static String generateHashedPassword(String plainPassword, String plainSalt) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            MessageDigest algorithm = MessageDigest.getInstance(PropertyProvider.getProperty("password_algorithm"));
            byte[] bytes = (plainPassword + plainSalt).getBytes(PropertyProvider.getProperty("password_encoding"));

            algorithm.reset();
            algorithm.update(bytes);

            return toHexString(algorithm.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOG.error("Failed to generate hashed password; " + e.getMessage());
            throw e;
        }
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder buffer = new StringBuilder();

        for (byte i : bytes) {
            String hex = Integer.toHexString(0xFF & i);

            if (hex.length() == 1) {
                buffer.append('0');
            }

            buffer.append(hex);
        }

        return buffer.toString();
    }
}

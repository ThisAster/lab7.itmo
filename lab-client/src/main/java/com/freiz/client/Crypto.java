package com.freiz.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public final class Crypto {
    private Crypto() {
    }
    public static String encryptPassword(String password) {
        String sha = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-384");
            messageDigest.reset();
            messageDigest.update(password.getBytes(StandardCharsets.UTF_8));
            sha = format(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sha;
    }

    private static String format(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte bytee : bytes) {
            formatter.format("%02x", bytee);
        }
        String res = formatter.toString();
        formatter.close();
        return res;
    }
}

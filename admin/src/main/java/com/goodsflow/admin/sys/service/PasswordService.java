package com.goodsflow.admin.sys.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class PasswordService {
    private static final String PREFIX = "sha256$";

    public String encode(String rawPassword) {
        return PREFIX + hex(sha256(rawPassword));
    }

    public boolean matches(String rawPassword, String storedPassword) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(storedPassword)) {
            return false;
        }
        String encoded = storedPassword.startsWith(PREFIX) ? encode(rawPassword) : rawPassword;
        return MessageDigest.isEqual(encoded.getBytes(StandardCharsets.UTF_8), storedPassword.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] sha256(String text) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    private String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            result.append(String.format("%02x", value));
        }
        return result.toString();
    }
}

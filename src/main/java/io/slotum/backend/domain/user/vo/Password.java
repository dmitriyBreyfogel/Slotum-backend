package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Pattern;

public final class Password {
    private static final Pattern RAW_PASSWORD_PATTERN = Pattern.compile("^[\\x21-\\x7E]+$");
    private static final String ALGORITHM = "SHA-256";

    private final String passwordHash;

    public Password(String rawPassword) {
        validateRaw(rawPassword);
        this.passwordHash = hash(rawPassword);
    }

    public static Password fromHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_PASSWORD,
                    "Password hash is blank"
            );
        }
        return new Password(passwordHash.trim(), true);
    }

    public boolean matches(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                hash(rawPassword).getBytes(StandardCharsets.UTF_8),
                passwordHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static void validateRaw(String rawPassword) {
        if (rawPassword == null || !RAW_PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_PASSWORD,
                    "Invalid raw password format"
            );
        }
    }

    public String value() {
        return passwordHash;
    }

    private Password(String passwordHash, boolean fromHash) {
        if (!fromHash) {
            throw new IllegalStateException("Use public constructors/factories");
        }
        this.passwordHash = passwordHash;
    }

    private static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (GeneralSecurityException ex) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_PASSWORD,
                    "Password hashing error"
            );
        }
    }
}

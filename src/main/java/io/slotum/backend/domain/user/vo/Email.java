package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;
import java.util.regex.Pattern;

public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9_]+@[A-Za-z]+\\.[A-Za-z]{2,63}$");
    private final String email;

    public Email(String email) {
        String normalized = normalize(email);
        if (!validate(normalized)) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_EMAIL,
                    "Invalid email format",
                    Map.of("email", email)
            );
        }
        this.email = normalized;
    }

    public static boolean validate(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public String value() {
        return email;
    }

    private static String normalize(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}

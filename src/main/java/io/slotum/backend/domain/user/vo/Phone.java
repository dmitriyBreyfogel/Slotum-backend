package io.slotum.backend.domain.user.vo;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;
import java.util.regex.Pattern;

public final class Phone {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9][0-9]{9,14}$");
    private final String phone;

    public Phone(String phone) {
        String normalized = normalize(phone);
        if (!validate(normalized)) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_PHONE,
                    "Invalid phone format",
                    Map.of("phone", phone)
            );
        }
        this.phone = normalized;
    }

    public static boolean validate(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public String value() {
        return phone;
    }

    private static String normalize(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.trim().replace(" ", "").replace("-", "");
    }
}

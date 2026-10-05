package io.slotum.backend.domain.user.vo;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;
import java.util.regex.Pattern;

public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private final String email;

    private Email(String email) {
        this.email = email;
    }

    /**
     * Создание объекта email почты
     * @param email строка названия почты
     * @return созданный объект email почты
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_EMAIL} - если строка названия почты невалидна или равна {@code null}</li>
     *      </ul>
     */
    public static Email of(String email) {
        String normalizedEmail = StringUtils.normalize(email);

        if (normalizedEmail == null) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_EMAIL,
                    "Empty user email"
            );
        }

        normalizedEmail = normalizedEmail.toLowerCase();

        if (!isValid(normalizedEmail)) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_EMAIL,
                    "Invalid email format",
                    Map.of("email", email)
            );
        }

        return new Email(normalizedEmail);
    }

    public static boolean isValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public String value() {
        return email;
    }
}

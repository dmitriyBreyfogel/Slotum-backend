package io.slotum.backend.domain.user.vo;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;
import java.util.regex.Pattern;

public final class Phone {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9][0-9]{9,14}$");
    private final String phone;

    private Phone(String phone) {
        this.phone = phone;
    }

    /**
     * Создание телефонного номера из входящего номера.
     * Номер нормализуется до номера без пробелов и дефисов
     * @param phone строковый номер телефона
     * @return созданный телефонный номер
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_PHONE} - если номер не подходит под паттерн или равен {@code null}</li>
     *      </ul>
     */
    public static Phone of(String phone) {
        String normalized = normalize(phone);

        if (!isValid(normalized)) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_PHONE,
                    "Invalid phone format",
                    phone == null
                            ? Map.of()
                            : Map.of("phone", phone)
            );
        }

        return new Phone(normalized);
    }

    /**
     * Проверяет валидность номера
     * @param phone строковый номер
     * @return {@code true} если номер подходит под паттерн и не равен {@code null}, {@code false} иначе
     */
    public static boolean isValid(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public String value() {
        return phone;
    }

    private static String normalize(String phone) {
        String normalizedPhone = StringUtils.normalize(phone);
        return normalizedPhone == null
                ? null
                : normalizedPhone.replace(" ", "").replace("-", "");
    }
}

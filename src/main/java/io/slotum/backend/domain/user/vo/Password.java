package io.slotum.backend.domain.user.vo;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Pattern;

public final class Password {
    private static final Pattern RAW_PASSWORD_PATTERN = Pattern.compile("^[\\x21-\\x7E]+$");
    private static final String ALGORITHM = "SHA-256";

    private final String passwordHash;

    private Password(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Создание пароля из хэша
     * @param passwordHash хэш пароля
     * @return созданный пароль
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_PASSWORD} - если хэш пустой или {@code null}</li>
     *      </ul>
     */
    public static Password fromHash(String passwordHash) {
        String normalizedPasswordHash = StringUtils.normalize(passwordHash);

        if (normalizedPasswordHash == null) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_PASSWORD,
                    "Password hash is empty"
            );
        }

        return new Password(normalizedPasswordHash);
    }

    /**
     * Создание пароля из его начального строкового состояния.
     * При инициализации он захэшируется и сохранится только хэш
     * @param rawPassword строковой пароль исходного состояния
     * @return созданный пароль, хранящий хэш входного пароля
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_PASSWORD} - если строковый исходный пароль пуст, равен {@code null},
     *          не соответствует паттерну паролей или произошёл сбой при хэшировании</li>
     *      </ul>
     */
    public static Password fromRaw(String rawPassword) {
        String normalizedRawPassword = StringUtils.normalize(rawPassword);

        if (normalizedRawPassword == null) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_PASSWORD,
                    "Raw password is empty"
            );
        }

        validateRaw(normalizedRawPassword);
        String hash = hash(normalizedRawPassword);
        return new Password(hash);
    }

    /**
     * Проверяет равенство текущего пароля с другим строковым входящим.
     * Входящий пароль хэшируется текущим алгоритмом и сравнение происходит по хэшам
     * @param rawPassword строковый пароль для сравнения
     * @return {@code true} если равны, {@code false} если не равны
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_PASSWORD} - если произойдёт сбой при хэшировании</li>
     *      </ul>
     */
    public boolean matches(String rawPassword) {
        String normalizedRawPassword = StringUtils.normalize(rawPassword);

        if (normalizedRawPassword == null) {
            return false;
        }

        return MessageDigest.isEqual(
                hash(normalizedRawPassword).getBytes(StandardCharsets.UTF_8),
                passwordHash.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Хэширование пароля по алгоритму {@code SHA-256}.
     * @param rawPassword пароль в исходном строковом формате
     * @return строка получившегося хэша
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_PASSWORD} - произошёл сбой при хэшировании</li>
     *      </ul>
     */
    public static String hash(String rawPassword) {
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

    public String value() {
        return passwordHash;
    }

    public static void validateRaw(String rawPassword) {
        if (rawPassword == null || !RAW_PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_PASSWORD,
                    "Invalid raw password format"
            );
        }
    }
}

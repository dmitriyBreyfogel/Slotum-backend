package io.slotum.backend.domain.user;

import io.slotum.backend.domain.user.vo.Email;
import io.slotum.backend.domain.user.vo.Password;
import io.slotum.backend.domain.user.vo.Phone;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public final class User {
    private final Long id;
    private final String surname;
    private final String firstName;
    private final String secondName;
    private final Email email;
    private final Password password;
    private final Phone phone;

    private User(Long id, String surname, String firstName, String secondName, Email email, Password password, Phone phone) {
        this.id = id;
        this.surname = surname;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public static User create(Long id, String surname, String firstName, String secondName, String email, String password, String phone) {
        validateId(id);
        return new User(
                id,
                validateRequiredName(surname, ErrorCode.INVALID_USER_SURNAME, "surname"),
                validateRequiredName(firstName, ErrorCode.INVALID_USER_FIRSTNAME, "firstName"),
                normalizeOptional(secondName),
                Email.of(Of(email),
                new Password(password),
                new Phone(phone)
        );
    }

    public static User restore(Long id, String surname, String firstName, String secondName, String email, String passwordHash, String phone) {
        validateId(id);
        return new User(
                id,
                validateRequiredName(surname, ErrorCode.INVALID_USER_SURNAME, "surname"),
                validateRequiredName(firstName, ErrorCode.INVALID_USER_FIRSTNAME, "firstName"),
                normalizeOptional(secondName),
                Email.of(Of(email),
                Password.fromHash(passwordHash),
                new Phone(phone)
        );
    }

    public Long getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public Email getEmail() {
        return email;
    }

    public Password getPassword() {
        return password;
    }

    public Phone getPhone() {
        return phone;
    }

    public boolean matchesPassword(String rawPassword) {
        return password.matches(rawPassword);
    }

    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_ID,
                    "Invalid user id",
                    Map.of("id", id)
            );
        }
    }

    private static String validateRequiredName(String value, ErrorCode code, String field) {
        if (value == null || value.isBlank()) {
            Map<String, Object> details = (value == null)
                    ? Map.of("field", field)
                    : Map.of("field", field, "value", value);
            throw AppException.build(
                    code,
                    "Invalid " + field,
                    details
            );
        }
        return value.trim();
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

package io.slotum.backend.domain.user;

import io.slotum.backend.domain.user.vo.Email;
import io.slotum.backend.domain.user.vo.Password;
import io.slotum.backend.domain.user.vo.Phone;
import io.slotum.backend.domain.utils.StringUtils;
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

    /**
     * Создание пользователя без отчества
     * @param surname фамилия пользователя
     * @param firstName имя пользователя
     * @param email почта пользователя
     * @param password пароль пользователя
     * @param phone телефон пользователя
     * @return созданный пользователь
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_SURNAME} - если фамилия более 255 символов или равна {@code null}</li>
     *          <li>{@code INVALID_USER_FIRSTNAME} - если имя более 255 символов или равно {@code null}</li>
     *          <li>{@code INVALID_USER_EMAIL} - если строка названия почты невалидна или равна {@code null}</li>
     *          <li>{@code INVALID_USER_PASSWORD} - если хэш пустой или {@code null}</li>
     *          <li>{@code INVALID_USER_PHONE} - если номер не подходит под паттерн или равен {@code null}</li>
     *      </ul>
     */
    public static User create(String surname, String firstName, String email, String password, String phone) {
        return restore(
                null,
                surname,
                firstName,
                null,
                email,
                Password.hash(password),
                phone
        );
    }

    /**
     * Создание пользователя с отчеством
     * @param surname фамилия пользователя
     * @param firstName имя пользователя
     * @param secondName отчество пользователя
     * @param email почта пользователя
     * @param password пароль пользователя
     * @param phone телефон пользователя
     * @return созданный пользователь
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_SURNAME} - если фамилия более 255 символов или равна {@code null}</li>
     *          <li>{@code INVALID_USER_FIRSTNAME} - если имя более 255 символов или равно {@code null}</li>
     *          <li>{@code INVALID_USER_SECONDNAME} - если отчество более 255 символов</li>
     *          <li>{@code INVALID_USER_EMAIL} - если строка названия почты невалидна или равна {@code null}</li>
     *          <li>{@code INVALID_USER_PASSWORD} - если хэш пустой или {@code null}</li>
     *          <li>{@code INVALID_USER_PHONE} - если номер не подходит под паттерн или равен {@code null}</li>
     *      </ul>
     */
    public static User create(String surname, String firstName, String secondName, String email, String password, String phone) {
        return restore(
                null,
                surname,
                firstName,
                secondName,
                email,
                Password.hash(password),
                phone
        );
    }

    /**
     * Создаёт объект пользователя с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что пользователь новый.
     * @param id идентификатор пользователя
     * @param surname фамилия пользователя
     * @param firstName имя пользователя
     * @param secondName отчество пользователя (может быть {@code null})
     * @param email почта пользователя
     * @param passwordHash хэш пароля пользователя
     * @param phone телефон пользователя
     * @return созданный объект пользователя по заданным параметрам
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_ID} - идентификатора пользователя не положительный или {@code null}</li>
     *          <li>{@code INVALID_USER_SURNAME} - если фамилия более 255 символов или равна {@code null}</li>
     *          <li>{@code INVALID_USER_FIRSTNAME} - если имя более 255 символов или равно {@code null}</li>
     *          <li>{@code INVALID_USER_SECONDNAME} - если отчество более 255 символов</li>
     *          <li>{@code INVALID_USER_EMAIL} - если строка названия почты невалидна или равна {@code null}</li>
     *          <li>{@code INVALID_USER_PASSWORD} - если хэш пустой или {@code null}</li>
     *          <li>{@code INVALID_USER_PHONE} - если номер не подходит под паттерн или равен {@code null}</li>
     *      </ul>
     */
    public static User restore(Long id, String surname, String firstName, String secondName, String email, String passwordHash, String phone) {
        validateId(id);

        String normalizedSurname = StringUtils.normalize(surname);
        String normalizedFirstName = StringUtils.normalize(firstName);
        String normalizedSecondName = StringUtils.normalize(secondName);

        validateSurname(normalizedSurname);
        validateFirstName(normalizedFirstName);
        validateSecondName(normalizedSecondName);

        return new User(
                id,
                normalizedSurname,
                normalizedFirstName,
                normalizedSecondName,
                Email.of(email),
                Password.fromHash(passwordHash),
                Phone.of(phone)
        );
    }

    /**
     * Проверяет равенство текущего пароля с входящим строковым.
     * Сравнение происходит по хэшам.
     * @param rawPassword строковый пароль для сравнения
     * @return {@code true} если равны, {@code false} если не равны
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_USER_PASSWORD} - если произойдёт сбой при хэшировании</li>
     *      </ul>
     */
    public boolean matchesPassword(String rawPassword) {
        return password.matches(rawPassword);
    }

    /* Getters */
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

    /* Validation */
    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_ID,
                    "Invalid user id",
                    Map.of("id", id)
            );
        }
    }

    private static void validateSurname(String surname) {
        if (surname == null) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_SURNAME,
                    "Empty user surname"
            );
        }

        if (surname.length() > 255) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_SURNAME,
                    "Too long user surname",
                    Map.of(
                            "Expected max length", 255,
                            "Actual length", surname.length()
                    )
            );
        }
    }

    private static void validateFirstName(String firstName) {
        if (firstName == null) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_FIRSTNAME,
                    "Empty user first name"
            );
        }

        if (firstName.length() > 255) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_FIRSTNAME,
                    "Too long user first name",
                    Map.of(
                            "Expected max length", 255,
                            "Actual length", firstName.length()
                    )
            );
        }
    }

    private static void validateSecondName(String secondName) {
        if (secondName != null && secondName.length() > 255) {
            throw AppException.build(
                    ErrorCode.INVALID_USER_SECONDNAME,
                    "Too long user second name",
                    Map.of(
                            "Expected max length", 255,
                            "Actual length", secondName.length()
                    )
            );
        }
    }
}

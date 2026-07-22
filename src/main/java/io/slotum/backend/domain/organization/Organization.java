package io.slotum.backend.domain.organization;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public final class Organization {
    private final Long id;
    private final String name;
    private final String description;
    private final Double grade;

    private Organization(Long id, String name, String description, Double grade) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.grade = grade;
    }

    /**
     * Создание организации
     * @param name имя организации
     * @param description описание к организации
     * @param grade оценка организации
     * @return созданная организация
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_ORGANIZATION_NAME} — имя не указано</li>
     *          <li>{@code TOO_LONG_ORGANIZATION_NAME} — имя длиннее 255 символов</li>
     *          <li>{@code TOO_LONG_ORGANIZATION_DESCRIPTION} — описание длиннее 1024 символов</li>
     *          <li>{@code INVALID_ORGANIZATION_GRADE} — оценка вне диапазона [0, 5]</li>
     *      </ul>
     */
    public static Organization create(String name, String description, Double grade) {
        return restore(null, name, description, grade);
    }

    /**
     * Создание организации без оценки
     * @param name имя организации
     * @param description описание к организации
     * @return созданная организация c {@code null} оценкой
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_ORGANIZATION_NAME} — имя не указано</li>
     *          <li>{@code TOO_LONG_ORGANIZATION_NAME} — имя длиннее 255 символов</li>
     *          <li>{@code TOO_LONG_ORGANIZATION_DESCRIPTION} — описание длиннее 1024 символов</li>
     *      </ul>
     */
    public static Organization create(String name, String description) {
        return restore(null, name, description, null);
    }

    /**
     * Создание организации без её описания
     * @param name имя организации
     * @param grade оценка организации
     * @return созданная организация с {@code null} описанием
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_ORGANIZATION_NAME} — имя не указано</li>
     *          <li>{@code TOO_LONG_ORGANIZATION_NAME} — имя длиннее 255 символов</li>
     *          <li>{@code INVALID_ORGANIZATION_GRADE} — оценка вне диапазона [0, 5]</li>
     *      </ul>
     */
    public static Organization create(String name, Double grade) {
        return restore(null, name, null, grade);
    }

    /**
     * Создание организации без оценки и её описания
     * @param name имя организации
     * @return созданная организация с {@code null} оценкой и {@code null} описанием
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_ORGANIZATION_NAME} — имя не указано</li>
     *          <li>{@code TOO_LONG_ORGANIZATION_NAME} — имя длиннее 255 символов</li>
     *      </ul>
     */
    public static Organization create(String name) {
        return restore(null, name, null, null);
    }

    /**
     * Создаёт объект организации с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что организация новая.
     *
     * @param  id идентификатор, может быть {@code null} для новой организации
     * @param  name имя организации
     * @param  description описание, может быть {@code null}
     * @param  grade оценка, может быть {@code null}
     * @return организация с заданными параметрами
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_ORGANIZATION_NAME} — имя не указано</li>
     *          <li>{@code TOO_LONG_ORGANIZATION_NAME} — имя длиннее 255 символов</li>
     *          <li>{@code TOO_LONG_ORGANIZATION_DESCRIPTION} — описание длиннее 1024 символов</li>
     *          <li>{@code INVALID_ORGANIZATION_GRADE} — оценка вне диапазона [0, 5]</li>
     *          <li>{@code INVALID_ORGANIZATION_ID} — идентификатор не положительный</li>
     *      </ul>
     */
    public static Organization restore(Long id, String name, String description, Double grade) {
        String normalizedName = StringUtils.normalize(name);
        String normalizedDescription = StringUtils.normalize(description);

        validateId(id);
        validateName(normalizedName);
        validateDescription(normalizedDescription);
        validateGrade(grade);

        return new Organization(id, normalizedName, normalizedDescription, grade);
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getGrade() {
        return grade;
    }

    /* Validation */
    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_ORGANIZATION_ID,
                    "Invalid organization id",
                    Map.of("id", id)
            );
        }
    }

    private static void validateName(String name) {
        if (name == null) {
            throw AppException.build(
                    ErrorCode.EMPTY_ORGANIZATION_NAME,
                    "Empty organization name"
            );
        }

        if (name.length() > 255) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_ORGANIZATION_NAME,
                    "Too long organization name: " + name.length() + " symbols",
                    Map.of("name", name)
            );
        }
    }

    private static void validateDescription(String description) {
        if (description.length() > 1024) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_ORGANIZATION_DESCRIPTION,
                    "Too long organization description: " + description.length() + " symbols",
                    Map.of("description", description)
            );
        }
    }

    private static void validateGrade(Double grade) {
        if (grade != null && (grade < 0 || grade > 5)) {
            throw AppException.build(
                    ErrorCode.INVALID_ORGANIZATION_GRADE,
                    "Invalid organization grade",
                    Map.of("grade", grade)
            );
        }
    }
}

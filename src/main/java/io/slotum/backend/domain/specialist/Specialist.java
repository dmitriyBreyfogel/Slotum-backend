package io.slotum.backend.domain.specialist;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public final class Specialist {
    private final Long userId;
    private final String description;
    private final Double grade;

    private Specialist(Long userId, String description, Double grade) {
        this.userId = userId;
        this.description = description;
        this.grade = grade;
    }

    /**
     * Создание специалиста
     * @param userId идентификатор пользователя
     * @param description описание (может быть {@code null})
     * @param grade оценка (может быть {@code null})
     * @return созданный специалист по заданным параметрам
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_SPECIALIST_USER_ID} - идентификатор пользователя {@code null} или не положительный</li>
     *          <li>{@code TOO_LONG_SPECIALIST_DESCRIPTION} - описание содержит более 1024 символов</li>
     *          <li>{@code INVALID_SPECIALIST_GRADE} - оценка специалиста вне диапазона [0; 5]</li>
     *      </ul>
     */
    public static Specialist create(Long userId, String description, Double grade) {
        validateUserId(userId);

        String normalizedDescription = StringUtils.normalize(description);
        validateDescription(normalizedDescription);
        validateGrade(grade);

        return new Specialist(userId, normalizedDescription, grade);
    }

    /* Getters */
    public Long getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public Double getGrade() {
        return grade;
    }

    /* Validation */
    private static void validateUserId(Long id) {
        if (id == null || id <= 0) {
            Map<String, Object> details =
                    id == null
                    ? Map.of()
                    : Map.of("userId", id);

            throw AppException.build(
                    ErrorCode.INVALID_SPECIALIST_USER_ID,
                    "Invalid specialist userId",
                    details
            );
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 1024) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_SPECIALIST_DESCRIPTION,
                    "Too long specialist description",
                    Map.of(
                            "Expected max length", 1024,
                            "Actual length", description.length()
                    )
            );
        }
    }

    private static void validateGrade(Double grade) {
        if (grade != null && (grade < 0 || grade > 5)) {
            throw AppException.build(
                    ErrorCode.INVALID_SPECIALIST_GRADE,
                    "Invalid specialist grade",
                    Map.of("grade", grade)
            );
        }
    }
}

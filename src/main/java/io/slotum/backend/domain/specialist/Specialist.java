package io.slotum.backend.domain.specialist;

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

    public static Specialist create(Long userId, String description, Double grade) {
        validateUserId(userId);
        validateDescription(description);
        validateGrade(grade);
        return new Specialist(userId, description, grade);
    }

    public Long getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public Double getGrade() {
        return grade;
    }

    private static void validateUserId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_SPECIALIST_USER_ID,
                    "Invalid specialist userId",
                    Map.of("id", id)
            );
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw AppException.build(
                    ErrorCode.EMPTY_SPECIALIST_DESCRIPTION,
                    "Empty specialist description"
            );
        }

        if (description.length() > 1024) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_SPECIALIST_DESCRIPTION,
                    "Too long specialist description: " + description.length() + " symbols",
                    Map.of("description", description)
            );
        }
    }

    private static void validateGrade(Double grade) {
        if (grade < 0 || grade > 5) {
            throw AppException.build(
                    ErrorCode.INVALID_SPECIALIST_GRADE,
                    "Invalid specialist grade",
                    Map.of("grade", grade)
            );
        }
    }
}

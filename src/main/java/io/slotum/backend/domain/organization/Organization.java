package io.slotum.backend.domain.organization;

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

    public static Organization create(Long id, String name, String description, Double grade) {
        validateId(id);
        validateName(name);
        validateDescription(description);
        validateGrade(grade);

        return new Organization(
                id,
                normalizeName(name),
                description,
                grade
        );
    }

    public static Organization create(Long id, String name, String description) {
        validateId(id);
        validateName(name);
        validateDescription(description);

        return new Organization(
                id,
                normalizeName(name),
                description,
                0.0
        );
    }

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
        if (name == null || name.isEmpty()) {
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

    private static String normalizeName(String name) {
        return name.trim();
    }

    private static void validateDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw AppException.build(
                    ErrorCode.EMPTY_ORGANIZATION_DESCRIPTION,
                    "Empty organization description"
            );
        }

        if (description.length() > 1024) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_ORGANIZATION_DESCRIPTION,
                    "Too long organization description: " + description.length() + " symbols",
                    Map.of("description", description)
            );
        }
    }

    private static void validateGrade(Double grade) {
        if (grade < 0 || grade > 5) {
            throw AppException.build(
                    ErrorCode.INVALID_ORGANIZATION_GRADE,
                    "Invalid organization grade",
                    Map.of("grade", grade)
            );
        }
    }
}

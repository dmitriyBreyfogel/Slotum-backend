package io.slotum.backend.domain.role;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;
import java.util.Objects;

public final class Role {
    private final Long id;
    private final RoleNames name;
    private final String description;

    private Role(Long id, RoleNames name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Role create(RoleNames name, String description) {
        return restore(null, name, description);
    }

    public static Role restore(Long id, RoleNames name, String description) {
        String normalizeDescription = normalizeDescription(description);

        validateId(id);
        validateName(name);
        validateDescription(normalizeDescription);

        return new Role(id, name, normalizeDescription);
    }

    private static String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_ROLE_ID,
                    "Invalid role id",
                    Map.of( "id", id )
            );
        }
    }

    private static void validateName(RoleNames name) {
        if (name == null) {
            throw AppException.build(
                    ErrorCode.EMPTY_ROLE_NAME,
                    "Empty role name"
            );
        }
    }

    private static void validateDescription(String description) {
        if (description != null && (description.length() > 255)) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_ROLE_DESCRIPTION,
                    "Too long role description",
                    Map.of(
                            "Expected max length", 255,
                            "Actual length", description.length()
                    )
            );
        }
    }
}

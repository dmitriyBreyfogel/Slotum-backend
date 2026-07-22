package io.slotum.backend.domain.permission;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public final class Permission {
    private final Long id;
    private final String code;
    private final String description;

    private Permission(Long id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public static Permission create(String code, String description) {
        return restore(null, code, description);
    }

    public static Permission restore(Long id, String code, String description) {
        String normalizeCode = StringUtils.normalize(code);
        String normalizeDescription = StringUtils.normalize(description);

        validateId(id);
        validateCode(normalizeCode);
        validateDescription(normalizeDescription);

        return new Permission(id, normalizeCode, normalizeDescription);
    }

    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_PERMISSION_ID,
                    "Invalid permission id",
                    Map.of("id", id)
            );
        }
    }

    private static void validateCode(String code) {
        if (code == null) {
            throw AppException.build(
                    ErrorCode.EMPTY_PERMISSION_CODE,
                    "Empty permission code"
            );
        }

        if (code.length() > 100) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_PERMISSION_CODE,
                    "Too long permission code",
                    Map.of(
                            "Expected max length", 100,
                            "Actual length", code.length()
                    )
            );
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 255) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_PERMISSION_DESCRIPTION,
                    "Too long permission description",
                    Map.of(
                            "Expected max length", 255,
                            "Actual length", description.length()
                    )
            );
        }
    }
}

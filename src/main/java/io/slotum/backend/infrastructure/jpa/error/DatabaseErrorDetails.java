package io.slotum.backend.infrastructure.jpa.error;

import java.util.Optional;

public record DatabaseErrorDetails(
        String message,
        String sqlState,
        Integer vendorCode,
        String constraintName,
        String schemaName,
        String tableName,
        String columnName,
        String detail,
        String hint
) {
    public Optional<String> messageOptional() {
        return optional(message);
    }

    public Optional<String> sqlStateOptional() {
        return optional(sqlState);
    }

    public Optional<Integer> vendorCodeOptional() {
        return Optional.ofNullable(vendorCode);
    }

    public Optional<String> constraintNameOptional() {
        return optional(constraintName);
    }

    public Optional<String> schemaNameOptional() {
        return optional(schemaName);
    }

    public Optional<String> tableNameOptional() {
        return optional(tableName);
    }

    public Optional<String> columnNameOptional() {
        return optional(columnName);
    }

    public Optional<String> detailOptional() {
        return optional(detail);
    }

    public Optional<String> hintOptional() {
        return optional(hint);
    }

    private static Optional<String> optional(String value) {
        return Optional.ofNullable(value)
                .filter(v -> !v.isBlank());
    }
}

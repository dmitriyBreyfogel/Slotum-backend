package io.slotum.backend.infrastructure.jpa.error.extractors;

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
    public static DatabaseErrorDetails empty() {
        return new DatabaseErrorDetails(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public DatabaseErrorDetails merge(final DatabaseErrorDetails other) {
        if (other == null) {
            return this;
        }

        return new DatabaseErrorDetails(
                firstNonBlank(message, other.message),
                firstNonBlank(sqlState, other.sqlState),
                firstNonNull(vendorCode, other.vendorCode),
                firstNonBlank(constraintName, other.constraintName),
                firstNonBlank(schemaName, other.schemaName),
                firstNonBlank(tableName, other.tableName),
                firstNonBlank(columnName, other.columnName),
                firstNonBlank(detail, other.detail),
                firstNonBlank(hint, other.hint)
        );
    }

    private String firstNonBlank(final String first, final String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }

        if (second != null && !second.isBlank()) {
            return second;
        }

        return first;
    }

    private Integer firstNonNull(final Integer first, final Integer second) {
        if (first == null) {
            return second;
        }

        return first;
    }

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

package io.slotum.backend.infrastructure.jpa.error;

import io.slotum.backend.error.AppException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public final class DatabaseConstraintExceptionResolver {

    private final DatabaseErrorExtractor databaseErrorExtractor;
    private final List<DatabaseConstraintErrorMapper> constraintErrorMappers;

    public DatabaseConstraintExceptionResolver(
            DatabaseErrorExtractor databaseErrorExtractor,
            List<DatabaseConstraintErrorMapper> constraintErrorMappers
    ) {
        this.databaseErrorExtractor = databaseErrorExtractor;
        this.constraintErrorMappers = constraintErrorMappers;
    }

    public Optional<AppException> resolve(
            Throwable throwable,
            Map<String, Object> details
    ) {
        DatabaseErrorDetails databaseErrorDetails = databaseErrorExtractor.extract(throwable);

        return databaseErrorDetails.constraintNameOptional()
                .flatMap(constraintName -> constraintErrorMappers.stream()
                        .filter(mapper -> mapper.supports(constraintName))
                        .findFirst()
                        .map(mapper -> mapper.buildException(details)));
    }
}
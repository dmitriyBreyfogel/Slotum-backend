package io.slotum.backend.infrastructure.jpa.error.extractors;

public interface DatabaseErrorExtractor {
    DatabaseErrorDetails extract(Throwable ex);
}

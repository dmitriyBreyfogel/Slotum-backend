package io.slotum.backend.infrastructure.jpa.error;

public interface DatabaseErrorExtractor {
    DatabaseErrorDetails extract(Throwable ex);
}

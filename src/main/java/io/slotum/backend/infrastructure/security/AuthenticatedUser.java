package io.slotum.backend.infrastructure.security;

public record AuthenticatedUser(
        long userId,
        String email
) {
}

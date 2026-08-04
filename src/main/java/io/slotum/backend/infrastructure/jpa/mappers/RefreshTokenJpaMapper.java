package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.refreshToken.RefreshToken;
import io.slotum.backend.infrastructure.jpa.entities.RefreshTokenJpa;

public final class RefreshTokenJpaMapper {
    private RefreshTokenJpaMapper() {}

    public static RefreshToken toDomain(RefreshTokenJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("RefreshTokenJpa source is null");
        }

        return RefreshToken.restore(
                source.getId(),
                source.getUserId(),
                source.getTokenHash(),
                source.getIssuedAt(),
                source.getExpiresAt(),
                source.getRevoked()
        );
    }

    public static RefreshTokenJpa toJpa(RefreshToken source) {
        if (source == null) {
            throw new IllegalArgumentException("RefreshToken source is null");
        }

        return new RefreshTokenJpa(
                source.getId(),
                source.getUserId(),
                source.getTokenHash(),
                source.getIssuedAt(),
                source.getIssuedAt(),
                source.getRevoked()
        );
    }
}

package io.slotum.backend.application.usecase.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class CountByRevokedRefreshTokensUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public CountByRevokedRefreshTokensUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Long execute(boolean revoked) {
        return refreshTokenRepository.countByRevoked(revoked);
    }
}

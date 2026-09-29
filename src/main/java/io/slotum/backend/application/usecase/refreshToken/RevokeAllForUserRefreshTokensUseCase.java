package io.slotum.backend.application.usecase.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class RevokeAllForUserRefreshTokensUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public RevokeAllForUserRefreshTokensUseCase(RefreshTokenRepository repository) {
        this.refreshTokenRepository = repository;
    }

    public void execute(Long userId) {
        refreshTokenRepository.revokeAllForUser(userId);
    }
}

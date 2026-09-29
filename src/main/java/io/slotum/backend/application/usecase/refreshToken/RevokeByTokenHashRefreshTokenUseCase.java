package io.slotum.backend.application.usecase.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class RevokeByTokenHashRefreshTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public RevokeByTokenHashRefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(String tokenHash) {
        refreshTokenRepository.revokeByTokenHash(tokenHash);
    }
}

package io.slotum.backend.application.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class CountRefreshTokensUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public CountRefreshTokensUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Long execute() {
        return refreshTokenRepository.count();
    }
}

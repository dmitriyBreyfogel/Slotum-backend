package io.slotum.backend.application.usecase.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshToken;
import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllByUserIdRefreshTokensUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public GetAllByUserIdRefreshTokensUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public List<RefreshToken> execute(Long userId) {
        return refreshTokenRepository.findAllByUserId(userId);
    }
}

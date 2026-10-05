package io.slotum.backend.application.usecase.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshToken;
import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRefreshTokensUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public GetAllRefreshTokensUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public List<RefreshToken> execute() {
        return refreshTokenRepository.findAll();
    }
}

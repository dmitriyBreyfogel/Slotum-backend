package io.slotum.backend.application.usecase.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshToken;
import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetByTokenHashRefreshTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public GetByTokenHashRefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken execute(String tokenHash) {
        Optional<RefreshToken> result = refreshTokenRepository.findByTokenHash(tokenHash);

        if (result.isEmpty()) {
            throw AppException.build(
                    ErrorCode.REFRESH_TOKEN_NOT_FOUND,
                    "Refresh token not found",
                    Map.of("tokenHash", tokenHash)
            );
        }

        return result.get();
    }
}

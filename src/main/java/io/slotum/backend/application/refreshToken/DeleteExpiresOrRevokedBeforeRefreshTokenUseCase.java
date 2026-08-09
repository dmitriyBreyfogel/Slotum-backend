package io.slotum.backend.application.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class DeleteExpiresOrRevokedBeforeRefreshTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public DeleteExpiresOrRevokedBeforeRefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public void execute(Instant cutoff) {
        if (cutoff.isAfter(Instant.now())) {
            throw AppException.build(
                    ErrorCode.INVALID_REFRESH_TOKEN_TIME_RANGE,
                    "Cutoff must be in the past",
                    Map.of("cutoff", cutoff)
            );
        }

        refreshTokenRepository.deleteExpiredOrRevokedBefore(cutoff);
    }
}

package io.slotum.backend.application.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshToken;
import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetRefreshTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public GetRefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken execute(Long id) {
        Optional<RefreshToken> result = refreshTokenRepository.findById(id);

        if (result.isEmpty()) {
            throw AppException.build(
                    ErrorCode.REFRESH_TOKEN_NOT_FOUND,
                    "Refresh token not found",
                    Map.of("id", id)
            );
        }

        return result.get();
    }
}

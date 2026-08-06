package io.slotum.backend.application.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshToken;
import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CreateRefreshTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public CreateRefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken execute(Command command) {
        RefreshToken result = RefreshToken.create(command.userId(), command.tokenHash(), command.expiresAt);
        return refreshTokenRepository.save(result);
    }

    public record Command(
            Long userId,
            String tokenHash,
            Instant expiresAt
    ) {}
}

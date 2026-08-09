package io.slotum.backend.api.refreshToken;

import io.slotum.backend.api.refreshToken.dto.RefreshTokenDto;
import io.slotum.backend.application.refreshToken.*;
import io.slotum.backend.domain.refreshToken.RefreshToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Validated
@RestController
public class RefreshTokenController implements RefreshTokenApi {
    private final GetRefreshTokenUseCase getRefreshTokenUseCase;
    private final GetByTokenHashRefreshTokenUseCase getByTokenHashRefreshTokenUseCase;
    private final GetAllRefreshTokensUseCase getAllRefreshTokensUseCase;
    private final GetAllByUserIdRefreshTokensUseCase getAllByUserIdRefreshTokensUseCase;
    private final CountRefreshTokensUseCase countRefreshTokensUseCase;
    private final CountByRevokedRefreshTokensUseCase countByRevokedRefreshTokensUseCase;
    private final RevokeByTokenHashRefreshTokenUseCase revokeByTokenHashRefreshTokenUseCase;
    private final RevokeAllForUserRefreshTokensUseCase revokeAllForUserRefreshTokensUseCase;
    private final DeleteExpiresOrRevokedBeforeRefreshTokenUseCase deleteExpiresOrRevokedBeforeRefreshTokenUseCase;

    public RefreshTokenController(
            GetRefreshTokenUseCase getRefreshTokenUseCase,
            GetByTokenHashRefreshTokenUseCase getByTokenHashRefreshTokenUseCase,
            GetAllRefreshTokensUseCase getAllRefreshTokensUseCase,
            GetAllByUserIdRefreshTokensUseCase getAllByUserIdRefreshTokensUseCase,
            CountRefreshTokensUseCase countRefreshTokensUseCase,
            CountByRevokedRefreshTokensUseCase countByRevokedRefreshTokensUseCase,
            RevokeByTokenHashRefreshTokenUseCase revokeByTokenHashRefreshTokenUseCase,
            RevokeAllForUserRefreshTokensUseCase revokeAllForUserRefreshTokensUseCase,
            DeleteExpiresOrRevokedBeforeRefreshTokenUseCase deleteExpiresOrRevokedBeforeRefreshTokenUseCase
    ) {
        this.getRefreshTokenUseCase = getRefreshTokenUseCase;
        this.getByTokenHashRefreshTokenUseCase = getByTokenHashRefreshTokenUseCase;
        this.getAllRefreshTokensUseCase = getAllRefreshTokensUseCase;
        this.getAllByUserIdRefreshTokensUseCase = getAllByUserIdRefreshTokensUseCase;
        this.countRefreshTokensUseCase = countRefreshTokensUseCase;
        this.countByRevokedRefreshTokensUseCase = countByRevokedRefreshTokensUseCase;
        this.revokeByTokenHashRefreshTokenUseCase = revokeByTokenHashRefreshTokenUseCase;
        this.revokeAllForUserRefreshTokensUseCase = revokeAllForUserRefreshTokensUseCase;
        this.deleteExpiresOrRevokedBeforeRefreshTokenUseCase = deleteExpiresOrRevokedBeforeRefreshTokenUseCase;
    }

    @Override
    public ResponseEntity<RefreshTokenDto> getById(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getRefreshTokenUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<RefreshTokenDto> getRefreshTokenByHash(String hash) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getByTokenHashRefreshTokenUseCase.execute(hash)));
    }

    @Override
    public ResponseEntity<List<RefreshTokenDto>> getAllRefreshTokens() {
        return ResponseEntity.status(HttpStatus.OK).body(
                getAllRefreshTokensUseCase.execute()
                        .stream()
                        .map(RefreshTokenController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<List<RefreshTokenDto>> getAllRefreshTokensByUser(Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                getAllByUserIdRefreshTokensUseCase.execute(userId)
                        .stream()
                        .map(RefreshTokenController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<Long> countRefreshTokens() {
        return ResponseEntity.status(HttpStatus.OK).body(countRefreshTokensUseCase.execute());
    }

    @Override
    public ResponseEntity<Long> countByRevokedRefreshTokens(boolean revoked) {
        return ResponseEntity.status(HttpStatus.OK).body(countByRevokedRefreshTokensUseCase.execute(revoked));
    }

    @Override
    public ResponseEntity<Void> revokeRefreshTokenByHash(String hash) {
        revokeByTokenHashRefreshTokenUseCase.execute(hash);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<Void> revokeRefreshTokensByUserId(Long userId) {
        revokeAllForUserRefreshTokensUseCase.execute(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<Void> deleteExpiredOrRevokedBefore(Instant cutoff) {
        deleteExpiresOrRevokedBeforeRefreshTokenUseCase.execute(cutoff);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private static RefreshTokenDto toDto(RefreshToken refreshToken) {
        return new RefreshTokenDto(
                refreshToken.getId(),
                refreshToken.getUserId(),
                refreshToken.getTokenHash(),
                refreshToken.getIssuedAt(),
                refreshToken.getExpiresAt(),
                refreshToken.getRevoked()
        );
    }
}

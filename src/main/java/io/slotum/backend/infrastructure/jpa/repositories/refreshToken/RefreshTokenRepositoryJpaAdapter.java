package io.slotum.backend.infrastructure.jpa.repositories.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshToken;
import io.slotum.backend.domain.refreshToken.RefreshTokenRepository;
import io.slotum.backend.infrastructure.jpa.mappers.RefreshTokenJpaMapper;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class RefreshTokenRepositoryJpaAdapter implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshTokenRepositoryJpaAdapter(RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    @Override
    public Optional<RefreshToken> findById(Long id) {
        return refreshTokenJpaRepository.findById(id).map(RefreshTokenJpaMapper::toDomain);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHash(tokenHash).map(RefreshTokenJpaMapper::toDomain);
    }

    @Override
    public List<RefreshToken> findAllByUserId(Long userId) {
        return refreshTokenJpaRepository.findAllByUserId(userId)
                .stream()
                .map(RefreshTokenJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<RefreshToken> findAll() {
        return refreshTokenJpaRepository.findAll()
                .stream()
                .map(RefreshTokenJpaMapper::toDomain)
                .toList();
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return RefreshTokenJpaMapper.toDomain(
                refreshTokenJpaRepository.save(RefreshTokenJpaMapper.toJpa(refreshToken))
        );
    }

    @Override
    public void revokeAllForUser(Long userId) {
        refreshTokenJpaRepository.revokeAllForUser(userId);
    }

    @Override
    public void revokeByTokenHash(String tokenHash) {
        refreshTokenJpaRepository.revokeByTokenHash(tokenHash);
    }

    @Override
    public Long count() {
        return refreshTokenJpaRepository.count();
    }

    @Override
    public Long countByRevoked(boolean revoked) {
        return refreshTokenJpaRepository.countByRevoked(revoked);
    }

    @Override
    public void deleteExpiredOrRevokedBefore(Instant cutoff) {
        refreshTokenJpaRepository.deleteExpiredOrRevokedBefore(cutoff);
    }
}

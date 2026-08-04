package io.slotum.backend.infrastructure.jpa.repositories.refreshToken;

import io.slotum.backend.infrastructure.jpa.entities.RefreshTokenJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpa, Long> {

    Optional<RefreshTokenJpa> findByTokenHash(String tokenHash);

    List<RefreshTokenJpa> findAllByUserId(Long userId);

    Long countByRevoked(boolean revoked);

    @Modifying
    @Query("""
        UPDATE RefreshTokenJpa r SET r.revoked = true
        WHERE r.userId = :userId AND r.revoked = false
    """)
    void revokeAllForUser(@Param("userId") Long userId);

    @Modifying
    @Query("""
        UPDATE RefreshTokenJpa r SET r.revoked = true
        WHERE r.tokenHash = :tokenHash
    """)
    void revokeByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying
    @Query("""
        DELETE FROM RefreshTokenJpa r WHERE r.expiresAt < :cutoff OR r.revoked = true
    """)
    void deleteExpiredOrRevokedBefore(@Param("cutoff") Instant cutoff);
}

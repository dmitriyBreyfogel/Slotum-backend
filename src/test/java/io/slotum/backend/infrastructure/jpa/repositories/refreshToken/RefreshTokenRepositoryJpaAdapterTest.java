package io.slotum.backend.infrastructure.jpa.repositories.refreshToken;

import io.slotum.backend.domain.refreshToken.RefreshToken;
import io.slotum.backend.infrastructure.jpa.entities.RefreshTokenJpa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenRepositoryJpaAdapterTest {

    private static final Long ID = 1L;
    private static final String TOKEN_HASH = "hash";
    private static final Long USER_ID = 2L;

    @Mock
    private RefreshTokenJpaRepository jpaRepository;

    @InjectMocks
    private RefreshTokenRepositoryJpaAdapter adapter;

    /* Поиск по id */
    @Test
    @DisplayName("Поиск refresh токена по id")
    void findById() {
        RefreshTokenJpa jpa = createJpa(ID, TOKEN_HASH, USER_ID);
        when(jpaRepository.findById(ID)).thenReturn(Optional.of(jpa));

        Optional<RefreshToken> found = adapter.findById(ID);

        assertTrue(found.isPresent());
        assertEquals(ID, found.get().getId());
        assertEquals(TOKEN_HASH, found.get().getTokenHash());
        assertEquals(USER_ID, found.get().getUserId());
    }

    @Test
    @DisplayName("Поиск несуществующего refresh токена по id")
    void findByIdNotFound() {
        when(jpaRepository.findById(ID)).thenReturn(Optional.empty());

        Optional<RefreshToken> found = adapter.findById(ID);

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск refresh токена по null id")
    void findByIdNull() {
        when(jpaRepository.findById(null)).thenReturn(Optional.empty());

        Optional<RefreshToken> found = adapter.findById(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск по хэшу */
    @Test
    @DisplayName("Поиск refresh токена по хэшу")
    void findByTokenHash() {
        RefreshTokenJpa jpa = createJpa(ID, TOKEN_HASH, USER_ID);
        when(jpaRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(jpa));

        Optional<RefreshToken> found = adapter.findByTokenHash(TOKEN_HASH);

        assertTrue(found.isPresent());
        assertEquals(TOKEN_HASH, found.get().getTokenHash());
    }

    @Test
    @DisplayName("Поиск refresh токена по несуществующему хэшу")
    void findByTokenHashNotFound() {
        when(jpaRepository.findByTokenHash("unknown")).thenReturn(Optional.empty());

        Optional<RefreshToken> found = adapter.findByTokenHash("unknown");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Поиск refresh токена по null хэшу")
    void findByTokenHashNull() {
        when(jpaRepository.findByTokenHash(null)).thenReturn(Optional.empty());

        Optional<RefreshToken> found = adapter.findByTokenHash(null);

        assertTrue(found.isEmpty());
    }

    /* Поиск всех */
    @Test
    @DisplayName("Поиск всех refresh токенов")
    void findAll() {
        when(jpaRepository.findAll()).thenReturn(List.of(
                createJpa(1L, "hash1", 2L),
                createJpa(2L, "hash2", 3L)
        ));

        List<RefreshToken> tokens = adapter.findAll();

        assertEquals(2, tokens.size());
    }

    @Test
    @DisplayName("Поиск всех refresh токенов при пустой базе")
    void findAllEmpty() {
        when(jpaRepository.findAll()).thenReturn(List.of());

        List<RefreshToken> tokens = adapter.findAll();

        assertTrue(tokens.isEmpty());
    }

    @Test
    @DisplayName("Поиск refresh токенов по пользователю")
    void findAllByUserId() {
        when(jpaRepository.findAllByUserId(USER_ID)).thenReturn(List.of(
                createJpa(1L, "hash1", USER_ID),
                createJpa(2L, "hash2", USER_ID)
        ));

        List<RefreshToken> tokens = adapter.findAllByUserId(USER_ID);

        assertEquals(2, tokens.size());
    }

    @Test
    @DisplayName("Поиск refresh токенов по несуществующему пользователю")
    void findAllByUserIdEmpty() {
        when(jpaRepository.findAllByUserId(USER_ID)).thenReturn(List.of());

        List<RefreshToken> tokens = adapter.findAllByUserId(USER_ID);

        assertTrue(tokens.isEmpty());
    }

    /* Сохранение */
    @Test
    @DisplayName("Сохранение refresh токена")
    void save() {
        RefreshToken token = RefreshToken.create(USER_ID, TOKEN_HASH, Instant.now().plusSeconds(3600));
        RefreshTokenJpa jpa = createJpa(ID, TOKEN_HASH, USER_ID);

        when(jpaRepository.save(any(RefreshTokenJpa.class))).thenReturn(jpa);

        RefreshToken saved = adapter.save(token);

        assertNotNull(saved.getId());
        assertEquals(TOKEN_HASH, saved.getTokenHash());
        assertEquals(USER_ID, saved.getUserId());
        verify(jpaRepository, times(1)).save(any(RefreshTokenJpa.class));
    }

    /* Ревокация */
    @Test
    @DisplayName("Аннулирование всех токенов пользователя")
    void revokeAllForUser() {
        adapter.revokeAllForUser(USER_ID);

        verify(jpaRepository, times(1)).revokeAllForUser(USER_ID);
    }

    @Test
    @DisplayName("Аннулирование токена по хэшу")
    void revokeByTokenHash() {
        adapter.revokeByTokenHash(TOKEN_HASH);

        verify(jpaRepository, times(1)).revokeByTokenHash(TOKEN_HASH);
    }

    /* Подсчёт */
    @Test
    @DisplayName("Подсчёт всех refresh токенов")
    void count() {
        when(jpaRepository.count()).thenReturn(5L);

        Long count = adapter.count();

        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Подсчёт аннулированных refresh токенов")
    void countByRevoked() {
        when(jpaRepository.countByRevoked(true)).thenReturn(3L);

        Long count = adapter.countByRevoked(true);

        assertEquals(3L, count);
    }

    /* Удаление */
    @Test
    @DisplayName("Удаление истёкших или аннулированных токенов")
    void deleteExpiredOrRevokedBefore() {
        Instant cutoff = Instant.now().minusSeconds(3600);

        adapter.deleteExpiredOrRevokedBefore(cutoff);

        verify(jpaRepository, times(1)).deleteExpiredOrRevokedBefore(cutoff);
    }

    /* Вспомогательный метод */
    private RefreshTokenJpa createJpa(Long id, String tokenHash, Long userId) {
        Instant issuedAt = Instant.now().minusSeconds(60);
        Instant expiresAt = Instant.now().plusSeconds(3600);
        return new RefreshTokenJpa(id, userId, tokenHash, issuedAt, expiresAt, false);
    }
}
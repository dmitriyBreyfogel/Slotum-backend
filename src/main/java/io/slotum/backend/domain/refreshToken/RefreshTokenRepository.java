package io.slotum.backend.domain.refreshToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository {

    /**
     * Поиск refresh-токена по идентификатору.
     *
     * @param id идентификатор токена. Обязан быть не {@code null}
     * @return объект токена, если найти удалось.
     *         {@code Optional.empty()}, если найти не удалось
     */
    Optional<RefreshToken> findById(Long id);

    /**
     * Поиск refresh-токена по хэшу.
     *
     * @param tokenHash хэш токена. Обязан быть не {@code null}
     * @return объект токена, если найти удалось.
     *         {@code Optional.empty()}, если найти не удалось
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Возвращает все refresh-токены пользователя.
     *
     * @param userId идентификатор пользователя. Обязан быть не {@code null}
     * @return список токенов пользователя. Если токенов нет, возвращается пустой список.
     *         Никогда не {@code null}
     */
    List<RefreshToken> findAllByUserId(Long userId);

    /**
     * Возвращает все refresh-токены.
     *
     * @return список всех токенов. Если токенов нет, возвращается пустой список.
     *         Никогда не {@code null}
     */
    List<RefreshToken> findAll();

    /**
     * Сохранение refresh-токена в базу данных.
     * Используется при создании нового токена и при аннуляции (revoke).
     *
     * @param refreshToken объект сохраняемого токена. Обязан быть не {@code null}
     * @return сохранённый объект токена. Никогда не будет {@code null}
     */
    RefreshToken save(RefreshToken refreshToken);

    /**
     * Аннулирует все активные токены пользователя.
     * Используется при логауте и при компрометации аккаунта.
     * Если активных токенов нет, метод ничего не делает.
     *
     * @param userId идентификатор пользователя. Обязан быть не {@code null}
     */
    void revokeAllForUser(Long userId);

    /**
     * Аннулирует токен по хэшу.
     * Используется при refresh token rotation (старый токен отзывается).
     * Если токен с таким хэшем не найден, метод ничего не делает.
     *
     * @param tokenHash хэш токена. Обязан быть не {@code null}
     */
    void revokeByTokenHash(String tokenHash);

    /**
     * Возвращает общее количество refresh-токенов.
     *
     * @return количество токенов. Никогда не {@code null}
     */
    Long count();

    /**
     * Возвращает количество токенов по статусу аннуляции.
     *
     * @param revoked {@code true} — аннулированные, {@code false} — активные
     * @return количество токенов с заданным статусом. Никогда не {@code null}
     */
    Long countByRevoked(boolean revoked);

    /**
     * Удаляет истёкшие или аннулированные токены.
     * Используется для периодической очистки базы данных.
     * Если подходящих токенов нет, метод ничего не делает.
     *
     * @param cutoff момент времени, до которого (строго раньше) удаляются истёкшие токены.
     *               Обязан быть не {@code null}
     */
    void deleteExpiredOrRevokedBefore(Instant cutoff);
}

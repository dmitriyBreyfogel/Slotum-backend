package io.slotum.backend.domain.slot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepository {

    /**
     * Поиск слота по идентификатору
     * @param id идентификатор слота. Обязан быть не {@code null}
     * @return объект найденного слота, если найти удалось. {@code Optional.empty()},
     * если найти не удалось
     */
    Optional<Slot> findById(Long id);

    /**
     * Сохранение слота в базу данных
     * @param slot объект сохраняемого слота. Обязан быть не {@code null}
     * @return сохранённый объект слота. Никогда не {@code null}
     */
    Slot save(Slot slot);

    /**
     * Получение всех имеющихся слотов
     * @return список имеющихся слотов. Если слоты отсутствуют - метод вернёт пустой список
     */
    List<Slot> findAll();

    /**
     * Удаление слота по его идентификатору
     * @param id идентификатор удаляемого слота. Обязан быть не {@code null}
     * @return объект удалённого слота. Если слот по идентификатору найти не удалось -
     * метод вернёт {@code Optional.empty()}
     */
    Optional<Slot> deleteById(Long id);

    /**
     * Удаление всех имеющихся слотов.
     * Если слоты отсутствуют - метод ничего не делает
     */
    void deleteAll();

    /**
     * Проверка существования пересекающегося слота специалиста
     * @param specialistUserId идентификатор пользователя-специалиста. Обязан быть не {@code null}
     * @param startsAt дата и время начала проверяемого интервала. Обязаны быть не {@code null}
     * @param endsAt дата и время окончания проверяемого интервала. Обязаны быть не {@code null}
     * @return {@code true}, если пересекающийся слот существует. Иначе {@code false}
     */
    boolean existsOverlappingSlot(Long specialistUserId, LocalDateTime startsAt, LocalDateTime endsAt);

    /**
     * Бронирование свободного слота указанным пользователем
     * @param slotId идентификатор слота. Обязан быть не {@code null}
     * @param specialistUserId идентификатор пользователя-специалиста. Обязан быть не {@code null}
     * @param customerId идентификатор пользователя-клиента. Обязан быть не {@code null}
     * @return {@code true}, если свободный слот удалось забронировать. Иначе {@code false}
     */
    boolean bookIfFree(Long slotId, Long specialistUserId, Long customerId);
}

package io.slotum.backend.domain.slotBookingRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SlotBookingRequestRepository {

    /**
     * Поиск заявки на бронирование слота по идентификатору
     * @param id идентификатор заявки. Обязан быть не {@code null}
     * @return объект найденной заявки, если найти удалось. {@code Optional.empty()},
     * если найти не удалось
     */
    Optional<SlotBookingRequest> findById(Long id);

    /**
     * Получение всех имеющихся заявок на бронирование слотов
     * @return список имеющихся заявок. Если заявки отсутствуют - метод вернёт пустой список
     */
    List<SlotBookingRequest> findAll();

    /**
     * Получение всех заявок на бронирование указанного слота
     * @param slotId идентификатор слота. Обязан быть не {@code null}
     * @return список заявок на бронирование слота. Если заявки отсутствуют - метод вернёт
     * пустой список
     */
    List<SlotBookingRequest> findBySlotId(Long slotId);

    /**
     * Получение всех ожидающих рассмотрения заявок на бронирование указанного слота
     * @param slotId идентификатор слота. Обязан быть не {@code null}
     * @return список ожидающих рассмотрения заявок. Если заявки отсутствуют - метод вернёт
     * пустой список
     */
    List<SlotBookingRequest> findPendingBySlotId(Long slotId);

    /**
     * Получение всех заявок указанного клиента
     * @param customerId идентификатор пользователя-клиента. Обязан быть не {@code null}
     * @return список заявок клиента. Если заявки отсутствуют - метод вернёт пустой список
     */
    List<SlotBookingRequest> findByCustomerId(Long customerId);

    /**
     * Получение всех входящих заявок указанного специалиста
     * @param specialistUserId идентификатор пользователя-специалиста. Обязан быть не {@code null}
     * @return список входящих заявок специалиста. Если заявки отсутствуют - метод вернёт
     * пустой список
     */
    List<SlotBookingRequest> findBySpecialistUserId(Long specialistUserId);

    /**
     * Проверка существования ожидающей рассмотрения заявки клиента на указанный слот
     * @param slotId идентификатор слота. Обязан быть не {@code null}
     * @param customerId идентификатор пользователя-клиента. Обязан быть не {@code null}
     * @return {@code true}, если ожидающая рассмотрения заявка существует. Иначе {@code false}
     */
    boolean existsPendingBySlotIdAndCustomerId(Long slotId, Long customerId);

    /**
     * Сохранение заявки на бронирование слота в базу данных
     * @param slotBookingRequest объект сохраняемой заявки. Обязан быть не {@code null}
     * @return сохранённый объект заявки. Никогда не {@code null}
     */
    SlotBookingRequest save(SlotBookingRequest slotBookingRequest);

    /**
     * Принятие заявки, если она ожидает рассмотрения
     * @param id идентификатор заявки. Обязан быть не {@code null}
     * @param decidedAt дата и время принятия решения. Обязаны быть не {@code null}
     * @return {@code true}, если заявку удалось принять. Иначе {@code false}
     */
    boolean acceptIfPending(Long id, LocalDateTime decidedAt);

    /**
     * Отклонение заявки, если она ожидает рассмотрения
     * @param id идентификатор заявки. Обязан быть не {@code null}
     * @param decidedAt дата и время принятия решения. Обязаны быть не {@code null}
     * @return {@code true}, если заявку удалось отклонить. Иначе {@code false}
     */
    boolean rejectIfPending(Long id, LocalDateTime decidedAt);

    /**
     * Отмена заявки, если она ожидает рассмотрения
     * @param id идентификатор заявки. Обязан быть не {@code null}
     * @param decidedAt дата и время отмены. Обязаны быть не {@code null}
     * @return {@code true}, если заявку удалось отменить. Иначе {@code false}
     */
    boolean cancelIfPending(Long id, LocalDateTime decidedAt);
}

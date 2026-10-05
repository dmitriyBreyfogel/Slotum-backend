package io.slotum.backend.domain.notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    /**
     * Получение уведомления
     * @param id идентификатор уведомления, обязан быть не {@code null}
     * @return объект найденного уведомления, если найти удалось. Иначе {@code Optional.empty()}
     */
    Optional<Notification> findById(Long id);

    /**
     * Получение всех уведомлений пользователя
     * @param userId идентификатор пользователя, обязан быть не {@code null}
     * @return список найденных уведомлений пользователя. Если уведомлений нет -
     * пустой список
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Получение всех непрочитанных уведомлений пользователя
     * @param userId идентификатор пользователя. Обязан быть не {@code null}
     * @return список найденных непрочитанных уведомлений пользователя.
     * Если таких уведомлений нет - пустой список
     */
    List<Notification> findUnreadByUserId(Long userId);

    /**
     * Подсчёт непрочитанных уведомлений пользователя
     * @param userId идентификатор пользователя. Обязан быть не {@code null}
     * @return количество непрочитанных уведомлений пользователя
     */
    long countUnreadByUserId(Long userId);

    /**
     * Сохранение уведомления в базу данных
     * @param notification уведомление для сохранения
     * @return сохранённый объект уведомления
     */
    Notification save(Notification notification);

    /**
     * Делает уведомление прочитанным
     * @param notificationId идентификатор уведомления. Обязан быть не {@code null}
     * @param userId идентификатор пользователя, которому принадлежит данное уведомление. Обязан быть не {@code null}
     * @return {@code true} - если удалось прочитать уведомление. {@code false} - иначе
     */
    boolean read(Long notificationId, Long userId);

    /**
     * Делает все уведомления пользователя прочитанными
     * @param userId идентификатор пользователя
     * @return количество уведомлений, которые были прочитаны
     */
    long readAll(Long userId);

    /**
     * Удаление уведомления
     * @param id идентификатор уведомления
     * @return объект удалённого уведомления.
     * {@code Optional.empty()} если такого уведомления не было
     */
    Optional<Notification> deleteById(Long id);

    /**
     * Удаление всех уведомлений
     */
    void deleteAll();
}

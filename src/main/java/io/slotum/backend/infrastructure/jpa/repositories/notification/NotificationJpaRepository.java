package io.slotum.backend.infrastructure.jpa.repositories.notification;

import io.slotum.backend.infrastructure.jpa.entities.NotificationJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpa, Long> {

    List<NotificationJpa> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<NotificationJpa> findAllByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        update NotificationJpa notification
        set notification.isRead = true
        where notification.id = :notificationId
            and notification.userId = :userId
            and notification.isRead = false
    """)
    int read(
            @Param("notificationId") Long notificationId,
            @Param("userId") Long userId
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
       update NotificationJpa notification
       set notification.isRead = true
       where notification.userId = :userId
            and notification.isRead = false 
    """)
    int readAll(@Param("userId") Long userId);
}

package io.slotum.backend.infrastructure.jpa.repositories.notification;

import io.slotum.backend.domain.notification.Notification;
import io.slotum.backend.domain.notification.NotificationRepository;
import io.slotum.backend.infrastructure.jpa.entities.NotificationJpa;
import io.slotum.backend.infrastructure.jpa.mappers.NotificationJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NotificationRepositoryJpaAdapter implements NotificationRepository {
    private final NotificationJpaRepository notificationJpaRepository;

    public NotificationRepositoryJpaAdapter(NotificationJpaRepository notificationJpaRepository) {
        this.notificationJpaRepository = notificationJpaRepository;
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id).map(NotificationJpaMapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        return notificationJpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        return notificationJpaRepository.findAllByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationJpaMapper::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByUserId(Long userId) {
        return notificationJpaRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public Notification save(Notification notification) {
        return NotificationJpaMapper.toDomain(
                notificationJpaRepository.save(NotificationJpaMapper.toJpa(notification))
        );
    }

    @Override
    public boolean read(Long notificationId, Long userId) {
        return notificationJpaRepository.read(notificationId, userId) == 1;
    }

    @Override
    public long readAll(Long userId) {
        return notificationJpaRepository.readAll(userId);
    }

    @Override
    public Optional<Notification> deleteById(Long id) {
        Optional<NotificationJpa> jpa = notificationJpaRepository.findById(id);

        if (jpa.isEmpty()) {
            return Optional.empty();
        }

        Notification domain = NotificationJpaMapper.toDomain(jpa.get());
        notificationJpaRepository.deleteById(id);

        return Optional.of(domain);
    }

    @Override
    public void deleteAll() {
        notificationJpaRepository.deleteAll();
    }
}

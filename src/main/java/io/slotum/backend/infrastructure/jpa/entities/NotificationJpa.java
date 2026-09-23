package io.slotum.backend.infrastructure.jpa.entities;

import io.slotum.backend.domain.notification.NotificationType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
      name = "notifications"
)
public class NotificationJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", length = 1024)
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /* Constructors */
    protected NotificationJpa() {}

    public NotificationJpa(
            Long id,
            Long userId,
            NotificationType type,
            String title,
            String message,
            boolean isRead,
            Instant createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean getIsRead() {
        return isRead;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

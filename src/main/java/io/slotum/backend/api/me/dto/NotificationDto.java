package io.slotum.backend.api.me.dto;

import io.slotum.backend.domain.notification.NotificationType;

import java.time.Instant;

/**
 * Данные системного уведомления
 *
 * @param id идентификатор уведомления
 * @param userId идентификатор пользователя, которому адресовано уведомление
 * @param type тип уведомления
 * @param title заголовок уведомления
 * @param message сообщение уведомления
 * @param isRead прочитано ли
 * @param createdAt время создания уведомления
 */
public record NotificationDto(
        Long id,
        Long userId,
        NotificationType type,
        String title,
        String message,
        boolean isRead,
        Instant createdAt
) {}
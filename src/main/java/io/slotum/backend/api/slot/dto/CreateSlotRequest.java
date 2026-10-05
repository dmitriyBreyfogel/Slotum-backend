package io.slotum.backend.api.slot.dto;

import io.slotum.backend.domain.slot.SlotStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * Данные запроса на создание слота.
 *
 * @param startsAt время начала слота
 * @param endsAt время окончания слота
 * @param status статус слота
 * @param specialistUserId идентификатор пользователя специалиста
 * @param customerId идентификатор записанного пользователя, может быть {@code null}
 * @param organizationId идентификатор организации
 */
public record CreateSlotRequest(
        @NotNull(message = "StartsAt is blank")
        LocalDateTime startsAt,

        @NotNull(message = "EndsAt is blank")
        LocalDateTime endsAt,

        @NotNull(message = "Status is blank")
        SlotStatus status,

        @NotNull(message = "SpecialistUserId is null")
        @Positive(message = "SpecialistUserId is not positive")
        Long specialistUserId,

        @Positive(message = "CustomerId is not positive")
        Long customerId,

        @NotNull(message = "OrganizationId is null")
        @Positive(message = "OrganizationId is not positive")
        Long organizationId
) {
}

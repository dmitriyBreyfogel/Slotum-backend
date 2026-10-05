package io.slotum.backend.api.me.dto;

/**
 * Данные членства текущего специалиста в организации.
 *
 * @param organizationId идентификатор организации
 * @param specialistUserId идентификатор пользователя специалиста
 */
public record OrganizationMemberDto(
        Long organizationId,
        Long specialistUserId
) {
}

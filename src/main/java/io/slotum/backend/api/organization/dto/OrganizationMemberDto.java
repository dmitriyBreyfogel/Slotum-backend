package io.slotum.backend.api.organization.dto;

/**
 * Данные членства специалиста в организации.
 *
 * @param organizationId идентификатор организации
 * @param specialistUserId идентификатор пользователя специалиста
 */
public record OrganizationMemberDto(
        Long organizationId,
        Long specialistUserId
) {
}

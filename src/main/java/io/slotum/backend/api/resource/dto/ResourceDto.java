package io.slotum.backend.api.resource.dto;

/**
 * Данные защищаемого ресурса.
 *
 * @param id идентификатор ресурса
 * @param httpMethod HTTP-метод ресурса
 * @param urlPattern URL-шаблон ресурса
 * @param description описание ресурса, может быть {@code null}
 */
public record ResourceDto(
        Long id,
        String httpMethod,
        String urlPattern,
        String description
) {
}

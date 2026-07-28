package io.slotum.backend.api.resource.dto;

/**
 * Данные запроса на создание защищаемого ресурса.
 *
 * @param httpMethod HTTP-метод ресурса
 * @param urlPattern URL-шаблон ресурса
 * @param description описание ресурса, может быть {@code null}
 */
public record CreateResourceRequest(
        String httpMethod,
        String urlPattern,
        String description
) {
}

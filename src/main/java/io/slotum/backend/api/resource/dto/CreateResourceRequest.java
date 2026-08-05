package io.slotum.backend.api.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Данные запроса на создание защищаемого ресурса.
 *
 * @param httpMethod HTTP-метод ресурса
 * @param urlPattern URL-шаблон ресурса
 * @param description описание ресурса, может быть {@code null}
 */
public record CreateResourceRequest(
        @NotBlank(message = "HttpMethod is blank")
        @Size(max = 10, message = "Too long httpMethod")
        String httpMethod,

        @NotBlank(message = "UrlPattern is blank")
        @Size(max = 255, message = "Too long urlPattern")
        String urlPattern,

        @Size(max = 255, message = "Too long description")
        String description
) {
}

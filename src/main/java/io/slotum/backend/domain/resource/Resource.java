package io.slotum.backend.domain.resource;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public final class Resource {
    private final Long id;
    private final String httpMethod;
    private final String urlPattern;
    private final String description;

    private Resource(Long id, String httpMethod, String urlPattern, String description) {
        this.id = id;
        this.httpMethod = httpMethod;
        this.urlPattern = urlPattern;
        this.description = description;
    }

    /**
     * Создание ресурса без описания
     * @param httpMethod метод запроса http
     * @param urlPattern url составляющая запроса
     * @return созданный ресурс с {@code null} описанием
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_RESOURCE_HTTP_METHOD} - метод запроса http пуст</li>
     *          <li>{@code TOO_LONG_RESOURCE_HTTP_METHOD} - метод запроса http больше 10 символов</li>
     *          <li>{@code EMPTY_RESOURCE_URL_PATTERN} - url составляющая запроса пуста</li>
     *          <li>{@code TOO_LONG_RESOURCE_URL_PATTERN} - url составляющая запроса больше 255 символов</li>
     *      </ul>
     */
    public static Resource create(String httpMethod, String urlPattern) {
        return restore(null, httpMethod, urlPattern, null);
    }

    /**
     * Создание ресурса
     * @param httpMethod метод запроса http
     * @param urlPattern url составляющая запроса
     * @param description описание запроса
     * @return созданный ресурс
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_RESOURCE_HTTP_METHOD} - метод запроса http пуст</li>
     *          <li>{@code TOO_LONG_RESOURCE_HTTP_METHOD} - метод запроса http больше 10 символов</li>
     *          <li>{@code EMPTY_RESOURCE_URL_PATTERN} - url составляющая запроса пуста</li>
     *          <li>{@code TOO_LONG_RESOURCE_URL_PATTERN} - url составляющая запроса больше 255 символов</li>
     *          <li>{@code TOO_LONG_RESOURCE_DESCRIPTION} - описание ресурса больше 255 символов</li>
     *      </ul>
     */
    public static Resource create(String httpMethod, String urlPattern, String description) {
        return restore(null, httpMethod, urlPattern, description);
    }

    /**
     * Создаёт объект ресурса с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что ресурс новый.
     * @param id идентификатор ресурса
     * @param httpMethod метод запроса http
     * @param urlPattern url составляющая запроса
     * @param description описание ресурса
     * @return созданный объект ресурса по заданным параметрам
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_RESOURCE_ID} - идентификатор ресурса не положительный</li>
     *          <li>{@code EMPTY_RESOURCE_HTTP_METHOD} - метод запроса http пуст</li>
     *          <li>{@code TOO_LONG_RESOURCE_HTTP_METHOD} - метод запроса http больше 10 символов</li>
     *          <li>{@code EMPTY_RESOURCE_URL_PATTERN} - url составляющая запроса пуста</li>
     *          <li>{@code TOO_LONG_RESOURCE_URL_PATTERN} - url составляющая запроса больше 255 символов</li>
     *          <li>{@code TOO_LONG_RESOURCE_DESCRIPTION} - описание ресурса больше 255 символов</li>
     *      </ul>
     */
    public static Resource restore(Long id, String httpMethod, String urlPattern, String description) {
        String normalizeHttpMethod = StringUtils.normalize(httpMethod);
        String normalizeUrlPattern = StringUtils.normalize(urlPattern);
        String normalizeDescription = StringUtils.normalize(description);

        validateId(id);
        validateHttpMethod(normalizeHttpMethod);
        validateUrlPattern(normalizeUrlPattern);
        validateDescription(normalizeDescription);

        return new Resource(id, normalizeHttpMethod, normalizeUrlPattern, normalizeDescription);
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public String getDescription() {
        return description;
    }

    /* Validation */
    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_RESOURCE_ID,
                    "Invalid resource id",
                    Map.of("id", id)
            );
        }
    }

    private static void validateHttpMethod(String httpMethod) {
        if (httpMethod == null) {
            throw AppException.build(
                    ErrorCode.EMPTY_RESOURCE_HTTP_METHOD,
                    "Empty resource http method"
            );
        }

        if (httpMethod.length() > 10) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_RESOURCE_HTTP_METHOD,
                    "Too long http method",
                    Map.of(
                            "Expected max length", 10,
                            "Actual length", httpMethod.length()
                    )
            );
        }
    }

    private static void validateUrlPattern(String urlPattern) {
        if (urlPattern == null) {
            throw AppException.build(
                    ErrorCode.EMPTY_RESOURCE_URL_PATTERN,
                    "Empty resource url pattern"
            );
        }

        if (urlPattern.length() > 255) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_RESOURCE_URL_PATTERN,
                    "Too long resource url pattern",
                    Map.of(
                            "Expected max length", 255,
                            "Actual length", urlPattern.length()
                    )
            );
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 255) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_RESOURCE_DESCRIPTION,
                    "Too long resource description",
                    Map.of(
                            "Expected max length", 255,
                            "Actual length", description.length()
                    )
            );
        }
    }
}

package io.slotum.backend.domain.resource;

import io.slotum.backend.domain.utils.StringUtils;

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
     */
    public static Resource restore(Long id, String httpMethod, String urlPattern, String description) {
        String normalizedHttpMethod = StringUtils.normalize(httpMethod);
        String normalizedUrlPattern = StringUtils.normalize(urlPattern);
        String normalizedDescription = StringUtils.normalize(description);

        return new Resource(id, normalizedHttpMethod, normalizedUrlPattern, normalizedDescription);
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
}

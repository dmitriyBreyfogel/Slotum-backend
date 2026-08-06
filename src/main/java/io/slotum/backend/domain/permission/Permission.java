package io.slotum.backend.domain.permission;

import io.slotum.backend.domain.utils.StringUtils;

public final class Permission {
    private final Long id;
    private final String code;
    private final String description;

    private Permission(Long id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    /**
     * Создание разрешения без описания
     * @param code код разрешения
     * @return созданное разрешение с {@code null} описанием
     */
    public static Permission create(String code) {
        return restore(null, code, null);
    }

    /**
     * Создание разрешения
     * @param code код разрешения
     * @param description описание разрешения
     * @return созданное разрешение
     */
    public static Permission create(String code, String description) {
        return restore(null, code, description);
    }

    /**
     * Создаёт объект разрешения с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что разрешение новое.
     * @param id идентификатор разрешения
     * @param code код разрешения
     * @param description описание разрешения (может быть {@code null})
     * @return созданный объект разрешения по заданным параметрам
     */
    public static Permission restore(Long id, String code, String description) {
        String normalizedCode = StringUtils.normalize(code);
        String normalizedDescription = StringUtils.normalize(description);

        return new Permission(id, normalizedCode, normalizedDescription);
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

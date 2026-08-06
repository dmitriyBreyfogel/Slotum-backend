package io.slotum.backend.domain.role;

import io.slotum.backend.domain.utils.StringUtils;

public final class Role {
    private final Long id;
    private final RoleNames name;
    private final String description;

    private Role(Long id, RoleNames name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Создание роли без описания
     * @param name название роли
     * @return созданная роль с {@code null} описанием
     */
    public static Role create(RoleNames name) {
        return restore(null, name, null);
    }

    /**
     * Создание роли
     * @param name название роли
     * @param description описание роли
     * @return созданная роль
     */
    public static Role create(RoleNames name, String description) {
        return restore(null, name, description);
    }

    /**
     * Создаёт объект роли с явно указанным идентификатором.
     * Используется, когда идентификатор известен заранее (например, при маппинге из БД).
     * В отличие от {@link #create}, не предполагает, что роль новая.
     * @param id идентификатор роли
     * @param name название роли
     * @param description описание роли
     * @return созданный объект роли по заданным параметрам
     */
    public static Role restore(Long id, RoleNames name, String description) {
        String normalizedDescription = StringUtils.normalize(description);

        return new Role(id, name, normalizedDescription);
    }

    /* Getters */
    public Long getId() {
        return id;
    }

    public RoleNames getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

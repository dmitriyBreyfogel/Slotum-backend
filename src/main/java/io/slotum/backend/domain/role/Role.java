package io.slotum.backend.domain.role;

import io.slotum.backend.domain.utils.StringUtils;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

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
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_ROLE_NAME} - название роли пустое</li>
     *      </ul>
     */
    public static Role create(RoleNames name) {
        return restore(null, name, null);
    }

    /**
     * Создание роли
     * @param name название роли
     * @param description описание роли
     * @return созданная роль
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code EMPTY_ROLE_NAME} - название роли пустое</li>
     *          <li>{@code TOO_LONG_ROLE_DESCRIPTION} - описание роли более 255 символов</li>
     *      </ul>
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
     * @throws AppException с кодом:
     *      <ul>
     *          <li>{@code INVALID_ROLE_ID} - идентификатор роли не положительный</li>
     *          <li>{@code EMPTY_ROLE_NAME} - название роли пустое</li>
     *          <li>{@code TOO_LONG_ROLE_DESCRIPTION} - описание роли более 255 символов</li>
     *      </ul>
     */
    public static Role restore(Long id, RoleNames name, String description) {
        String normalizeDescription = StringUtils.normalize(description);

        validateId(id);
        validateName(name);
        validateDescription(normalizeDescription);

        return new Role(id, name, normalizeDescription);
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

    /* Validation */
    private static void validateId(Long id) {
        if (id != null && id <= 0) {
            throw AppException.build(
                    ErrorCode.INVALID_ROLE_ID,
                    "Invalid role id",
                    Map.of( "id", id )
            );
        }
    }

    private static void validateName(RoleNames name) {
        if (name == null) {
            throw AppException.build(
                    ErrorCode.EMPTY_ROLE_NAME,
                    "Empty role name"
            );
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > 255) {
            throw AppException.build(
                    ErrorCode.TOO_LONG_ROLE_DESCRIPTION,
                    "Too long role description",
                    Map.of(
                            "Expected max length", 255,
                            "Actual length", description.length()
                    )
            );
        }
    }
}

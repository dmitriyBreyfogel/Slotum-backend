package io.slotum.backend.domain.permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    /**
     * Поиск разрешения по идентификатору
     * @param id идентификатор разрешения
     * @return объект разрешения, если найти удалось.
     * {@code Optional#empty} если найти не удалось
     */
    Optional<Permission> findById(Long id);

    /**
     * Поиск разрешения по коду
     * @param code код разрешения
     * @return объект разрешения, если найти удалось. {@code Optional#empty} если найти не удалось
     */
    Optional<Permission> findByCode(String code);

    /**
     * Сохранение разрешения в базу данных
     * @param permission объект сохраняемого разрешения. Обязан быть не {@code null}
     * @return сохранённый объект разрешения. Никогда не будет {@code null}
     */
    Permission save(Permission permission);

    /**
     * Получение всех имеющихся разрешений
     * @return список всех найденных разрешений, если разрешений нет - вернёт пустой список.
     * Никогда не {@code null}
     */
    List<Permission> findAll();

    /**
     * Удаление разрешения по идентификатору
     * @param id идентификатор разрешения. Обязан быть не {@code null}
     * @return удалённое разрешение. Если запись отсутствует в бд, то метод вернёт {@code null}
     */
    Permission deleteById(Long id);

    /**
     * Удаление всех имеющихся разрешений.
     * Если разрешений нет, метод ничего не делает
     */
    void deleteAll();
}

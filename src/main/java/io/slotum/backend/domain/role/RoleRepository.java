package io.slotum.backend.domain.role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    /**
     * Поиск роли по идентификатору
     * @param id идентификатор роли. Обязан быть не {@code null}
     * @return объект найденной роли, если найти удалось. {@code Optional.empty()}, если найти не удалось
     */
    Optional<Role> findById(Long id);

    /**
     * Поиск роли по её имени
     * @param name имя роли. Обязано быть не {@code null}
     * @return объект найденной роли, если найти удалось. {@code Optional.empty()}, если найти не удалось
     */
    Optional<Role> findByName(RoleNames name);

    /**
     * Получение всех имеющихся ролей
     * @return список имеющихся ролей. Если роли отсутствуют - метод вернёт пустой список
     */
    List<Role> findAll();

    /**
     * Сохранение роли в базу данных
     * @param role объект сохраняемой роли. Обязан быть не {@code null}
     * @return сохранённый объект роли. Никогда не {@code null}
     */
    Role save(Role role);

    /**
     * Удаление роли по её идентификатору
     * @param id идентификатор удаляемой роли. Обязан быть не {@code null}
     * @return объект удалённой роли. Если роль по идентификатору найти не удалось - метод вернёт {@code Optional.empty()}
     */
    Optional<Role> deleteById(Long id);

    /**
     * Удаление всех имеющихся ролей.
     * Если роли отсутствуют - метод ничего не делает
     */
    void deleteAll();
}

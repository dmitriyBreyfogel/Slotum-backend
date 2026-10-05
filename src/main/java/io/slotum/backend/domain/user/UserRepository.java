package io.slotum.backend.domain.user;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.role.RoleNames;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository {

    /**
     * Поиск пользователя по идентификатору
     * @param id идентификатор пользователя. Обязан быть не {@code null}
     * @return объект найденного пользователя, если найти удалось. {@code Optional.empty()},
     * если найти не удалось
     */
    Optional<User> findById(Long id);

    /**
     * Поиск пользователя по электронной почте
     * @param email электронная почта пользователя. Обязана быть не {@code null}
     * @return объект найденного пользователя, если найти удалось. {@code Optional.empty()},
     * если найти не удалось
     */
    Optional<User> findByEmail(String email);

    /**
     * Поиск ролей пользователя по его идентификатору
     * @param id идентификатор пользователя. Обязан быть не {@code null}
     * @return множество ролей пользователя, если найти удалось. Иначе пустое множество
     */
    Set<RoleNames> findRolesById(Long id);

    /**
     * Поиск разрешений пользователя по его идентификатору
     * @param id идентификатор пользователя. Обязан быть не {@code null}
     * @return список разрешений пользователя, если найти удалось. Иначе пустой список
     */
    Set<Permission> findPermissionsById(Long id);

    /**
     * Проверка существования пользователя с указанной электронной почтой
     * @param email электронная почта пользователя. Обязана быть не {@code null}
     * @return {@code true}, если пользователь существует. Иначе {@code false}
     */
    boolean existsByEmail(String email);

    /**
     * Сохранение пользователя в базу данных
     * @param user объект сохраняемого пользователя. Обязан быть не {@code null}
     * @return сохранённый объект пользователя. Никогда не {@code null}
     */
    User save(User user);

    /**
     * Получение всех имеющихся пользователей
     * @return список имеющихся пользователей. Если пользователи отсутствуют - метод вернёт
     * пустой список
     */
    List<User> findAll();

    /**
     * Удаление пользователя по его идентификатору
     * @param id идентификатор удаляемого пользователя. Обязан быть не {@code null}
     * @return объект удалённого пользователя. Если пользователя по идентификатору найти
     * не удалось - метод вернёт {@code Optional.empty()}
     */
    Optional<User> deleteById(Long id);

    /**
     * Удаление всех имеющихся пользователей.
     * Если пользователи отсутствуют - метод ничего не делает
     */
    void deleteAll();
}

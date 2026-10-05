package io.slotum.backend.domain.organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {

    /**
     * Поиск организации по идентификатору
     * @param id идентификатор организации. Обязан быть не {@code null}
     * @return объект найденной организации, если найти удалось. {@code Optional.empty()},
     * если найти не удалось
     */
    Optional<Organization> findById(Long id);

    /**
     * Поиск организации по её названию
     * @param name название организации. Обязано быть не {@code null}
     * @return объект найденной организации, если найти удалось. {@code Optional.empty()},
     * если найти не удалось
     */
    Optional<Organization> findByName(String name);

    /**
     * Сохранение организации в базу данных
     * @param organization объект сохраняемой организации. Обязан быть не {@code null}
     * @return сохранённый объект организации. Никогда не {@code null}
     */
    Organization save(Organization organization);

    /**
     * Получение всех имеющихся организаций
     * @return список имеющихся организаций. Если организации отсутствуют - метод вернёт
     * пустой список
     */
    List<Organization> findAll();

    /**
     * Удаление организации по её идентификатору
     * @param id идентификатор удаляемой организации. Обязан быть не {@code null}
     * @return объект удалённой организации. Если организацию по идентификатору найти
     * не удалось - метод вернёт {@code Optional.empty()}
     */
    Optional<Organization> deleteById(Long id);

    /**
     * Удаление всех имеющихся организаций.
     * Если организации отсутствуют - метод ничего не делает
     */
    void deleteAll();
}

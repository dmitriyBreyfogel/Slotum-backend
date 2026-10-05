package io.slotum.backend.domain.specialist;

import java.util.List;
import java.util.Optional;

public interface SpecialistRepository {

    /**
     * Поиск специалиста по идентификатору
     * @param id идентификатор специалиста. Обязан быть не {@code null}
     * @return объект найденного специалиста, если найти удалось. {@code Optional.empty()},
     * если найти не удалось
     */
    Optional<Specialist> findById(Long id);

    /**
     * Поиск специалиста по идентификатору пользователя
     * @param userId идентификатор пользователя. Обязан быть не {@code null}
     * @return объект найденного специалиста, если найти удалось. {@code Optional.empty()},
     * если найти не удалось
     */
    Optional<Specialist> findSpecialistByUserId(Long userId);

    /**
     * Сохранение специалиста в базу данных
     * @param specialist объект сохраняемого специалиста. Обязан быть не {@code null}
     * @return сохранённый объект специалиста. Никогда не {@code null}
     */
    Specialist save(Specialist specialist);

    /**
     * Получение всех имеющихся специалистов
     * @return список имеющихся специалистов. Если специалисты отсутствуют - метод вернёт
     * пустой список
     */
    List<Specialist> findAll();

    /**
     * Удаление специалиста по его идентификатору
     * @param id идентификатор удаляемого специалиста. Обязан быть не {@code null}
     * @return объект удалённого специалиста. Если специалиста по идентификатору найти
     * не удалось - метод вернёт {@code Optional.empty()}
     */
    Optional<Specialist> deleteById(Long id);

    /**
     * Удаление всех имеющихся специалистов.
     * Если специалисты отсутствуют - метод ничего не делает
     */
    void deleteAll();
}

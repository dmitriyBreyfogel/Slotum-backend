package io.slotum.backend.domain.resource;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {

    /**
     * Поиск ресурса по идентификатору
     * @param id идентификатор ресурса. Обязан быть не {@code null}
     * @return объект ресурса, если найден. {@code Optional.empty()}, если не найден
     */
    Optional<Resource> findById(Long id);

    /**
     * Получение всех имеющихся ресурсов
     * @return список имеющихся ресурсов. Если они отсутствуют - вернётся пустой список.
     * Никогда не {@code null}
     */
    List<Resource> findAll();

    /**
     * Сохранение ресурса в базу данных
     * @param resource объект сохраняемого ресурса. Обязан быть не {@code null}
     * @return сохранённый объект ресурса. Никогда не будет {@code null}
     */
    Resource save(Resource resource);

    /**
     * Удаление ресурса по идентификатору
     * @param id идентификатор ресурса. Обязан быть не {@code null}
     * @return объект удалённого ресурса. Если ресурс по идентификатору найти не удалось -
     * метод вернёт {@code Optional.empty()}
     */
    Optional<Resource> deleteById(Long id);

    /**
     * Удаление всех имеющихся ресурсов.
     * Если ресурсы отсутствуют - метод ничего не делает
     */
    void deleteAll();
}

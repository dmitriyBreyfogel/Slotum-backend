package io.slotum.backend.domain.resource;

import io.slotum.backend.domain.permission.Permission;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {

    /**
     * Поиск ресурса по идентификатору
     * @param id идентификатор разрешения. Обязан быть не {@code null}
     * @return объект ресурса, если найден. {@code Optional#empty} если не найден
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
     * @return удалённый объект ресурса. Если объект не найден - вернётся {@code null}
     */
    Resource deleteById(Long id);

    /**
     * Удаление всех имеющихся ресурсов.
     * Если ресурсы отсутствуют - метод ничего не делает
     */
    void deleteAll();
}

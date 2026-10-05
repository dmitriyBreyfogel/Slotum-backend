package io.slotum.backend.domain.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.specialist.Specialist;

import java.util.List;
import java.util.Optional;

public interface OrganizationMemberRepository {

    /**
     * Проверка существования связи организации и специалиста
     * @param organizationId идентификатор организации. Обязан быть не {@code null}
     * @param specialistUserId идентификатор пользователя-специалиста. Обязан быть не {@code null}
     * @return {@code true}, если связь существует. Иначе {@code false}
     */
    boolean exists(Long organizationId, Long specialistUserId);

    /**
     * Сохранение связи организации и специалиста в базу данных
     * @param organizationId идентификатор организации. Обязан быть не {@code null}
     * @param specialistUserId идентификатор пользователя-специалиста. Обязан быть не {@code null}
     * @return сохранённый объект связи организации и специалиста. Никогда не {@code null}
     */
    OrganizationMember save(Long organizationId, Long specialistUserId);

    /**
     * Удаление связи организации и специалиста
     * @param organizationId идентификатор организации. Обязан быть не {@code null}
     * @param specialistUserId идентификатор пользователя-специалиста. Обязан быть не {@code null}
     * @return объект удалённой связи. Если связь найти не удалось - метод вернёт
     * {@code Optional.empty()}
     */
    Optional<OrganizationMember> delete(Long organizationId, Long specialistUserId);

    /**
     * Получение всех специалистов организации
     * @param organizationId идентификатор организации. Обязан быть не {@code null}
     * @return список специалистов организации. Если специалисты отсутствуют - метод вернёт
     * пустой список
     */
    List<Specialist> findSpecialistsByOrganizationId(Long organizationId);

    /**
     * Получение всех организаций специалиста
     * @param specialistUserId идентификатор пользователя-специалиста. Обязан быть не {@code null}
     * @return список организаций специалиста. Если организации отсутствуют - метод вернёт
     * пустой список
     */
    List<Organization> findOrganizationsBySpecialistUserId(Long specialistUserId);
}

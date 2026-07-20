package io.slotum.backend.infrastructure.jpa.repositories.organizationMember;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organizationMember.OrganizationMember;
import io.slotum.backend.domain.organizationMember.OrganizationMemberRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationMemberJpa;
import io.slotum.backend.infrastructure.jpa.mappers.OrganizationJpaMapper;
import io.slotum.backend.infrastructure.jpa.mappers.OrganizationMemberJpaMapper;
import io.slotum.backend.infrastructure.jpa.mappers.SpecialistJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OrganizationMemberRepositoryJpaAdapter implements OrganizationMemberRepository {
    private final OrganizationMemberJpaRepository organizationMemberJpaRepository;

    public OrganizationMemberRepositoryJpaAdapter(OrganizationMemberJpaRepository organizationMemberJpaRepository) {
        this.organizationMemberJpaRepository = organizationMemberJpaRepository;
    }

    @Override
    public boolean exists(Long organizationId, Long specialistUserId) {
        return organizationMemberJpaRepository.existsByIdOrganizationIdAndIdSpecialistId(organizationId, specialistUserId);
    }

    @Override
    public OrganizationMember save(Long organizationId, Long specialistUserId) {
        return OrganizationMemberJpaMapper.toDomain(
                organizationMemberJpaRepository.save(new OrganizationMemberJpa(organizationId, specialistUserId))
        );
    }

    @Override
    public OrganizationMember delete(Long organizationId, Long specialistUserId) {
        OrganizationMemberJpa.OrganizationMemberId id =
                new OrganizationMemberJpa.OrganizationMemberId(organizationId, specialistUserId);

        Optional<OrganizationMemberJpa> deleted = organizationMemberJpaRepository.findById(id);

        if (deleted.isPresent()) {
            organizationMemberJpaRepository.deleteById(id);
            return OrganizationMemberJpaMapper.toDomain(deleted.get());
        }

        return null;
    }

    @Override
    public List<Specialist> findSpecialistsByOrganizationId(Long organizationId) {
        List<OrganizationMemberJpa> members = organizationMemberJpaRepository.findAllByIdOrganizationId(organizationId);

        return members.stream()
                .map(OrganizationMemberJpa::getSpecialist)
                .map(SpecialistJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<Organization> findOrganizationsBySpecialistUserId(Long specialistUserId) {
        List<OrganizationMemberJpa> members = organizationMemberJpaRepository.findAllByIdSpecialistId(specialistUserId);

        return members.stream()
                .map(OrganizationMemberJpa::getOrganization)
                .map(OrganizationJpaMapper::toDomain)
                .toList();
    }
}

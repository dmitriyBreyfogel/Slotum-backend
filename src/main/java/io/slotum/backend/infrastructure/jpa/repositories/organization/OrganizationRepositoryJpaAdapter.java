package io.slotum.backend.infrastructure.jpa.repositories.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.infrastructure.jpa.mappers.OrganizationJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrganizationRepositoryJpaAdapter implements OrganizationRepository {
    private final OrganizationJpaRepository organizationJpaRepository;

    public OrganizationRepositoryJpaAdapter(OrganizationJpaRepository organizationJpaRepository) {
        this.organizationJpaRepository = organizationJpaRepository;
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return organizationJpaRepository.findById(id).map(OrganizationJpaMapper::toDomain);
    }

    @Override
    public Optional<Organization> findByName(String name) {
        return organizationJpaRepository.findByName(name).map(OrganizationJpaMapper::toDomain);
    }

    @Override
    public Organization save(Organization organization) {
        return OrganizationJpaMapper.toDomain(
                organizationJpaRepository.save(OrganizationJpaMapper.toJpa(organization))
        );
    }
}

package io.slotum.backend.infrastructure.jpa.repositories.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.infrastructure.jpa.mappers.OrganizationJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<Organization> findAll() {
        return organizationJpaRepository.findAll().stream().map(OrganizationJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Organization save(Organization organization) {
        return OrganizationJpaMapper.toDomain(
                organizationJpaRepository.save(OrganizationJpaMapper.toJpa(organization))
        );
    }
}

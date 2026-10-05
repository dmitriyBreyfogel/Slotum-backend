package io.slotum.backend.infrastructure.jpa.repositories.organization;

import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.mappers.OrganizationJpaMapper;
import jakarta.transaction.Transactional;
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

    @Override
    @Transactional
    public Optional<Organization> deleteById(Long id) {
        Optional<OrganizationJpa> organizationJpa = organizationJpaRepository.findById(id);

        if (organizationJpa.isEmpty()) {
            return Optional.empty();
        }

        Organization organization = OrganizationJpaMapper.toDomain(organizationJpa.get());
        organizationJpaRepository.delete(organizationJpa.get());

        return Optional.of(organization);
    }

    @Override
    public void deleteAll() {
        organizationJpaRepository.deleteAll();
    }
}

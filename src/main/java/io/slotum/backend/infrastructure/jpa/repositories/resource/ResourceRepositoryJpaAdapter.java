package io.slotum.backend.infrastructure.jpa.repositories.resource;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.resource.Resource;
import io.slotum.backend.domain.resource.ResourceRepository;
import io.slotum.backend.infrastructure.jpa.entities.ResourceJpa;
import io.slotum.backend.infrastructure.jpa.mappers.ResourceJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResourceRepositoryJpaAdapter implements ResourceRepository {
    private final ResourceJpaRepository resourceJpaRepository;

    public ResourceRepositoryJpaAdapter(ResourceJpaRepository resourceJpaRepository) {
        this.resourceJpaRepository = resourceJpaRepository;
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return resourceJpaRepository.findById(id).map(ResourceJpaMapper::toDomain);
    }

    @Override
    public Resource save(Resource resource) {
        return ResourceJpaMapper.toDomain(
                resourceJpaRepository.save(ResourceJpaMapper.toJpa(resource))
        );
    }

    @Override
    public List<Resource> findAll() {
        return resourceJpaRepository.findAll().stream().map(ResourceJpaMapper::toDomain).toList();
    }

    @Override
    public Resource deleteById(Long id) {
        Optional<ResourceJpa> deleted = resourceJpaRepository.findById(id);

        if (deleted.isPresent()) {
            resourceJpaRepository.deleteById(id);
            return ResourceJpaMapper.toDomain(deleted.get());
        }

        return null;
    }

    @Override
    public void deleteAll() {
        resourceJpaRepository.deleteAll();
    }
}

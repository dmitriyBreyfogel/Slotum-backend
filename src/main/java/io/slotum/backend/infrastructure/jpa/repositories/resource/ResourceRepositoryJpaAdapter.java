package io.slotum.backend.infrastructure.jpa.repositories.resource;

import io.slotum.backend.domain.resource.Resource;
import io.slotum.backend.domain.resource.ResourceRepository;
import io.slotum.backend.infrastructure.jpa.entities.ResourceJpa;
import io.slotum.backend.infrastructure.jpa.mappers.ResourceJpaMapper;
import jakarta.transaction.Transactional;
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
    @Transactional
    public Optional<Resource> deleteById(Long id) {
        Optional<ResourceJpa> resourceJpa = resourceJpaRepository.findById(id);

        if (resourceJpa.isEmpty()) {
            return Optional.empty();
        }

        Resource resource = ResourceJpaMapper.toDomain(resourceJpa.get());
        resourceJpaRepository.delete(resourceJpa.get());

        return Optional.of(resource);
    }

    @Override
    public void deleteAll() {
        resourceJpaRepository.deleteAll();
    }
}

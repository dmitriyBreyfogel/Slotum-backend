package io.slotum.backend.infrastructure.jpa.repositories.permission;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.permission.PermissionRepository;
import io.slotum.backend.infrastructure.jpa.entities.PermissionJpa;
import io.slotum.backend.infrastructure.jpa.mappers.PermissionJpaMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PermissionRepositoryJpaAdapter implements PermissionRepository {
    private final PermissionJpaRepository permissionJpaRepository;

    public PermissionRepositoryJpaAdapter(PermissionJpaRepository permissionJpaRepository) {
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionJpaRepository.findById(id).map(PermissionJpaMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return permissionJpaRepository.findByCode(code).map(PermissionJpaMapper::toDomain);
    }

    @Override
    public Permission save(Permission permission) {
        return PermissionJpaMapper.toDomain(
                permissionJpaRepository.save(PermissionJpaMapper.toJpa(permission))
        );
    }

    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAll().stream().map(PermissionJpaMapper::toDomain).toList();
    }

    @Override
    @Transactional
    public Optional<Permission> deleteById(Long id) {
        Optional<PermissionJpa> permissionJpa = permissionJpaRepository.findById(id);

        if (permissionJpa.isEmpty()) {
            return Optional.empty();
        }

        Permission permission = PermissionJpaMapper.toDomain(permissionJpa.get());
        permissionJpaRepository.delete(permissionJpa.get());

        return Optional.of(permission);
    }

    @Override
    public void deleteAll() {
        permissionJpaRepository.deleteAll();
    }
}

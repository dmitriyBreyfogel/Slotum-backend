package io.slotum.backend.infrastructure.jpa.repositories.role;

import io.slotum.backend.domain.role.Role;
import io.slotum.backend.domain.role.RoleNames;
import io.slotum.backend.domain.role.RoleRepository;
import io.slotum.backend.infrastructure.jpa.entities.RoleJpa;
import io.slotum.backend.infrastructure.jpa.mappers.RoleJpaMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepositoryJpaAdapter implements RoleRepository {
    private final RoleJpaRepository roleJpaRepository;

    public RoleRepositoryJpaAdapter(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id).map(RoleJpaMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(RoleNames name) {
        return roleJpaRepository.findByName(name).map(RoleJpaMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream().map(RoleJpaMapper::toDomain).toList();
    }

    @Override
    public Role save(Role role) {
        return RoleJpaMapper.toDomain(
                roleJpaRepository.save(RoleJpaMapper.toJpa(role))
        );
    }

    @Override
    @Transactional
    public Optional<Role> deleteById(Long id) {
        Optional<RoleJpa> roleJpa = roleJpaRepository.findById(id);

        if (roleJpa.isEmpty()) {
            return Optional.empty();
        }

        Role role = RoleJpaMapper.toDomain(roleJpa.get());
        roleJpaRepository.delete(roleJpa.get());

        return Optional.of(role);
    }

    @Override
    public void deleteAll() {
        roleJpaRepository.deleteAll();
    }
}

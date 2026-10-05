package io.slotum.backend.infrastructure.jpa.repositories.user;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.role.RoleNames;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.infrastructure.jpa.entities.PermissionJpa;
import io.slotum.backend.infrastructure.jpa.entities.RoleJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import io.slotum.backend.infrastructure.jpa.mappers.PermissionJpaMapper;
import io.slotum.backend.infrastructure.jpa.mappers.UserJpaMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryJpaAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    public UserRepositoryJpaAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserJpaMapper::toDomain);
    }

    @Override
    public Set<RoleNames> findRolesById(Long id) {
        Optional<UserJpa> jpa = userJpaRepository.findById(id);

        if (jpa.isEmpty()) {
            return Collections.emptySet();
        }

        Set<RoleNames> roles = new HashSet<>();
        for(RoleJpa role : jpa.get().getRoles()) {
            roles.add(role.getName());
        }

        return roles;
    }

    @Override
    public Set<Permission> findPermissionsById(Long id) {
        Optional<UserJpa> jpa = userJpaRepository.findById(id);
        if (jpa.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Permission> permissions = new HashSet<>();
        for (RoleJpa role : jpa.get().getRoles()) {
            for (PermissionJpa permissionJpa : role.getPermissions()) {
                permissions.add(PermissionJpaMapper.toDomain(permissionJpa));
            }
        }
        return permissions;
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream().map(UserJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        return UserJpaMapper.toDomain(
                userJpaRepository.save(UserJpaMapper.toJpa(user))
        );
    }

    @Override
    @Transactional
    public Optional<User> deleteById(Long id) {
        Optional<UserJpa> userJpa = userJpaRepository.findById(id);

        if (userJpa.isEmpty()) {
            return Optional.empty();
        }

        User user = UserJpaMapper.toDomain(userJpa.get());
        userJpaRepository.delete(userJpa.get());

        return Optional.of(user);
    }

    @Override
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }
}

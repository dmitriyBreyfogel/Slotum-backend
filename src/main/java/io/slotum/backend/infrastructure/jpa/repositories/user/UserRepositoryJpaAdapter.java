package io.slotum.backend.infrastructure.jpa.repositories.user;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.infrastructure.jpa.mappers.SpecialistJpaMapper;
import io.slotum.backend.infrastructure.jpa.mappers.UserJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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
}

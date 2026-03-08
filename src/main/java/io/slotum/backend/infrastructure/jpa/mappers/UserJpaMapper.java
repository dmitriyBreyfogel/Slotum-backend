package io.slotum.backend.infrastructure.jpa.mappers;

import io.slotum.backend.domain.user.User;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;

public final class UserJpaMapper {
    private UserJpaMapper() {
    }

    public static User toDomain(UserJpa source) {
        if (source == null) {
            throw new IllegalArgumentException("UserJpa source is null");
        }

        return User.restore(
                source.getId(),
                source.getSurname(),
                source.getFirstName(),
                source.getSecondName(),
                source.getEmail(),
                source.getPassword(),
                source.getPhone()
        );
    }

    public static UserJpa toJpa(User source) {
        if (source == null) {
            throw new IllegalArgumentException("User source is null");
        }

        return new UserJpa(
                source.getId(),
                source.getSurname(),
                source.getFirstName(),
                source.getSecondName(),
                source.getEmail().value(),
                source.getPassword().value(),
                source.getPhone().value()
        );
    }
}

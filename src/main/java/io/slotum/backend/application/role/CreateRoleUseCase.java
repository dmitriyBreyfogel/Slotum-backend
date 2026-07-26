package io.slotum.backend.application.role;

import io.slotum.backend.domain.role.Role;
import io.slotum.backend.domain.role.RoleNames;
import io.slotum.backend.domain.role.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateRoleUseCase {
    private final RoleRepository roleRepository;

    public CreateRoleUseCase(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role execute(Command command) {
        Role role = Role.create(command.name(), command.description());
        return roleRepository.save(role);
    }

    public record Command(
            RoleNames name,
            String description
    ) {}
}

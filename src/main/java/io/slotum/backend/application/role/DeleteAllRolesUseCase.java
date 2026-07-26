package io.slotum.backend.application.role;

import io.slotum.backend.domain.role.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllRolesUseCase {
    private final RoleRepository roleRepository;

    public DeleteAllRolesUseCase(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void execute() {
        roleRepository.deleteAll();
    }
}

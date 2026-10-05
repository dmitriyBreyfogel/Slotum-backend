package io.slotum.backend.application.usecase.role;

import io.slotum.backend.domain.role.Role;
import io.slotum.backend.domain.role.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRolesUseCase {
    private final RoleRepository roleRepository;

    public GetAllRolesUseCase(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> execute() {
        return roleRepository.findAll();
    }
}

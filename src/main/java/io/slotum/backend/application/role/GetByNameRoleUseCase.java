package io.slotum.backend.application.role;

import io.slotum.backend.domain.role.Role;
import io.slotum.backend.domain.role.RoleNames;
import io.slotum.backend.domain.role.RoleRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetByNameRoleUseCase {
    private final RoleRepository roleRepository;

    public GetByNameRoleUseCase(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role execute(RoleNames name) {
        Optional<Role> role = roleRepository.findByName(name);

        if (role.isEmpty()) {
            throw AppException.build(
                    ErrorCode.ROLE_NOT_FOUND,
                    "Role not found",
                    Map.of("name", name)
            );
        }

        return role.get();
    }
}

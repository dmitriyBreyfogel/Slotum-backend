package io.slotum.backend.application.role;

import io.slotum.backend.domain.role.Role;
import io.slotum.backend.domain.role.RoleRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetRoleUseCase {
    private final RoleRepository roleRepository;

    public GetRoleUseCase(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role execute(Long id) {
        Optional<Role> role = roleRepository.findById(id);

        if (role.isEmpty()) {
            throw AppException.build(
                    ErrorCode.ROLE_NOT_FOUND,
                    "Role not found",
                    Map.of("id", id)
            );
        }

        return role.get();
    }
}

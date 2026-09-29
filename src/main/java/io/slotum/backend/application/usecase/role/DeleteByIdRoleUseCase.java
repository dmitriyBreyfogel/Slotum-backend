package io.slotum.backend.application.usecase.role;

import io.slotum.backend.domain.role.Role;
import io.slotum.backend.domain.role.RoleRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class DeleteByIdRoleUseCase {
    private final RoleRepository roleRepository;

    public DeleteByIdRoleUseCase(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role execute(Long id) {
        Optional<Role> role = roleRepository.deleteById(id);

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

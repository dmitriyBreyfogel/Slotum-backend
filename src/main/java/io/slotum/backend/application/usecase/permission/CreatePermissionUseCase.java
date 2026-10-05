package io.slotum.backend.application.usecase.permission;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.permission.PermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreatePermissionUseCase {
    private final PermissionRepository permissionRepository;

    public CreatePermissionUseCase(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission execute(Command command) {
        Permission permission = Permission.create(command.code(), command.description());
        return permissionRepository.save(permission);
    }

    public record Command(
            String code,
            String description
    ) {}
}

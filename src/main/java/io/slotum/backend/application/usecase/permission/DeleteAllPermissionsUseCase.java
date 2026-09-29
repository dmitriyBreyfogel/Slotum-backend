package io.slotum.backend.application.usecase.permission;

import io.slotum.backend.domain.permission.PermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteAllPermissionsUseCase {
    private final PermissionRepository permissionRepository;

    public DeleteAllPermissionsUseCase(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public void execute() {
        permissionRepository.deleteAll();
    }
}

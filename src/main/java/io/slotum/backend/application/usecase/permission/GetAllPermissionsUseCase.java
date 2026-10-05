package io.slotum.backend.application.usecase.permission;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.permission.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllPermissionsUseCase {
    private final PermissionRepository permissionRepository;

    public GetAllPermissionsUseCase(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<Permission> execute() {
        return permissionRepository.findAll();
    }
}

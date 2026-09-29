package io.slotum.backend.application.usecase.permission;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.permission.PermissionRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetPermissionUseCase {
    private final PermissionRepository permissionRepository;

    public GetPermissionUseCase(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission execute(Long permissionId) {
        Optional<Permission> permission = permissionRepository.findById(permissionId);

        if (permission.isEmpty()) {
            throw AppException.build(
                    ErrorCode.PERMISSION_NOT_FOUND,
                    "Permission not found",
                    Map.of("id", permissionId)
            );
        }

        return permission.get();
    }
}

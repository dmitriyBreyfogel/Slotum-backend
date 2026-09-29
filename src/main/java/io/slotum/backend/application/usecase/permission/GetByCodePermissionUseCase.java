package io.slotum.backend.application.usecase.permission;

import io.slotum.backend.domain.permission.Permission;
import io.slotum.backend.domain.permission.PermissionRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class GetByCodePermissionUseCase {
    private final PermissionRepository permissionRepository;

    public GetByCodePermissionUseCase(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission execute(String code) {
        Optional<Permission> permission = permissionRepository.findByCode(code);

        if (permission.isEmpty()) {
            throw AppException.build(
                    ErrorCode.PERMISSION_NOT_FOUND,
                    "Permission not found",
                    Map.of("code", code)
            );
        }

        return permission.get();
    }
}

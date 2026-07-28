package io.slotum.backend.api.permission;

import io.slotum.backend.api.permission.dto.CreatePermissionRequest;
import io.slotum.backend.api.permission.dto.PermissionDto;
import io.slotum.backend.application.permission.CreatePermissionUseCase;
import io.slotum.backend.application.permission.DeleteAllPermissionsUseCase;
import io.slotum.backend.application.permission.DeleteByIdPermissionUseCase;
import io.slotum.backend.application.permission.GetAllPermissionsUseCase;
import io.slotum.backend.application.permission.GetByCodePermissionUseCase;
import io.slotum.backend.application.permission.GetPermissionUseCase;
import io.slotum.backend.domain.permission.Permission;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PermissionController implements PermissionApi {
    private final CreatePermissionUseCase createPermissionUseCase;
    private final GetPermissionUseCase getPermissionUseCase;
    private final GetByCodePermissionUseCase getByCodePermissionUseCase;
    private final GetAllPermissionsUseCase getAllPermissionsUseCase;
    private final DeleteByIdPermissionUseCase deleteByIdPermissionUseCase;
    private final DeleteAllPermissionsUseCase deleteAllPermissionsUseCase;

    public PermissionController(
            CreatePermissionUseCase createPermissionUseCase,
            GetPermissionUseCase getPermissionUseCase,
            GetByCodePermissionUseCase getByCodePermissionUseCase,
            GetAllPermissionsUseCase getAllPermissionsUseCase,
            DeleteByIdPermissionUseCase deleteByIdPermissionUseCase,
            DeleteAllPermissionsUseCase deleteAllPermissionsUseCase
    ) {
        this.createPermissionUseCase = createPermissionUseCase;
        this.getPermissionUseCase = getPermissionUseCase;
        this.getByCodePermissionUseCase = getByCodePermissionUseCase;
        this.getAllPermissionsUseCase = getAllPermissionsUseCase;
        this.deleteByIdPermissionUseCase = deleteByIdPermissionUseCase;
        this.deleteAllPermissionsUseCase = deleteAllPermissionsUseCase;
    }

    @Override
    public ResponseEntity<PermissionDto> create(CreatePermissionRequest request) {
        Permission result = createPermissionUseCase.execute(
                new CreatePermissionUseCase.Command(
                        request.code(),
                        request.description()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(result));
    }

    @Override
    public ResponseEntity<PermissionDto> getPermission(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getPermissionUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<PermissionDto> getByCode(String code) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getByCodePermissionUseCase.execute(code)));
    }

    @Override
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        return ResponseEntity.status(HttpStatus.OK).body(
                getAllPermissionsUseCase.execute().stream()
                        .map(PermissionController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<PermissionDto> deletePermission(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(deleteByIdPermissionUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<Void> deleteAllPermissions() {
        deleteAllPermissionsUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private static PermissionDto toDto(Permission source) {
        return new PermissionDto(
                source.getId(),
                source.getCode(),
                source.getDescription()
        );
    }
}

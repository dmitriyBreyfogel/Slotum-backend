package io.slotum.backend.api.role;

import io.slotum.backend.api.role.dto.CreateRoleRequest;
import io.slotum.backend.api.role.dto.RoleDto;
import io.slotum.backend.application.role.CreateRoleUseCase;
import io.slotum.backend.application.role.DeleteAllRolesUseCase;
import io.slotum.backend.application.role.DeleteByIdRoleUseCase;
import io.slotum.backend.application.role.GetAllRolesUseCase;
import io.slotum.backend.application.role.GetByNameRoleUseCase;
import io.slotum.backend.application.role.GetRoleUseCase;
import io.slotum.backend.domain.role.Role;
import io.slotum.backend.domain.role.RoleNames;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
public class RoleController implements RoleApi {
    private final CreateRoleUseCase createRoleUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final GetByNameRoleUseCase getByNameRoleUseCase;
    private final GetAllRolesUseCase getAllRolesUseCase;
    private final DeleteByIdRoleUseCase deleteByIdRoleUseCase;
    private final DeleteAllRolesUseCase deleteAllRolesUseCase;

    public RoleController(
            CreateRoleUseCase createRoleUseCase,
            GetRoleUseCase getRoleUseCase,
            GetByNameRoleUseCase getByNameRoleUseCase,
            GetAllRolesUseCase getAllRolesUseCase,
            DeleteByIdRoleUseCase deleteByIdRoleUseCase,
            DeleteAllRolesUseCase deleteAllRolesUseCase
    ) {
        this.createRoleUseCase = createRoleUseCase;
        this.getRoleUseCase = getRoleUseCase;
        this.getByNameRoleUseCase = getByNameRoleUseCase;
        this.getAllRolesUseCase = getAllRolesUseCase;
        this.deleteByIdRoleUseCase = deleteByIdRoleUseCase;
        this.deleteAllRolesUseCase = deleteAllRolesUseCase;
    }

    @Override
    public ResponseEntity<RoleDto> create(CreateRoleRequest request) {
        Role result = createRoleUseCase.execute(
                new CreateRoleUseCase.Command(
                        request.name(),
                        request.description()
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(result));
    }

    @Override
    public ResponseEntity<RoleDto> getRole(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getRoleUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<RoleDto> getByName(RoleNames name) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(getByNameRoleUseCase.execute(name)));
    }

    @Override
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(
                getAllRolesUseCase.execute().stream()
                        .map(RoleController::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<RoleDto> deleteRole(Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(toDto(deleteByIdRoleUseCase.execute(id)));
    }

    @Override
    public ResponseEntity<Void> deleteAllRoles() {
        deleteAllRolesUseCase.execute();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    private static RoleDto toDto(Role source) {
        return new RoleDto(
                source.getId(),
                source.getName(),
                source.getDescription()
        );
    }
}

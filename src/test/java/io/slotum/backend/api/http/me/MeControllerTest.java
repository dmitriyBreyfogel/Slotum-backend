package io.slotum.backend.api.http.me;

import io.slotum.backend.application.organization.CreateMyOrganizationUseCase;
import io.slotum.backend.application.organizationMember.GetSpecialistOrganizationsUseCase;
import io.slotum.backend.application.organizationMember.RemoveSpecialistFromOrganizationUseCase;
import io.slotum.backend.application.specialist.CreateSpecialistUseCase;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class MeControllerTest {

    @Test
    @DisplayName("Создание моей организации берет specialistUserId из текущего пользователя")
    void createMyOrganizationUsesCurrentUserId() {
        CreateSpecialistUseCase createSpecialistUseCase = mock(CreateSpecialistUseCase.class);
        CreateMyOrganizationUseCase createMyOrganizationUseCase = mock(CreateMyOrganizationUseCase.class);
        GetSpecialistOrganizationsUseCase getSpecialistOrganizationsUseCase = mock(GetSpecialistOrganizationsUseCase.class);
        RemoveSpecialistFromOrganizationUseCase removeSpecialistFromOrganizationUseCase =
                mock(RemoveSpecialistFromOrganizationUseCase.class);
        MeController controller = new MeController(
                createSpecialistUseCase,
                createMyOrganizationUseCase,
                getSpecialistOrganizationsUseCase,
                removeSpecialistFromOrganizationUseCase
        );

        when(createMyOrganizationUseCase.execute(any()))
                .thenReturn(Organization.create(3L, "Org", "Desc"));

        ResponseEntity<MeController.OrganizationDto> response = controller.createMyOrganization(
                new AuthenticatedUser(10L, "spec@example.com"),
                new MeController.CreateOrganizationRequest("Org", "Desc")
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(3L, response.getBody().id());
        assertEquals("Org", response.getBody().name());
        assertEquals("Desc", response.getBody().description());
        assertEquals(0.0, response.getBody().grade());

        ArgumentCaptor<CreateMyOrganizationUseCase.Command> commandCaptor =
                ArgumentCaptor.forClass(CreateMyOrganizationUseCase.Command.class);
        verify(createMyOrganizationUseCase).execute(commandCaptor.capture());
        CreateMyOrganizationUseCase.Command command = commandCaptor.getValue();
        assertEquals(10L, command.specialistUserId());
        assertEquals("Org", command.name());
        assertEquals("Desc", command.description());
        verifyNoMoreInteractions(createMyOrganizationUseCase);
    }
}

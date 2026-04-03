package io.slotum.backend.api.http.organization;

import io.slotum.backend.api.http.GlobalExceptionHandler;
import io.slotum.backend.application.organization.CreateOrganizationUseCase;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrganizationController.class)
@Import(GlobalExceptionHandler.class)
public class OrganizationControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    CreateOrganizationUseCase createOrganizationUseCase;

    @Test
    @DisplayName("Валидное создание организации и возврат CREATED 201")
    void testCreateOrganization() throws Exception {
        when(createOrganizationUseCase.execute(any()))
                .thenReturn(new CreateOrganizationUseCase.Result(1L, "Acme", "Some description", 0.0));

        var req = Map.of(
                "name", "Acme",
                "description", "Some description"
        );

        mvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/organizations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Acme"));

        ArgumentCaptor<CreateOrganizationUseCase.Command> commandCaptor =
                ArgumentCaptor.forClass(CreateOrganizationUseCase.Command.class);
        verify(createOrganizationUseCase).execute(commandCaptor.capture());
        CreateOrganizationUseCase.Command command = commandCaptor.getValue();
        assertEquals("Acme", command.name());
        assertEquals("Some description", command.description());
    }

    @Test
    @DisplayName("Создание организации, уже имеющейся в бд. Возврат CONFLICT 409")
    void testCreateOrganizationConflict() throws Exception {
        when(createOrganizationUseCase.execute(any())).thenThrow(AppException.build(
                ErrorCode.ORGANIZATION_ALREADY_EXISTS,
                "exist",
                Map.of("name", "Acme")));

        var req = Map.of(
                "name", "Acme",
                "description", "Some description"
        );

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ORGANIZATION_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Создание невалидной организации. Возврат BAD_REQUEST 400")
    void testCreateOrganizationBadRequest() throws Exception {
        when(createOrganizationUseCase.execute(any())).thenThrow(AppException.build(
                ErrorCode.EMPTY_ORGANIZATION_NAME,
                "empty name",
                Map.of("name", "")
        ));

        var req = Map.of(
                "name", "",
                "description", "Some description"
        );

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("EMPTY_ORGANIZATION_NAME"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Неожиданная ошибка в use case. Возврат INTERNAL_SERVER_ERROR 500")
    void testCreateOrganizationInternalServerError() throws Exception {
        when(createOrganizationUseCase.execute(any())).thenThrow(new RuntimeException("boom"));

        var req = Map.of(
                "name", "Acme",
                "description", "Some description"
        );

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @DisplayName("Невалидный JSON в запросе. Возврат BAD_REQUEST 400")
    void testCreateOrganizationMalformedJson() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());
    }
}


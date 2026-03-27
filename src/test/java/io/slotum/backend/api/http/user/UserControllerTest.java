package io.slotum.backend.api.http.user;

import io.slotum.backend.api.http.GlobalExceptionHandler;
import io.slotum.backend.application.user.CreateUserUseCase;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("Валидное создание через ручку и возврат CREATED 201")
    void testCreateUser() throws Exception {
        when(createUserUseCase.execute(any())).thenReturn(new CreateUserUseCase.Result(1L, "slotum@io.com"));

        var req = Map.of(
                "surname", "Ivanov",
                "firstName", "Ivan",
                "secondName", "Ivanovich",
                "email", "slotum@io.com",
                "password", "Password123!",
                "phone", "88005553535"
        );

        mvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.email").value("slotum@io.com"));

        ArgumentCaptor<CreateUserUseCase.Command> commandCaptor =
                ArgumentCaptor.forClass(CreateUserUseCase.Command.class);
        verify(createUserUseCase).execute(commandCaptor.capture());
        CreateUserUseCase.Command command = commandCaptor.getValue();
        assertEquals("Ivanov", command.surname());
        assertEquals("Ivan", command.firstName());
        assertEquals("Ivanovich", command.secondName());
        assertEquals("slotum@io.com", command.email());
        assertEquals("Password123!", command.password());
        assertEquals("88005553535", command.phone());
    }

    @Test
    @DisplayName("Создание пользователя, уже имеющегося в бд. Возврат CONFLICT 409")
    void testCreateUserConflict() throws Exception {
        when(createUserUseCase.execute(any())).thenThrow(AppException.build(
                ErrorCode.USER_EMAIL_ALREADY_EXISTS,
                "exist",
                Map.of("email", "slotum@io.com")));

        var req = Map.of(
                "surname", "Ivanov",
                "firstName", "Ivan",
                "secondName", "Ivanovich",
                "email", "slotum@io.com",
                "password", "Password123!",
                "phone", "88005553535"
        );

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("USER_EMAIL_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Создание невалидного пользователя. Возврат BAD_REQUEST 400")
    void testCreateUserBadRequest() throws Exception {
        when(createUserUseCase.execute(any())).thenThrow(AppException.build(
                ErrorCode.INVALID_USER_EMAIL,
                "invalid email",
                Map.of("email", "slotumio.com")
        ));

        var req = Map.of(
                "surname", "Ivanov",
                "firstName", "Ivan",
                "secondName", "Ivanovich",
                "email", "slotumio.com",
                "password", "Password123!",
                "phone", "88005553535"
        );

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_USER_EMAIL"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Неожиданная ошибка в use case. Возврат INTERNAL_SERVER_ERROR 500")
    void testCreateUserInternalServerError() throws Exception {
        when(createUserUseCase.execute(any())).thenThrow(new RuntimeException("boom"));

        var req = Map.of(
                "surname", "Ivanov",
                "firstName", "Ivan",
                "secondName", "Ivanovich",
                "email", "slotum@io.com",
                "password", "Password123!",
                "phone", "88005553535"
        );

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @DisplayName("Невалидный JSON в запросе. Возврат BAD_REQUEST 400")
    void testCreateUserMalformedJson() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());
    }
}

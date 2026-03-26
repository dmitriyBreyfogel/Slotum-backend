package io.slotum.backend.application.appointment;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CreateAppointmentUseCaseTest {

    @Test
    @DisplayName("Не обращается к репозиторию, если startsAt = null")
    void rejectsNullStartsAtBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                null,
                LocalDateTime.of(2026, 3, 21, 11, 0),
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_STARTS_AT, ex.getCode());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если endsAt = null")
    void rejectsNullEndsAtBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                null,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ENDS_AT, ex.getCode());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если endsAt не позже startsAt (валидация диапазона времени)")
    void rejectsInvalidTimeRangeBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                startsAt,
                endsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_TIME_RANGE, ex.getCode());
        assertEquals(Map.of("startsAt", startsAt, "endsAt", endsAt), ex.getDetails());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если status = null")
    void rejectsNullStatusBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                null,
                10L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_STATUS, ex.getCode());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если specialistUserId = null")
    void rejectsNullSpecialistUserIdBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                AppointmentStatus.BOOKED,
                null,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_SPECIALIST_ID, ex.getCode());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если specialistUserId <= 0")
    void rejectsNonPositiveSpecialistUserIdBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                AppointmentStatus.BOOKED,
                0L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_SPECIALIST_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("specialistUserId"));
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если customerId = null")
    void rejectsNullCustomerIdBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                AppointmentStatus.BOOKED,
                10L,
                null,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_CUSTOMER_ID, ex.getCode());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если customerId <= 0")
    void rejectsNonPositiveCustomerIdBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                AppointmentStatus.BOOKED,
                10L,
                -1L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_CUSTOMER_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("customerId"));
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если organizationId = null")
    void rejectsNullOrganizationIdBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                AppointmentStatus.BOOKED,
                10L,
                20L,
                null
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ORGANIZATION_ID, ex.getCode());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если organizationId <= 0")
    void rejectsNonPositiveOrganizationIdBeforeRepository() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                AppointmentStatus.BOOKED,
                10L,
                20L,
                0L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_APPOINTMENT_ORGANIZATION_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("organizationId"));
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Сохраняет новый appointment и возвращает данные из сохранённой сущности")
    void savesNewAppointmentAndReturnsData() {
        AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
        CreateAppointmentUseCase useCase = new CreateAppointmentUseCase(appointmentRepository);

        LocalDateTime commandStartsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime commandEndsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        LocalDateTime savedStartsAt = LocalDateTime.of(2026, 3, 22, 12, 0);
        LocalDateTime savedEndsAt = LocalDateTime.of(2026, 3, 22, 13, 0);

        when(appointmentRepository.save(any())).thenReturn(
                Appointment.restore(
                        1L,
                        savedStartsAt,
                        savedEndsAt,
                        AppointmentStatus.CANCELLED,
                        11L,
                        21L,
                        31L
                )
        );

        CreateAppointmentUseCase.Command command = new CreateAppointmentUseCase.Command(
                commandStartsAt,
                commandEndsAt,
                AppointmentStatus.BOOKED,
                10L,
                20L,
                30L
        );

        CreateAppointmentUseCase.Result result = useCase.execute(command);

        assertEquals(savedStartsAt, result.startsAt());
        assertEquals(savedEndsAt, result.endsAt());
        assertEquals(AppointmentStatus.CANCELLED, result.status());
        assertEquals(11L, result.specialistUserId());
        assertEquals(21L, result.customerId());
        assertEquals(31L, result.organizationId());

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentRepository).save(captor.capture());

        assertNull(captor.getValue().getId());
        assertEquals(commandStartsAt, captor.getValue().getStartsAt());
        assertEquals(commandEndsAt, captor.getValue().getEndsAt());
        assertEquals(AppointmentStatus.BOOKED, captor.getValue().getStatus());
        assertEquals(10L, captor.getValue().getSpecialistUserId());
        assertEquals(20L, captor.getValue().getCustomerId());
        assertEquals(30L, captor.getValue().getOrganizationId());

        verifyNoMoreInteractions(appointmentRepository);
    }
}


package io.slotum.backend.application.slot;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.organization.Organization;
import io.slotum.backend.domain.organization.OrganizationRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class CreateSlotUseCaseTest {

    @Test
    @DisplayName("Не обращается к репозиторию, если startsAt = null")
    void rejectsNullStartsAtBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                null,
                LocalDateTime.of(2026, 3, 21, 11, 0),
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_STARTS_AT, ex.getCode());
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если endsAt = null")
    void rejectsNullEndsAtBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                null,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_ENDS_AT, ex.getCode());
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если endsAt не позже startsAt (валидация диапазона времени)")
    void rejectsInvalidTimeRangeBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 10, 0);

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                startsAt,
                endsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_TIME_RANGE, ex.getCode());
        assertEquals(Map.of("startsAt", startsAt, "endsAt", endsAt), ex.getDetails());
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если status = null")
    void rejectsNullStatusBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                null,
                10L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_STATUS, ex.getCode());
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если specialistUserId = null")
    void rejectsNullSpecialistUserIdBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                SlotStatus.BOOKED,
                null,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_SPECIALIST_ID, ex.getCode());
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если specialistUserId <= 0")
    void rejectsNonPositiveSpecialistUserIdBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                SlotStatus.BOOKED,
                0L,
                20L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_SPECIALIST_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("specialistUserId"));
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если customerId = null")
    void rejectsNullCustomerIdBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                SlotStatus.BOOKED,
                10L,
                null,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_CUSTOMER_ID, ex.getCode());
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если customerId <= 0")
    void rejectsNonPositiveCustomerIdBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                SlotStatus.BOOKED,
                10L,
                -1L,
                30L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_CUSTOMER_ID, ex.getCode());
        assertEquals(-1L, ex.getDetails().get("customerId"));
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если organizationId = null")
    void rejectsNullOrganizationIdBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                SlotStatus.BOOKED,
                10L,
                20L,
                null
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_ORGANIZATION_ID, ex.getCode());
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Не обращается к репозиторию, если organizationId <= 0")
    void rejectsNonPositiveOrganizationIdBeforeRepository() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                LocalDateTime.of(2026, 3, 21, 10, 0),
                LocalDateTime.of(2026, 3, 21, 11, 0),
                SlotStatus.BOOKED,
                10L,
                20L,
                0L
        );

        AppException ex = assertThrows(AppException.class, () -> useCase.execute(command));

        assertEquals(ErrorCode.INVALID_SLOT_ORGANIZATION_ID, ex.getCode());
        assertEquals(0L, ex.getDetails().get("organizationId"));
        verifyNoInteractions(slotRepository);
    }

    @Test
    @DisplayName("Creates free slot without customer")
    void savesFreeSlotWithoutCustomer() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        LocalDateTime startsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime endsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.of(mock(Specialist.class)));
        when(organizationRepository.findById(30L)).thenReturn(Optional.of(mock(Organization.class)));
        when(slotRepository.existsOverlappingSlot(10L, startsAt, endsAt)).thenReturn(false);
        when(slotRepository.save(any())).thenReturn(
                Slot.restore(
                        1L,
                        startsAt,
                        endsAt,
                        SlotStatus.FREE,
                        10L,
                        null,
                        30L
                )
        );

        Slot result = useCase.execute(
                new CreateSlotUseCase.Command(
                        startsAt,
                        endsAt,
                        SlotStatus.FREE,
                        10L,
                        null,
                        30L
                )
        );

        assertEquals(1L, result.getId());
        assertEquals(SlotStatus.FREE, result.getStatus());
        assertNull(result.getCustomerId());
        verify(specialistRepository).findSpecialistByUserId(10L);
        verify(organizationRepository).findById(30L);
        verify(slotRepository).existsOverlappingSlot(10L, startsAt, endsAt);
        verify(slotRepository).save(any());
        verifyNoInteractions(userRepository);
        verifyNoMoreInteractions(slotRepository, specialistRepository, organizationRepository);
    }

    @Test
    @DisplayName("РЎРѕС…СЂР°РЅСЏРµС‚ РЅРѕРІС‹Р№ slot Рё РІРѕР·РІСЂР°С‰Р°РµС‚ РґР°РЅРЅС‹Рµ РёР· СЃРѕС…СЂР°РЅС‘РЅРЅРѕР№ СЃСѓС‰РЅРѕСЃС‚Рё")
    void savesNewSlotAndReturnsData() {
        SlotRepository slotRepository = mock(SlotRepository.class);
        SpecialistRepository specialistRepository = mock(SpecialistRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        OrganizationRepository organizationRepository = mock(OrganizationRepository.class);
        CreateSlotUseCase useCase = new CreateSlotUseCase(
                slotRepository,
                specialistRepository,
                userRepository,
                organizationRepository
        );

        LocalDateTime commandStartsAt = LocalDateTime.of(2026, 3, 21, 10, 0);
        LocalDateTime commandEndsAt = LocalDateTime.of(2026, 3, 21, 11, 0);

        LocalDateTime savedStartsAt = LocalDateTime.of(2026, 3, 22, 12, 0);
        LocalDateTime savedEndsAt = LocalDateTime.of(2026, 3, 22, 13, 0);

        when(slotRepository.save(any())).thenReturn(
                Slot.restore(
                        1L,
                        savedStartsAt,
                        savedEndsAt,
                        SlotStatus.CANCELLED,
                        11L,
                        21L,
                        31L
                )
        );
        when(specialistRepository.findSpecialistByUserId(10L)).thenReturn(Optional.of(mock(Specialist.class)));
        when(userRepository.findById(20L)).thenReturn(Optional.of(mock(User.class)));
        when(organizationRepository.findById(30L)).thenReturn(Optional.of(mock(Organization.class)));
        when(slotRepository.existsOverlappingSlot(10L, commandStartsAt, commandEndsAt)).thenReturn(false);

        CreateSlotUseCase.Command command = new CreateSlotUseCase.Command(
                commandStartsAt,
                commandEndsAt,
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        );

        Slot result = useCase.execute(command);

        assertEquals(savedStartsAt, result.getStartsAt());
        assertEquals(savedEndsAt, result.getEndsAt());
        assertEquals(SlotStatus.CANCELLED, result.getStatus());
        assertEquals(11L, result.getSpecialistUserId());
        assertEquals(21L, result.getCustomerId());
        assertEquals(31L, result.getOrganizationId());

        ArgumentCaptor<Slot> captor = ArgumentCaptor.forClass(Slot.class);
        verify(slotRepository).existsOverlappingSlot(10L, commandStartsAt, commandEndsAt);
        verify(slotRepository).save(captor.capture());

        assertNull(captor.getValue().getId());
        assertEquals(commandStartsAt, captor.getValue().getStartsAt());
        assertEquals(commandEndsAt, captor.getValue().getEndsAt());
        assertEquals(SlotStatus.BOOKED, captor.getValue().getStatus());
        assertEquals(10L, captor.getValue().getSpecialistUserId());
        assertEquals(20L, captor.getValue().getCustomerId());
        assertEquals(30L, captor.getValue().getOrganizationId());

        verifyNoMoreInteractions(slotRepository);
    }
}

package io.slotum.backend.infrastructure.jpa.repositories.slot;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class SlotRepositoryJpaAdapterTest {

    @Test
    @DisplayName("save maps overlap exclusion constraint violation to domain error")
    void saveMapsOverlapConstraintViolation() {
        SlotJpaRepository jpaRepository = mock(SlotJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotRepositoryJpaAdapter adapter = new SlotRepositoryJpaAdapter(jpaRepository, entityManager);
        Slot slot = freeSlot();
        SpecialistJpa specialist = new SpecialistJpa(10L, "description", 4.5);
        OrganizationJpa organization = new OrganizationJpa(30L, "Org", "Description", 0.0);
        DataIntegrityViolationException violation = new DataIntegrityViolationException(
                "conflicting key value violates exclusion constraint \"ex_slots_no_overlap\""
        );

        when(entityManager.getReference(SpecialistJpa.class, 10L)).thenReturn(specialist);
        when(entityManager.getReference(OrganizationJpa.class, 30L)).thenReturn(organization);
        when(jpaRepository.save(any())).thenThrow(violation);

        AppException ex = assertThrows(AppException.class, () -> adapter.save(slot));

        assertEquals(ErrorCode.SLOT_OVERLAPPING, ex.getCode());
        assertEquals(10L, ex.getDetails().get("specialistUserId"));
        assertEquals(slot.getStartsAt(), ex.getDetails().get("startsAt"));
        assertEquals(slot.getEndsAt(), ex.getDetails().get("endsAt"));
        verify(entityManager).getReference(SpecialistJpa.class, 10L);
        verify(entityManager).getReference(OrganizationJpa.class, 30L);
        verify(jpaRepository).save(any());
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("save maps overlap exclusion constraint violation for booked slot")
    void saveMapsOverlapConstraintViolationForBookedSlot() {
        SlotJpaRepository jpaRepository = mock(SlotJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotRepositoryJpaAdapter adapter = new SlotRepositoryJpaAdapter(jpaRepository, entityManager);
        Slot slot = bookedSlot();
        SpecialistJpa specialist = new SpecialistJpa(10L, "description", 4.5);
        UserJpa customer = new UserJpa(20L, "Doe", "John", null, "john@test.com", "HASH", "+79991234567");
        OrganizationJpa organization = new OrganizationJpa(30L, "Org", "Description", 0.0);
        DataIntegrityViolationException violation = new DataIntegrityViolationException(
                "conflicting key value violates exclusion constraint \"ex_slots_no_overlap\""
        );

        when(entityManager.getReference(SpecialistJpa.class, 10L)).thenReturn(specialist);
        when(entityManager.getReference(UserJpa.class, 20L)).thenReturn(customer);
        when(entityManager.getReference(OrganizationJpa.class, 30L)).thenReturn(organization);
        when(jpaRepository.save(any())).thenThrow(violation);

        AppException ex = assertThrows(AppException.class, () -> adapter.save(slot));

        assertEquals(ErrorCode.SLOT_OVERLAPPING, ex.getCode());
        assertEquals(10L, ex.getDetails().get("specialistUserId"));
        verify(entityManager).getReference(SpecialistJpa.class, 10L);
        verify(entityManager).getReference(UserJpa.class, 20L);
        verify(entityManager).getReference(OrganizationJpa.class, 30L);
        verify(jpaRepository).save(any());
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    private static Slot freeSlot() {
        return Slot.restore(
                null,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                SlotStatus.FREE,
                10L,
                null,
                30L
        );
    }

    private static Slot bookedSlot() {
        return Slot.restore(
                null,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                SlotStatus.BOOKED,
                10L,
                20L,
                30L
        );
    }
}

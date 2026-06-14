package io.slotum.backend.infrastructure.jpa.repositories.slotBookingRequest;

import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.SlotBookingRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class SlotBookingRequestRepositoryJpaAdapterTest {

    @Test
    @DisplayName("findById delegates to JpaRepository and maps domain")
    void findByIdDelegatesAndMapsDomain() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(pendingJpa(1L)));

        Optional<SlotBookingRequest> result = adapter.findById(1L);

        assertEquals(true, result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(5L, result.get().getSlotId());
        verify(jpaRepository).findById(1L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findAll maps all requests")
    void findAllMapsRequests() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAll()).thenReturn(List.of(pendingJpa(1L)));

        List<SlotBookingRequest> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(jpaRepository).findAll();
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findBySlotId delegates to nested slot id query")
    void findBySlotIdDelegates() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAllBySlot_Id(5L)).thenReturn(List.of(pendingJpa(1L)));

        List<SlotBookingRequest> result = adapter.findBySlotId(5L);

        assertEquals(1, result.size());
        verify(jpaRepository).findAllBySlot_Id(5L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findPendingBySlotId delegates with PENDING status")
    void findPendingBySlotIdDelegates() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAllBySlot_IdAndStatus(5L, SlotBookingRequestStatus.PENDING))
                .thenReturn(List.of(pendingJpa(1L)));

        List<SlotBookingRequest> result = adapter.findPendingBySlotId(5L);

        assertEquals(1, result.size());
        verify(jpaRepository).findAllBySlot_IdAndStatus(5L, SlotBookingRequestStatus.PENDING);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findByCustomerId delegates to nested customer id query")
    void findByCustomerIdDelegates() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAllByCustomer_Id(20L)).thenReturn(List.of(pendingJpa(1L)));

        List<SlotBookingRequest> result = adapter.findByCustomerId(20L);

        assertEquals(1, result.size());
        verify(jpaRepository).findAllByCustomer_Id(20L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findBySpecialistUserId delegates to nested slot specialist query")
    void findBySpecialistUserIdDelegates() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAllBySlot_Specialist_UserId(10L)).thenReturn(List.of(pendingJpa(1L)));

        List<SlotBookingRequest> result = adapter.findBySpecialistUserId(10L);

        assertEquals(1, result.size());
        verify(jpaRepository).findAllBySlot_Specialist_UserId(10L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("existsPendingBySlotIdAndCustomerId delegates with PENDING status")
    void existsPendingDelegates() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.existsBySlot_IdAndCustomer_IdAndStatus(
                5L,
                20L,
                SlotBookingRequestStatus.PENDING
        )).thenReturn(true);

        boolean result = adapter.existsPendingBySlotIdAndCustomerId(5L, 20L);

        assertEquals(true, result);
        verify(jpaRepository).existsBySlot_IdAndCustomer_IdAndStatus(
                5L,
                20L,
                SlotBookingRequestStatus.PENDING
        );
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("save persists Jpa entity and returns domain")
    void savePersistsJpaAndReturnsDomain() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        SlotJpa slot = slotJpa();
        UserJpa customer = userJpa();
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        SlotBookingRequest request = SlotBookingRequest.create(5L, 20L, "message", createdAt);

        when(entityManager.getReference(SlotJpa.class, 5L)).thenReturn(slot);
        when(entityManager.getReference(UserJpa.class, 20L)).thenReturn(customer);
        when(jpaRepository.save(any())).thenReturn(new SlotBookingRequestJpa(
                1L,
                slot,
                customer,
                SlotBookingRequestStatus.PENDING,
                "message",
                createdAt,
                null
        ));

        SlotBookingRequest result = adapter.save(request);

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getSlotId());
        assertEquals(20L, result.getCustomerId());

        ArgumentCaptor<SlotBookingRequestJpa> captor = ArgumentCaptor.forClass(SlotBookingRequestJpa.class);
        verify(jpaRepository).save(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(SlotBookingRequestStatus.PENDING, captor.getValue().getStatus());
        verify(entityManager).getReference(SlotJpa.class, 5L);
        verify(entityManager).getReference(UserJpa.class, 20L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("save maps pending duplicate unique violation to domain error")
    void saveMapsPendingDuplicateUniqueViolation() {
        SlotBookingRequestJpaRepository jpaRepository = mock(SlotBookingRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        SlotBookingRequestRepositoryJpaAdapter adapter =
                new SlotBookingRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        SlotJpa slot = slotJpa();
        UserJpa customer = userJpa();
        SlotBookingRequest request = SlotBookingRequest.create(
                5L,
                20L,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0)
        );
        DataIntegrityViolationException violation = new DataIntegrityViolationException(
                "duplicate key value violates unique constraint \"uq_slot_booking_requests_pending_slot_customer\""
        );

        when(entityManager.getReference(SlotJpa.class, 5L)).thenReturn(slot);
        when(entityManager.getReference(UserJpa.class, 20L)).thenReturn(customer);
        when(jpaRepository.save(any())).thenThrow(violation);

        AppException ex = assertThrows(AppException.class, () -> adapter.save(request));

        assertEquals(ErrorCode.SLOT_BOOKING_REQUEST_ALREADY_EXISTS, ex.getCode());
        assertEquals(5L, ex.getDetails().get("slotId"));
        assertEquals(20L, ex.getDetails().get("customerId"));
        verify(entityManager).getReference(SlotJpa.class, 5L);
        verify(entityManager).getReference(UserJpa.class, 20L);
        verify(jpaRepository).save(any());
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    private static SlotBookingRequestJpa pendingJpa(Long id) {
        return new SlotBookingRequestJpa(
                id,
                slotJpa(),
                userJpa(),
                SlotBookingRequestStatus.PENDING,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        );
    }

    private static SlotJpa slotJpa() {
        return new SlotJpa(
                5L,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                SlotStatus.FREE,
                new SpecialistJpa(10L, "description", 4.5),
                null,
                new OrganizationJpa(30L, "Org", "Description", 0.0)
        );
    }

    private static UserJpa userJpa() {
        return new UserJpa(
                20L,
                "Doe",
                "John",
                null,
                "john@test.com",
                "HASH",
                "+79991234567"
        );
    }
}

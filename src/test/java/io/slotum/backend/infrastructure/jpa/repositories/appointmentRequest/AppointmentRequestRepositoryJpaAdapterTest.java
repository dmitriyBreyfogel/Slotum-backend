package io.slotum.backend.infrastructure.jpa.repositories.appointmentRequest;

import io.slotum.backend.domain.appointment.AppointmentStatus;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentJpa;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AppointmentRequestRepositoryJpaAdapterTest {

    @Test
    @DisplayName("findById delegates to JpaRepository and maps domain")
    void findByIdDelegatesAndMapsDomain() {
        AppointmentRequestJpaRepository jpaRepository = mock(AppointmentRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        AppointmentRequestRepositoryJpaAdapter adapter =
                new AppointmentRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findById(1L)).thenReturn(Optional.of(pendingJpa(1L)));

        Optional<AppointmentRequest> result = adapter.findById(1L);

        assertEquals(true, result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(5L, result.get().getAppointmentId());
        verify(jpaRepository).findById(1L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findAll maps all requests")
    void findAllMapsRequests() {
        AppointmentRequestJpaRepository jpaRepository = mock(AppointmentRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        AppointmentRequestRepositoryJpaAdapter adapter =
                new AppointmentRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAll()).thenReturn(List.of(pendingJpa(1L)));

        List<AppointmentRequest> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(jpaRepository).findAll();
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findByAppointmentId delegates to nested appointment id query")
    void findByAppointmentIdDelegates() {
        AppointmentRequestJpaRepository jpaRepository = mock(AppointmentRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        AppointmentRequestRepositoryJpaAdapter adapter =
                new AppointmentRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAllByAppointment_Id(5L)).thenReturn(List.of(pendingJpa(1L)));

        List<AppointmentRequest> result = adapter.findByAppointmentId(5L);

        assertEquals(1, result.size());
        verify(jpaRepository).findAllByAppointment_Id(5L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findPendingByAppointmentId delegates with PENDING status")
    void findPendingByAppointmentIdDelegates() {
        AppointmentRequestJpaRepository jpaRepository = mock(AppointmentRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        AppointmentRequestRepositoryJpaAdapter adapter =
                new AppointmentRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAllByAppointment_IdAndStatus(5L, AppointmentRequestStatus.PENDING))
                .thenReturn(List.of(pendingJpa(1L)));

        List<AppointmentRequest> result = adapter.findPendingByAppointmentId(5L);

        assertEquals(1, result.size());
        verify(jpaRepository).findAllByAppointment_IdAndStatus(5L, AppointmentRequestStatus.PENDING);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findByCustomerId delegates to nested customer id query")
    void findByCustomerIdDelegates() {
        AppointmentRequestJpaRepository jpaRepository = mock(AppointmentRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        AppointmentRequestRepositoryJpaAdapter adapter =
                new AppointmentRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAllByCustomer_Id(20L)).thenReturn(List.of(pendingJpa(1L)));

        List<AppointmentRequest> result = adapter.findByCustomerId(20L);

        assertEquals(1, result.size());
        verify(jpaRepository).findAllByCustomer_Id(20L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("findBySpecialistUserId delegates to nested appointment specialist query")
    void findBySpecialistUserIdDelegates() {
        AppointmentRequestJpaRepository jpaRepository = mock(AppointmentRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        AppointmentRequestRepositoryJpaAdapter adapter =
                new AppointmentRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.findAllByAppointment_Specialist_UserId(10L)).thenReturn(List.of(pendingJpa(1L)));

        List<AppointmentRequest> result = adapter.findBySpecialistUserId(10L);

        assertEquals(1, result.size());
        verify(jpaRepository).findAllByAppointment_Specialist_UserId(10L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("existsPendingByAppointmentIdAndCustomerId delegates with PENDING status")
    void existsPendingDelegates() {
        AppointmentRequestJpaRepository jpaRepository = mock(AppointmentRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        AppointmentRequestRepositoryJpaAdapter adapter =
                new AppointmentRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        when(jpaRepository.existsByAppointment_IdAndCustomer_IdAndStatus(
                5L,
                20L,
                AppointmentRequestStatus.PENDING
        )).thenReturn(true);

        boolean result = adapter.existsPendingByAppointmentIdAndCustomerId(5L, 20L);

        assertEquals(true, result);
        verify(jpaRepository).existsByAppointment_IdAndCustomer_IdAndStatus(
                5L,
                20L,
                AppointmentRequestStatus.PENDING
        );
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    @Test
    @DisplayName("save persists Jpa entity and returns domain")
    void savePersistsJpaAndReturnsDomain() {
        AppointmentRequestJpaRepository jpaRepository = mock(AppointmentRequestJpaRepository.class);
        EntityManager entityManager = mock(EntityManager.class);
        AppointmentRequestRepositoryJpaAdapter adapter =
                new AppointmentRequestRepositoryJpaAdapter(jpaRepository, entityManager);
        AppointmentJpa appointment = appointmentJpa();
        UserJpa customer = userJpa();
        LocalDateTime createdAt = LocalDateTime.of(2026, 5, 24, 10, 0);
        AppointmentRequest request = AppointmentRequest.create(5L, 20L, "message", createdAt);

        when(entityManager.getReference(AppointmentJpa.class, 5L)).thenReturn(appointment);
        when(entityManager.getReference(UserJpa.class, 20L)).thenReturn(customer);
        when(jpaRepository.save(any())).thenReturn(new AppointmentRequestJpa(
                1L,
                appointment,
                customer,
                AppointmentRequestStatus.PENDING,
                "message",
                createdAt,
                null
        ));

        AppointmentRequest result = adapter.save(request);

        assertEquals(1L, result.getId());
        assertEquals(5L, result.getAppointmentId());
        assertEquals(20L, result.getCustomerId());

        ArgumentCaptor<AppointmentRequestJpa> captor = ArgumentCaptor.forClass(AppointmentRequestJpa.class);
        verify(jpaRepository).save(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(AppointmentRequestStatus.PENDING, captor.getValue().getStatus());
        verify(entityManager).getReference(AppointmentJpa.class, 5L);
        verify(entityManager).getReference(UserJpa.class, 20L);
        verifyNoMoreInteractions(jpaRepository, entityManager);
    }

    private static AppointmentRequestJpa pendingJpa(Long id) {
        return new AppointmentRequestJpa(
                id,
                appointmentJpa(),
                userJpa(),
                AppointmentRequestStatus.PENDING,
                "message",
                LocalDateTime.of(2026, 5, 24, 10, 0),
                null
        );
    }

    private static AppointmentJpa appointmentJpa() {
        return new AppointmentJpa(
                5L,
                LocalDateTime.of(2026, 5, 24, 12, 0),
                LocalDateTime.of(2026, 5, 24, 13, 0),
                AppointmentStatus.FREE,
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

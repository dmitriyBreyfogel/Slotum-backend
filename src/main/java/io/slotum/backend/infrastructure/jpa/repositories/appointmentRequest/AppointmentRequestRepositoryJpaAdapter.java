package io.slotum.backend.infrastructure.jpa.repositories.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentJpa;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import io.slotum.backend.infrastructure.jpa.mappers.AppointmentRequestJpaMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentRequestRepositoryJpaAdapter implements AppointmentRequestRepository {
    private final AppointmentRequestJpaRepository appointmentRequestJpaRepository;
    private final EntityManager entityManager;

    public AppointmentRequestRepositoryJpaAdapter(
            AppointmentRequestJpaRepository appointmentRequestJpaRepository,
            EntityManager entityManager
    ) {
        this.appointmentRequestJpaRepository = appointmentRequestJpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<AppointmentRequest> findById(Long id) {
        return appointmentRequestJpaRepository.findById(id).map(AppointmentRequestJpaMapper::toDomain);
    }

    @Override
    public List<AppointmentRequest> findAll() {
        return appointmentRequestJpaRepository.findAll().stream()
                .map(AppointmentRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<AppointmentRequest> findByAppointmentId(Long appointmentId) {
        return appointmentRequestJpaRepository.findAllByAppointment_Id(appointmentId).stream()
                .map(AppointmentRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<AppointmentRequest> findPendingByAppointmentId(Long appointmentId) {
        return appointmentRequestJpaRepository
                .findAllByAppointment_IdAndStatus(appointmentId, AppointmentRequestStatus.PENDING)
                .stream()
                .map(AppointmentRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<AppointmentRequest> findByCustomerId(Long customerId) {
        return appointmentRequestJpaRepository.findAllByCustomer_Id(customerId).stream()
                .map(AppointmentRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<AppointmentRequest> findBySpecialistUserId(Long specialistUserId) {
        return appointmentRequestJpaRepository.findAllByAppointment_Specialist_UserId(specialistUserId).stream()
                .map(AppointmentRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsPendingByAppointmentIdAndCustomerId(Long appointmentId, Long customerId) {
        return appointmentRequestJpaRepository.existsByAppointment_IdAndCustomer_IdAndStatus(
                appointmentId,
                customerId,
                AppointmentRequestStatus.PENDING
        );
    }

    @Override
    public AppointmentRequest save(AppointmentRequest appointmentRequest) {
        AppointmentJpa appointmentRef = entityManager.getReference(
                AppointmentJpa.class,
                appointmentRequest.getAppointmentId()
        );
        UserJpa customerRef = entityManager.getReference(UserJpa.class, appointmentRequest.getCustomerId());

        AppointmentRequestJpa saved = appointmentRequestJpaRepository.save(
                AppointmentRequestJpaMapper.toJpa(appointmentRequest, appointmentRef, customerRef)
        );

        return AppointmentRequestJpaMapper.toDomain(saved);
    }
}

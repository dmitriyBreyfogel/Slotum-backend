package io.slotum.backend.infrastructure.jpa.repositories.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
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
    public List<AppointmentRequest> findBySlotId(Long slotId) {
        return appointmentRequestJpaRepository.findAllBySlot_Id(slotId).stream()
                .map(AppointmentRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<AppointmentRequest> findPendingBySlotId(Long slotId) {
        return appointmentRequestJpaRepository
                .findAllBySlot_IdAndStatus(slotId, AppointmentRequestStatus.PENDING)
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
        return appointmentRequestJpaRepository.findAllBySlot_Specialist_UserId(specialistUserId).stream()
                .map(AppointmentRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsPendingBySlotIdAndCustomerId(Long slotId, Long customerId) {
        return appointmentRequestJpaRepository.existsBySlot_IdAndCustomer_IdAndStatus(
                slotId,
                customerId,
                AppointmentRequestStatus.PENDING
        );
    }

    @Override
    public AppointmentRequest save(AppointmentRequest appointmentRequest) {
        SlotJpa slotRef = entityManager.getReference(
                SlotJpa.class,
                appointmentRequest.getSlotId()
        );
        UserJpa customerRef = entityManager.getReference(UserJpa.class, appointmentRequest.getCustomerId());

        AppointmentRequestJpa saved = appointmentRequestJpaRepository.save(
                AppointmentRequestJpaMapper.toJpa(appointmentRequest, slotRef, customerRef)
        );

        return AppointmentRequestJpaMapper.toDomain(saved);
    }
}

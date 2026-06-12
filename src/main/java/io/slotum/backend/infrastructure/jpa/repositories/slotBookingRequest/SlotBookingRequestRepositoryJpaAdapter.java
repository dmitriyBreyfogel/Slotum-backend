package io.slotum.backend.infrastructure.jpa.repositories.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.SlotBookingRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import io.slotum.backend.infrastructure.jpa.mappers.SlotBookingRequestJpaMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SlotBookingRequestRepositoryJpaAdapter implements SlotBookingRequestRepository {
    private final SlotBookingRequestJpaRepository slotBookingRequestJpaRepository;
    private final EntityManager entityManager;

    public SlotBookingRequestRepositoryJpaAdapter(
            SlotBookingRequestJpaRepository slotBookingRequestJpaRepository,
            EntityManager entityManager
    ) {
        this.slotBookingRequestJpaRepository = slotBookingRequestJpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<SlotBookingRequest> findById(Long id) {
        return slotBookingRequestJpaRepository.findById(id).map(SlotBookingRequestJpaMapper::toDomain);
    }

    @Override
    public List<SlotBookingRequest> findAll() {
        return slotBookingRequestJpaRepository.findAll().stream()
                .map(SlotBookingRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<SlotBookingRequest> findBySlotId(Long slotId) {
        return slotBookingRequestJpaRepository.findAllBySlot_Id(slotId).stream()
                .map(SlotBookingRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<SlotBookingRequest> findPendingBySlotId(Long slotId) {
        return slotBookingRequestJpaRepository
                .findAllBySlot_IdAndStatus(slotId, SlotBookingRequestStatus.PENDING)
                .stream()
                .map(SlotBookingRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<SlotBookingRequest> findByCustomerId(Long customerId) {
        return slotBookingRequestJpaRepository.findAllByCustomer_Id(customerId).stream()
                .map(SlotBookingRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public List<SlotBookingRequest> findBySpecialistUserId(Long specialistUserId) {
        return slotBookingRequestJpaRepository.findAllBySlot_Specialist_UserId(specialistUserId).stream()
                .map(SlotBookingRequestJpaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsPendingBySlotIdAndCustomerId(Long slotId, Long customerId) {
        return slotBookingRequestJpaRepository.existsBySlot_IdAndCustomer_IdAndStatus(
                slotId,
                customerId,
                SlotBookingRequestStatus.PENDING
        );
    }

    @Override
    public SlotBookingRequest save(SlotBookingRequest slotBookingRequest) {
        SlotJpa slotRef = entityManager.getReference(
                SlotJpa.class,
                slotBookingRequest.getSlotId()
        );
        UserJpa customerRef = entityManager.getReference(UserJpa.class, slotBookingRequest.getCustomerId());

        SlotBookingRequestJpa saved = slotBookingRequestJpaRepository.save(
                SlotBookingRequestJpaMapper.toJpa(slotBookingRequest, slotRef, customerRef)
        );

        return SlotBookingRequestJpaMapper.toDomain(saved);
    }
}

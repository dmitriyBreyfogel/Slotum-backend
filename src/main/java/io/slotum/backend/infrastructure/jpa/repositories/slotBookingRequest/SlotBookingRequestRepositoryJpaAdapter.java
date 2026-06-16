package io.slotum.backend.infrastructure.jpa.repositories.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.SlotBookingRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import io.slotum.backend.infrastructure.jpa.error.DatabaseConstraintExceptionResolver;
import io.slotum.backend.infrastructure.jpa.mappers.SlotBookingRequestJpaMapper;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class SlotBookingRequestRepositoryJpaAdapter implements SlotBookingRequestRepository {
    private static final String PENDING_SLOT_CUSTOMER_UNIQUE_INDEX =
            "uq_slot_booking_requests_pending_slot_customer";

    private final SlotBookingRequestJpaRepository slotBookingRequestJpaRepository;
    private final EntityManager entityManager;
    private final DatabaseConstraintExceptionResolver databaseConstraintExceptionResolver;

    public SlotBookingRequestRepositoryJpaAdapter(
            SlotBookingRequestJpaRepository slotBookingRequestJpaRepository,
            EntityManager entityManager,
            DatabaseConstraintExceptionResolver databaseConstraintExceptionResolver
    ) {
        this.slotBookingRequestJpaRepository = slotBookingRequestJpaRepository;
        this.entityManager = entityManager;
        this.databaseConstraintExceptionResolver = databaseConstraintExceptionResolver;
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

        SlotBookingRequestJpa saved;
        try {
            saved = slotBookingRequestJpaRepository.save(
                    SlotBookingRequestJpaMapper.toJpa(slotBookingRequest, slotRef, customerRef)
            );
        } catch (DataIntegrityViolationException ex) {
            throw databaseConstraintExceptionResolver
                    .resolve(ex, Map.of(
                            "slotId", slotBookingRequest.getSlotId(),
                            "customerId", slotBookingRequest.getCustomerId()
                    ))
                    .orElseThrow(() -> ex);
        }

        return SlotBookingRequestJpaMapper.toDomain(saved);
    }

    @Override
    public boolean acceptIfPending(Long id, LocalDateTime decidedAt) {
        int updatedRows = slotBookingRequestJpaRepository.acceptIfPending(
                id,
                decidedAt,
                SlotBookingRequestStatus.PENDING,
                SlotBookingRequestStatus.ACCEPTED
        );
        return updatedRows == 1;
    }

    @Override
    public boolean rejectIfPending(Long id, LocalDateTime decidedAt) {
        int updatedRows = slotBookingRequestJpaRepository.rejectIfPending(
                id,
                decidedAt,
                SlotBookingRequestStatus.PENDING,
                SlotBookingRequestStatus.REJECTED
        );
        return updatedRows == 1;
    }

    @Override
    public boolean cancelIfPending(Long id, LocalDateTime decidedAt) {
        int updatedRows = slotBookingRequestJpaRepository.cancelIfPending(
                id,
                decidedAt,
                SlotBookingRequestStatus.PENDING,
                SlotBookingRequestStatus.CANCELLED
        );
        return updatedRows == 1;
    }
}

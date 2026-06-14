package io.slotum.backend.infrastructure.jpa.repositories.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.SlotBookingRequestJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
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

        SlotBookingRequestJpa saved;
        try {
            saved = slotBookingRequestJpaRepository.save(
                    SlotBookingRequestJpaMapper.toJpa(slotBookingRequest, slotRef, customerRef)
            );
        } catch (DataIntegrityViolationException ex) {
            if (isPendingRequestDuplicate(ex)) {
                throw AppException.build(
                        ErrorCode.SLOT_BOOKING_REQUEST_ALREADY_EXISTS,
                        "Pending slot booking request already exists",
                        Map.of(
                                "slotId", slotBookingRequest.getSlotId(),
                                "customerId", slotBookingRequest.getCustomerId()
                        )
                );
            }
            throw ex;
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

    private static boolean isPendingRequestDuplicate(DataIntegrityViolationException ex) {
        Throwable cause = ex;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintViolation
                    && PENDING_SLOT_CUSTOMER_UNIQUE_INDEX.equals(constraintViolation.getConstraintName())) {
                return true;
            }

            String message = cause.getMessage();
            if (message != null && message.contains(PENDING_SLOT_CUSTOMER_UNIQUE_INDEX)) {
                return true;
            }

            cause = cause.getCause();
        }
        return false;
    }
}

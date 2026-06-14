package io.slotum.backend.infrastructure.jpa.repositories.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.SlotBookingRequestJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SlotBookingRequestJpaRepository extends JpaRepository<SlotBookingRequestJpa, Long> {
    List<SlotBookingRequestJpa> findAllBySlot_Id(Long slotId);

    List<SlotBookingRequestJpa> findAllBySlot_IdAndStatus(
            Long slotId,
            SlotBookingRequestStatus status
    );

    List<SlotBookingRequestJpa> findAllByCustomer_Id(Long customerId);

    List<SlotBookingRequestJpa> findAllBySlot_Specialist_UserId(Long specialistUserId);

    boolean existsBySlot_IdAndCustomer_IdAndStatus(
            Long slotId,
            Long customerId,
            SlotBookingRequestStatus status
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update SlotBookingRequestJpa request
            set request.status = :acceptedStatus,
                request.decidedAt = :decidedAt
            where request.id = :id
              and request.status = :pendingStatus
            """)
    int acceptIfPending(
            @Param("id") Long id,
            @Param("decidedAt") LocalDateTime decidedAt,
            @Param("pendingStatus") SlotBookingRequestStatus pendingStatus,
            @Param("acceptedStatus") SlotBookingRequestStatus acceptedStatus
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update SlotBookingRequestJpa request
            set request.status = :rejectedStatus,
                request.decidedAt = :decidedAt
            where request.id = :id
              and request.status = :pendingStatus
            """)
    int rejectIfPending(
            @Param("id") Long id,
            @Param("decidedAt") LocalDateTime decidedAt,
            @Param("pendingStatus") SlotBookingRequestStatus pendingStatus,
            @Param("rejectedStatus") SlotBookingRequestStatus rejectedStatus
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update SlotBookingRequestJpa request
            set request.status = :cancelledStatus,
                request.decidedAt = :decidedAt
            where request.id = :id
              and request.status = :pendingStatus
            """)
    int cancelIfPending(
            @Param("id") Long id,
            @Param("decidedAt") LocalDateTime decidedAt,
            @Param("pendingStatus") SlotBookingRequestStatus pendingStatus,
            @Param("cancelledStatus") SlotBookingRequestStatus cancelledStatus
    );
}

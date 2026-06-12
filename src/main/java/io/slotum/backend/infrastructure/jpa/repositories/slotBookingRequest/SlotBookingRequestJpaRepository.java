package io.slotum.backend.infrastructure.jpa.repositories.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.SlotBookingRequestJpa;
import org.springframework.data.jpa.repository.JpaRepository;

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
}

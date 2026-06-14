package io.slotum.backend.domain.slotBookingRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SlotBookingRequestRepository {
    Optional<SlotBookingRequest> findById(Long id);

    List<SlotBookingRequest> findAll();

    List<SlotBookingRequest> findBySlotId(Long slotId);

    List<SlotBookingRequest> findPendingBySlotId(Long slotId);

    List<SlotBookingRequest> findByCustomerId(Long customerId);

    List<SlotBookingRequest> findBySpecialistUserId(Long specialistUserId);

    boolean existsPendingBySlotIdAndCustomerId(Long slotId, Long customerId);

    SlotBookingRequest save(SlotBookingRequest slotBookingRequest);

    boolean acceptIfPending(Long id, LocalDateTime decidedAt);

    boolean rejectIfPending(Long id, LocalDateTime decidedAt);
}

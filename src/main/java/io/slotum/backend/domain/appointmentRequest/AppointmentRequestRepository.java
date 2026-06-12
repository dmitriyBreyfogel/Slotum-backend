package io.slotum.backend.domain.appointmentRequest;

import java.util.List;
import java.util.Optional;

public interface AppointmentRequestRepository {
    Optional<AppointmentRequest> findById(Long id);

    List<AppointmentRequest> findAll();

    List<AppointmentRequest> findBySlotId(Long slotId);

    List<AppointmentRequest> findPendingBySlotId(Long slotId);

    List<AppointmentRequest> findByCustomerId(Long customerId);

    List<AppointmentRequest> findBySpecialistUserId(Long specialistUserId);

    boolean existsPendingBySlotIdAndCustomerId(Long slotId, Long customerId);

    AppointmentRequest save(AppointmentRequest appointmentRequest);
}

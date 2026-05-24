package io.slotum.backend.domain.appointmentRequest;

import java.util.List;
import java.util.Optional;

public interface AppointmentRequestRepository {
    Optional<AppointmentRequest> findById(Long id);

    List<AppointmentRequest> findAll();

    List<AppointmentRequest> findByAppointmentId(Long appointmentId);

    List<AppointmentRequest> findPendingByAppointmentId(Long appointmentId);

    List<AppointmentRequest> findByCustomerId(Long customerId);

    List<AppointmentRequest> findBySpecialistUserId(Long specialistUserId);

    boolean existsPendingByAppointmentIdAndCustomerId(Long appointmentId, Long customerId);

    AppointmentRequest save(AppointmentRequest appointmentRequest);
}

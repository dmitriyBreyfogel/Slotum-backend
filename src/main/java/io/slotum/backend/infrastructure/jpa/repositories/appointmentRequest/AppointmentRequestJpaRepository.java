package io.slotum.backend.infrastructure.jpa.repositories.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentRequestJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRequestJpaRepository extends JpaRepository<AppointmentRequestJpa, Long> {
    List<AppointmentRequestJpa> findAllBySlot_Id(Long slotId);

    List<AppointmentRequestJpa> findAllBySlot_IdAndStatus(
            Long slotId,
            AppointmentRequestStatus status
    );

    List<AppointmentRequestJpa> findAllByCustomer_Id(Long customerId);

    List<AppointmentRequestJpa> findAllBySlot_Specialist_UserId(Long specialistUserId);

    boolean existsBySlot_IdAndCustomer_IdAndStatus(
            Long slotId,
            Long customerId,
            AppointmentRequestStatus status
    );
}

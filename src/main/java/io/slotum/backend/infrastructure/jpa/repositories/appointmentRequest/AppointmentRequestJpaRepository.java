package io.slotum.backend.infrastructure.jpa.repositories.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentRequestJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRequestJpaRepository extends JpaRepository<AppointmentRequestJpa, Long> {
    List<AppointmentRequestJpa> findAllByAppointment_Id(Long appointmentId);

    List<AppointmentRequestJpa> findAllByAppointment_IdAndStatus(
            Long appointmentId,
            AppointmentRequestStatus status
    );

    List<AppointmentRequestJpa> findAllByCustomer_Id(Long customerId);

    List<AppointmentRequestJpa> findAllByAppointment_Specialist_UserId(Long specialistUserId);

    boolean existsByAppointment_IdAndCustomer_IdAndStatus(
            Long appointmentId,
            Long customerId,
            AppointmentRequestStatus status
    );
}

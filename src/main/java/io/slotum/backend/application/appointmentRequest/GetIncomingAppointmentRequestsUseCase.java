package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetIncomingAppointmentRequestsUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final SpecialistRepository specialistRepository;

    public GetIncomingAppointmentRequestsUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            SpecialistRepository specialistRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.specialistRepository = specialistRepository;
    }

    public List<AppointmentRequest> execute(Long specialistUserId) {
        Optional<Specialist> specialist = specialistRepository.findSpecialistByUserId(specialistUserId);
        if (specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("specialistUserId", specialistUserId)
            );
        }

        return appointmentRequestRepository.findBySpecialistUserId(specialistUserId);
    }
}

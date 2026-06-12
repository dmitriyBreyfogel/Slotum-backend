package io.slotum.backend.application.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetIncomingSlotBookingRequestsUseCase {
    private final SlotBookingRequestRepository slotBookingRequestRepository;
    private final SpecialistRepository specialistRepository;

    public GetIncomingSlotBookingRequestsUseCase(
            SlotBookingRequestRepository slotBookingRequestRepository,
            SpecialistRepository specialistRepository
    ) {
        this.slotBookingRequestRepository = slotBookingRequestRepository;
        this.specialistRepository = specialistRepository;
    }

    public List<SlotBookingRequest> execute(Long specialistUserId) {
        Optional<Specialist> specialist = specialistRepository.findSpecialistByUserId(specialistUserId);
        if (specialist.isEmpty()) {
            throw AppException.build(
                    ErrorCode.SPECIALIST_NOT_FOUND,
                    "Specialist not found",
                    Map.of("specialistUserId", specialistUserId)
            );
        }

        return slotBookingRequestRepository.findBySpecialistUserId(specialistUserId);
    }
}

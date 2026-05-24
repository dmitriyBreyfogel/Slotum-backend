package io.slotum.backend.application.appointmentRequest;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequest;
import io.slotum.backend.domain.appointmentRequest.AppointmentRequestRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetMyAppointmentRequestsUseCase {
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final UserRepository userRepository;

    public GetMyAppointmentRequestsUseCase(
            AppointmentRequestRepository appointmentRequestRepository,
            UserRepository userRepository
    ) {
        this.appointmentRequestRepository = appointmentRequestRepository;
        this.userRepository = userRepository;
    }

    public List<AppointmentRequest> execute(Long customerId) {
        Optional<User> customer = userRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User customer not found",
                    Map.of("customerId", customerId)
            );
        }

        return appointmentRequestRepository.findByCustomerId(customerId);
    }
}

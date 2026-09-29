package io.slotum.backend.application.usecase.slotBookingRequest;

import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequest;
import io.slotum.backend.domain.slotBookingRequest.SlotBookingRequestRepository;
import io.slotum.backend.domain.user.User;
import io.slotum.backend.domain.user.UserRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GetMySlotBookingRequestsUseCase {
    private final SlotBookingRequestRepository slotBookingRequestRepository;
    private final UserRepository userRepository;

    public GetMySlotBookingRequestsUseCase(
            SlotBookingRequestRepository slotBookingRequestRepository,
            UserRepository userRepository
    ) {
        this.slotBookingRequestRepository = slotBookingRequestRepository;
        this.userRepository = userRepository;
    }

    public List<SlotBookingRequest> execute(Long customerId) {
        Optional<User> customer = userRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw AppException.build(
                    ErrorCode.USER_NOT_FOUND,
                    "User customer not found",
                    Map.of("customerId", customerId)
            );
        }

        return slotBookingRequestRepository.findByCustomerId(customerId);
    }
}

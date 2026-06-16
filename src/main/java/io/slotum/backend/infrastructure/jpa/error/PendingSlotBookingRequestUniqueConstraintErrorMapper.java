package io.slotum.backend.infrastructure.jpa.error;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public final class PendingSlotBookingRequestUniqueConstraintErrorMapper implements DatabaseConstraintErrorMapper {
    private static final String PENDING_SLOT_CUSTOMER_UNIQUE_INDEX = "uq_slot_booking_requests_pending_slot_customer";

    @Override
    public boolean supports(final String sql) {
        return sql.contains(PENDING_SLOT_CUSTOMER_UNIQUE_INDEX);
    }

    @Override
    public ErrorCode resolve() {
        return ErrorCode.SLOT_BOOKING_REQUEST_ALREADY_EXISTS;
    }

    @Override
    public AppException buildException(Map<String, Object> details) {
        throw AppException.build(
                resolve(),
                "Pending slot booking request already exists",
                details
        );
    }
}

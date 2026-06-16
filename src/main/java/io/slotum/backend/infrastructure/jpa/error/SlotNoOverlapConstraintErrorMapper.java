package io.slotum.backend.infrastructure.jpa.error;

import io.slotum.backend.error.AppException;
import io.slotum.backend.error.ErrorCode;

import java.util.Map;

public class SlotNoOverlapConstraintErrorMapper implements DatabaseConstraintErrorMapper{
    private static final String SLOTS_NO_OVERLAP_CONSTRAINT = "ex_slots_no_overlap";

    @Override
    public boolean supports(final String sql) {
        return sql.contains(SLOTS_NO_OVERLAP_CONSTRAINT);
    }

    @Override
    public ErrorCode resolve() {
        return ErrorCode.SLOT_OVERLAPPING;
    }

    @Override
    public AppException buildException(Map<String, Object> details) {
        throw AppException.build(
                resolve(),
                "Slot overlapping",
                details
        );
    }
}

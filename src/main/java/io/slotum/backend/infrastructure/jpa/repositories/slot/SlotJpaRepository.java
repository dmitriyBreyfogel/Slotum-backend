package io.slotum.backend.infrastructure.jpa.repositories.slot;

import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SlotJpaRepository extends JpaRepository<SlotJpa, Long> {
    boolean existsBySpecialistUserIdAndStatusNotAndStartsAtLessThanAndEndsAtGreaterThan(
            Long specialistUserId,
            SlotStatus status,
            LocalDateTime endsAt,
            LocalDateTime startsAt
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            value = """
                  update slots
                  set status = 'BOOKED',
                      customer_id = :customerId
                  where id = :slotId
                    and specialist_id = :specialistUserId
                    and status = 'FREE'
                  """,
            nativeQuery = true
    )
    int bookIfFree(
            @Param("slotId") Long slotId,
            @Param("specialistUserId") Long specialistUserId,
            @Param("customerId") Long customerId
    );
}
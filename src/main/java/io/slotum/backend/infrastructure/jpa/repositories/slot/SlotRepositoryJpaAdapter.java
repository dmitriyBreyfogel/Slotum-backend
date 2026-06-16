package io.slotum.backend.infrastructure.jpa.repositories.slot;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import io.slotum.backend.infrastructure.jpa.error.resolvers.DatabaseConstraintExceptionResolver;
import io.slotum.backend.infrastructure.jpa.mappers.SlotJpaMapper;
import jakarta.persistence.EntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SlotRepositoryJpaAdapter implements SlotRepository {
    private static final String SLOTS_NO_OVERLAP_CONSTRAINT = "ex_slots_no_overlap";

    private final SlotJpaRepository slotJpaRepository;
    private final EntityManager entityManager;
    private final DatabaseConstraintExceptionResolver databaseConstraintExceptionResolver;

    public SlotRepositoryJpaAdapter(
            SlotJpaRepository slotJpaRepository,
            EntityManager entityManager,
            DatabaseConstraintExceptionResolver databaseConstraintExceptionResolver
    ) {
        this.slotJpaRepository = slotJpaRepository;
        this.entityManager = entityManager;
        this.databaseConstraintExceptionResolver = databaseConstraintExceptionResolver;
    }

    @Override
    public Optional<Slot> findById(Long id) {
        return slotJpaRepository.findById(id).map(SlotJpaMapper::toDomain);
    }

    @Override
    public List<Slot> findAll() {
        return slotJpaRepository.findAll().stream().map(SlotJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Slot deleteById(Long id) {
        Optional<SlotJpa> deleted = slotJpaRepository.findById(id);
        if (deleted.isPresent()) {
            slotJpaRepository.deleteById(id);
            return SlotJpaMapper.toDomain(deleted.get());
        }
        return null;
    }

    @Override
    public void deleteAll() {
        slotJpaRepository.deleteAll();
    }

    @Override
    public Slot save(Slot slot) {
        SpecialistJpa specialistRef = entityManager.getReference(SpecialistJpa.class, slot.getSpecialistUserId());
        UserJpa customerRef = slot.getCustomerId() == null
                ? null
                : entityManager.getReference(UserJpa.class, slot.getCustomerId());
        OrganizationJpa organizationRef = entityManager.getReference(OrganizationJpa.class, slot.getOrganizationId());

        try {
            return SlotJpaMapper.toDomain(
                    slotJpaRepository.save(
                            SlotJpaMapper.toJpa(slot, specialistRef, customerRef, organizationRef)
                    )
            );
        } catch (DataIntegrityViolationException ex) {
            throw databaseConstraintExceptionResolver
                    .resolve(ex, Map.of(
                            "specialistUserId", slot.getSpecialistUserId(),
                            "organizationId", slot.getOrganizationId(),
                            "startsAt", slot.getStartsAt(),
                            "endsAt", slot.getEndsAt()
                    ))
                    .orElseThrow(() -> ex);
        }
    }

    @Override
    public boolean existsOverlappingSlot(Long specialistUserId, LocalDateTime startsAt, LocalDateTime endsAt) {
        return slotJpaRepository.existsBySpecialistUserIdAndStatusNotAndStartsAtLessThanAndEndsAtGreaterThan(
                specialistUserId,
                SlotStatus.CANCELLED,
                endsAt,
                startsAt
        );
    }

    @Override
    public boolean bookIfFree(Long slotId, Long specialistUserId, Long customerId) {
        int updatedRows = slotJpaRepository.bookIfFree(slotId, specialistUserId, customerId);
        return updatedRows == 1;
    }
}

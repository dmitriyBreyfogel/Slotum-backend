package io.slotum.backend.infrastructure.jpa.repositories.slot;

import io.slotum.backend.domain.slot.Slot;
import io.slotum.backend.domain.slot.SlotRepository;
import io.slotum.backend.domain.slot.SlotStatus;
import io.slotum.backend.error.AppException;
import io.slotum.backend.infrastructure.jpa.entities.SlotJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import io.slotum.backend.infrastructure.jpa.mappers.SlotJpaMapper;
import io.slotum.backend.infrastructure.jpa.mappers.OrganizationJpaMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SlotRepositoryJpaAdapter implements SlotRepository {
    private final SlotJpaRepository slotJpaRepository;
    private final EntityManager entityManager;

    public SlotRepositoryJpaAdapter(SlotJpaRepository slotJpaRepository, EntityManager entityManager) {
        this.slotJpaRepository = slotJpaRepository;
        this.entityManager = entityManager;
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

        return SlotJpaMapper.toDomain(
                slotJpaRepository.save(
                        SlotJpaMapper.toJpa(slot, specialistRef, customerRef, organizationRef)
                )
        );
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

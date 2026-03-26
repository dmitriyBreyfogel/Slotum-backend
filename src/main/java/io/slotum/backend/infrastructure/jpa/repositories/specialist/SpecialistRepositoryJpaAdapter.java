package io.slotum.backend.infrastructure.jpa.repositories.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.infrastructure.jpa.mappers.SpecialistJpaMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SpecialistRepositoryJpaAdapter implements SpecialistRepository {
    private final SpecialistJpaRepository specialistJpaRepository;

    public SpecialistRepositoryJpaAdapter(SpecialistJpaRepository specialistJpaRepository) {
        this.specialistJpaRepository = specialistJpaRepository;
    }
    
    @Override
    public Optional<Specialist> findSpecialistByUserId(Long userId) {
        return specialistJpaRepository.findByUserId(userId).map(SpecialistJpaMapper::toDomain);
    }

    @Override
    public Specialist save(Specialist specialist) {
        return SpecialistJpaMapper.toDomain(specialistJpaRepository.save(SpecialistJpaMapper.toJpa(specialist)));
    }
}

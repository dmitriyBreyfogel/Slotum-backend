package io.slotum.backend.infrastructure.jpa.repositories.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.mappers.SpecialistJpaMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class SpecialistRepositoryJpaAdapter implements SpecialistRepository {
    private final SpecialistJpaRepository specialistJpaRepository;

    public SpecialistRepositoryJpaAdapter(SpecialistJpaRepository specialistJpaRepository) {
        this.specialistJpaRepository = specialistJpaRepository;
    }

    @Override
    public Optional<Specialist> findById(Long id) {
        return specialistJpaRepository.findById(id).map(SpecialistJpaMapper::toDomain);
    }

    @Override
    public Optional<Specialist> findSpecialistByUserId(Long userId) {
        return specialistJpaRepository.findByUserId(userId).map(SpecialistJpaMapper::toDomain);
    }

    @Override
    public List<Specialist> findAll(){
        return specialistJpaRepository.findAll().stream().map(SpecialistJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Specialist save(Specialist specialist) {
        return SpecialistJpaMapper.toDomain(specialistJpaRepository.save(SpecialistJpaMapper.toJpa(specialist)));
    }

    @Override
    @Transactional
    public Optional<Specialist> deleteById(Long id) {
        Optional<SpecialistJpa> specialistJpa = specialistJpaRepository.findById(id);

        if (specialistJpa.isEmpty()) {
            return Optional.empty();
        }

        Specialist specialist = SpecialistJpaMapper.toDomain(specialistJpa.get());
        specialistJpaRepository.delete(specialistJpa.get());

        return Optional.of(specialist);
    }

    @Override
    public void deleteAll() {
        specialistJpaRepository.deleteAll();
    }
}

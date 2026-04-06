package io.slotum.backend.infrastructure.jpa.repositories.specialist;

import io.slotum.backend.domain.specialist.Specialist;
import io.slotum.backend.domain.specialist.SpecialistRepository;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.mappers.OrganizationJpaMapper;
import io.slotum.backend.infrastructure.jpa.mappers.SpecialistJpaMapper;
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
    public Specialist deleteById(Long id){
        Optional<SpecialistJpa> deleted = specialistJpaRepository.findById(id);
        if (deleted.isPresent()) {
            specialistJpaRepository.deleteById(id);
            return SpecialistJpaMapper.toDomain(deleted.get());
        }
        return null;
    }
}

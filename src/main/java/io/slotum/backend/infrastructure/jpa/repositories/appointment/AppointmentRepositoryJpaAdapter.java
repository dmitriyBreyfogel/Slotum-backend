package io.slotum.backend.infrastructure.jpa.repositories.appointment;

import io.slotum.backend.domain.appointment.Appointment;
import io.slotum.backend.domain.appointment.AppointmentRepository;
import io.slotum.backend.error.AppException;
import io.slotum.backend.infrastructure.jpa.entities.AppointmentJpa;
import io.slotum.backend.infrastructure.jpa.entities.OrganizationJpa;
import io.slotum.backend.infrastructure.jpa.entities.SpecialistJpa;
import io.slotum.backend.infrastructure.jpa.entities.UserJpa;
import io.slotum.backend.infrastructure.jpa.mappers.AppointmentJpaMapper;
import io.slotum.backend.infrastructure.jpa.mappers.OrganizationJpaMapper;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AppointmentRepositoryJpaAdapter implements AppointmentRepository {
    private final AppointmentJpaRepository appointmentJpaRepository;
    private final EntityManager entityManager;

    public AppointmentRepositoryJpaAdapter(AppointmentJpaRepository appointmentJpaRepository, EntityManager entityManager) {
        this.appointmentJpaRepository = appointmentJpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return appointmentJpaRepository.findById(id).map(AppointmentJpaMapper::toDomain);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentJpaRepository.findAll().stream().map(AppointmentJpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Appointment deleteById(Long id) {
        Optional<AppointmentJpa> deleted = appointmentJpaRepository.findById(id);
        if (deleted.isPresent()) {
            appointmentJpaRepository.deleteById(id);
            return AppointmentJpaMapper.toDomain(deleted.get());
        }
        return null;
    }

    @Override
    public Appointment save(Appointment appointment) {
        SpecialistJpa specialistRef = entityManager.getReference(SpecialistJpa.class, appointment.getSpecialistUserId());
        UserJpa customerRef = entityManager.getReference(UserJpa.class, appointment.getCustomerId());
        OrganizationJpa organizationRef = entityManager.getReference(OrganizationJpa.class, appointment.getOrganizationId());

        return AppointmentJpaMapper.toDomain(
                appointmentJpaRepository.save(
                        AppointmentJpaMapper.toJpa(appointment, specialistRef, customerRef, organizationRef)
                )
        );
    }
}
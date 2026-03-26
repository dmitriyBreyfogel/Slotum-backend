package io.slotum.backend.infrastructure.jpa.entities;

import io.slotum.backend.domain.appointment.AppointmentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class AppointmentJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AppointmentStatus status;

    // Communications
    @ManyToOne
    @JoinColumn(name = "specialist_id", referencedColumnName = "user_id", nullable = false)
    private SpecialistJpa specialist;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private UserJpa customer;

    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id", nullable = false)
    private OrganizationJpa organization;

    protected AppointmentJpa() {}

    public AppointmentJpa(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            AppointmentStatus status,
            SpecialistJpa specialist,
            UserJpa customer,
            OrganizationJpa organization
    ) {
        this.id = id;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.status = status;
        this.specialist = specialist;
        this.customer = customer;
        this.organization = organization;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public SpecialistJpa getSpecialist() {
        return specialist;
    }

    public UserJpa getCustomer() {
        return customer;
    }

    public OrganizationJpa getOrganization() {
        return organization;
    }
}

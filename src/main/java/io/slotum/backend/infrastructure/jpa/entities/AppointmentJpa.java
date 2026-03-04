package io.slotum.backend.infrastructure.jpa.entities;

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

    @Column(nullable = false)
    private String status;

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

    public String getStatus() {
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

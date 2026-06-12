package io.slotum.backend.infrastructure.jpa.entities;

import io.slotum.backend.domain.slot.SlotStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "slots")
public class SlotJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SlotStatus status;

    // Communications
    @ManyToOne
    @JoinColumn(name = "specialist_id", referencedColumnName = "user_id", nullable = false)
    private SpecialistJpa specialist;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private UserJpa customer;

    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "id", nullable = false)
    private OrganizationJpa organization;

    protected SlotJpa() {}

    public SlotJpa(
            Long id,
            LocalDateTime startsAt,
            LocalDateTime endsAt,
            SlotStatus status,
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

    public SlotStatus getStatus() {
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

package io.slotum.backend.infrastructure.jpa.entities;

import io.slotum.backend.domain.appointmentRequest.AppointmentRequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_requests")
public class AppointmentRequestJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "slot_id", referencedColumnName = "id", nullable = false)
    private SlotJpa slot;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    private UserJpa customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AppointmentRequestStatus status;

    @Column(length = 1024)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    protected AppointmentRequestJpa() {}

    public AppointmentRequestJpa(
            Long id,
            SlotJpa slot,
            UserJpa customer,
            AppointmentRequestStatus status,
            String message,
            LocalDateTime createdAt,
            LocalDateTime decidedAt
    ) {
        this.id = id;
        this.slot = slot;
        this.customer = customer;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.decidedAt = decidedAt;
    }

    public Long getId() {
        return id;
    }

    public SlotJpa getSlot() {
        return slot;
    }

    public UserJpa getCustomer() {
        return customer;
    }

    public AppointmentRequestStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }
}

package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "specialists")
public class SpecialistJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column
    private Double grade;

    @Column
    private String description;

    // Communications
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserJpa user;

    protected SpecialistJpa() {}

    // Getters
    public Long getUserId() {
        return userId;
    }

    public Double getGrade() {
        return grade;
    }

    public String getDescription() {
        return description;
    }
}

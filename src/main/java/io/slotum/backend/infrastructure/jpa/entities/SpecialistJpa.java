package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "specialists")
public class SpecialistJpa  {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column
    private Double grade;

    @Column(length = 1024)
    private String description;

    // Communications
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private UserJpa user;

    protected SpecialistJpa() {}

    public SpecialistJpa(Long userId, String description, Double grade) {
        this.userId = userId;
        this.description = description;
        this.grade = grade;
    }

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

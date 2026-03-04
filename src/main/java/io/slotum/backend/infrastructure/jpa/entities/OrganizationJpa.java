package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(
     name = "organizations",
     uniqueConstraints = {
             @UniqueConstraint(name = "uq_organizations_name", columnNames = "name")
     }
)
public class OrganizationJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private Double grade;

    protected OrganizationJpa() {}

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getGrade() {
        return grade;
    }
}

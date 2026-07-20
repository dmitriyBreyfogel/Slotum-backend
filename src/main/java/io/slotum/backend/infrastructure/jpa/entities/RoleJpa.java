package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "roles_name_key", columnNames = "name")
        }
)
public class RoleJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;
}

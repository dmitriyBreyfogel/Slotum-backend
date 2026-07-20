package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;

@Entity
@Table(
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "permission_code_key", columnNames = "code")
        }
)
public class PermissionJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code", length = 100, nullable = false, unique = true)
    private String code;

    @Column(name = "description")
    private String description;
}

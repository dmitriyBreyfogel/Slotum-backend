package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(mappedBy = "permissions")
    private Set<RoleJpa> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "permission_resources",
            joinColumns = @JoinColumn(name = "permission_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private Set<ResourceJpa> resources = new HashSet<>();

    protected PermissionJpa() {}

    public PermissionJpa(Long id, String code, String description, Set<RoleJpa> roles, Set<ResourceJpa> resources) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.roles = roles;
        this.resources = resources;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Set<RoleJpa> getRoles() {
        return roles;
    }

    public Set<ResourceJpa> getResources() {
        return resources;
    }
}

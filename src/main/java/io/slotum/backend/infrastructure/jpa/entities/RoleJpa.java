package io.slotum.backend.infrastructure.jpa.entities;

import io.slotum.backend.domain.role.RoleNames;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 50, nullable = false, unique = true)
    private RoleNames name;

    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<PermissionJpa> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Set<UserJpa> users = new HashSet<>();

    protected RoleJpa() {}

    public RoleJpa(Long id, RoleNames name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public RoleNames getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<PermissionJpa> getPermissions() {
        return permissions;
    }

    public Set<UserJpa> getUsers() {
        return users;
    }
}

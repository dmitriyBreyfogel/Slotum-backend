package io.slotum.backend.infrastructure.jpa.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resources")
public class ResourceJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "http_method", length = 10, nullable = false)
    private String httpMethod;

    @Column(name = "url_pattern", nullable = false)
    private String urlPattern;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "resources")
    private Set<PermissionJpa> permissions = new HashSet<>();

    protected ResourceJpa() {}

    public ResourceJpa(Long id, String httpMethod, String urlPattern, String description) {
        this.id = id;
        this.httpMethod = httpMethod;
        this.urlPattern = urlPattern;
        this.description = description;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public String getDescription() {
        return description;
    }

    public Set<PermissionJpa> getPermissions() {
        return permissions;
    }
}

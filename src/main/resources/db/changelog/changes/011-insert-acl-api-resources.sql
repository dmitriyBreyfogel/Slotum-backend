-- liquibase formatted sql

-- changeset dmitriyBreyfogel:023-insert-acl-api-resources
WITH resources_to_insert(http_method, url_pattern, description) AS (
    VALUES
        ('POST', '/api/v1/roles', 'Create role'),
        ('GET', '/api/v1/roles', 'Get all roles'),
        ('GET', '/api/v1/roles/{id}', 'Get role by id'),
        ('GET', '/api/v1/roles/by-name', 'Get role by name'),
        ('DELETE', '/api/v1/roles/{id}', 'Delete role by id'),
        ('DELETE', '/api/v1/roles', 'Delete all roles'),

        ('POST', '/api/v1/permissions', 'Create permission'),
        ('GET', '/api/v1/permissions', 'Get all permissions'),
        ('GET', '/api/v1/permissions/{id}', 'Get permission by id'),
        ('GET', '/api/v1/permissions/by-code', 'Get permission by code'),
        ('DELETE', '/api/v1/permissions/{id}', 'Delete permission by id'),
        ('DELETE', '/api/v1/permissions', 'Delete all permissions'),

        ('POST', '/api/v1/resources', 'Create resource'),
        ('GET', '/api/v1/resources', 'Get all resources'),
        ('GET', '/api/v1/resources/{id}', 'Get resource by id'),
        ('DELETE', '/api/v1/resources/{id}', 'Delete resource by id'),
        ('DELETE', '/api/v1/resources', 'Delete all resources')
)
INSERT INTO resources (http_method, url_pattern, description)
SELECT http_method, url_pattern, description
FROM resources_to_insert candidate
WHERE NOT EXISTS (
    SELECT 1
    FROM resources existing
    WHERE existing.http_method = candidate.http_method
      AND existing.url_pattern = candidate.url_pattern
);

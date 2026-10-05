-- liquibase formatted sql

-- changeset dmitriyBreyfogel:020-insert-api-resources
WITH resources_to_insert(http_method, url_pattern, description) AS (
    VALUES
        ('POST', '/api/v1/auth/login', 'Login user'),

        ('POST', '/api/v1/users', 'Create user'),
        ('GET', '/api/v1/users', 'Get all users'),
        ('GET', '/api/v1/users/{id}', 'Get user by id'),
        ('GET', '/api/v1/users/by-email', 'Get user by email'),
        ('DELETE', '/api/v1/users/{id}', 'Delete user by id'),
        ('DELETE', '/api/v1/users', 'Delete all users'),

        ('POST', '/api/v1/me/specialist', 'Create specialist profile for current user'),
        ('POST', '/api/v1/me/organizations', 'Create organization for current specialist'),
        ('GET', '/api/v1/me/organizations', 'Get current specialist organizations'),
        ('DELETE', '/api/v1/me/organizations/{organizationId}', 'Remove current specialist from organization'),

        ('POST', '/api/v1/organizations', 'Create organization'),
        ('GET', '/api/v1/organizations', 'Get all organizations'),
        ('GET', '/api/v1/organizations/{id}', 'Get organization by id'),
        ('GET', '/api/v1/organizations/{organizationId}/specialists', 'Get organization specialists'),
        ('POST', '/api/v1/organizations/{organizationId}/specialists/{specialistUserId}', 'Add specialist to organization'),
        ('DELETE', '/api/v1/organizations/{id}', 'Delete organization by id'),
        ('DELETE', '/api/v1/organizations/{organizationId}/specialists/{specialistUserId}', 'Remove specialist from organization'),
        ('DELETE', '/api/v1/organizations', 'Delete all organizations'),

        ('POST', '/api/v1/specialists', 'Create specialist'),
        ('GET', '/api/v1/specialists', 'Get all specialists'),
        ('GET', '/api/v1/specialists/{id}', 'Get specialist by id'),
        ('GET', '/api/v1/specialists/{specialistUserId}/organizations', 'Get specialist organizations'),
        ('DELETE', '/api/v1/specialists/{id}', 'Delete specialist by id'),
        ('DELETE', '/api/v1/specialists', 'Delete all specialists'),

        ('POST', '/api/v1/slots', 'Create slot'),
        ('GET', '/api/v1/slots', 'Get all slots'),
        ('GET', '/api/v1/slots/{id}', 'Get slot by id'),
        ('DELETE', '/api/v1/slots/{id}', 'Delete slot by id'),
        ('DELETE', '/api/v1/slots', 'Delete all slots'),

        ('POST', '/api/v1/slot-booking-requests', 'Create slot booking request'),
        ('GET', '/api/v1/slot-booking-requests/{id}', 'Get slot booking request by id'),
        ('GET', '/api/v1/slot-booking-requests/me', 'Get current customer slot booking requests'),
        ('GET', '/api/v1/slot-booking-requests/incoming', 'Get incoming slot booking requests'),
        ('GET', '/api/v1/slot-booking-requests/slots/{slotId}', 'Get slot booking requests by slot'),
        ('POST', '/api/v1/slot-booking-requests/{id}/accept', 'Accept slot booking request'),
        ('POST', '/api/v1/slot-booking-requests/{id}/reject', 'Reject slot booking request'),
        ('POST', '/api/v1/slot-booking-requests/{id}/cancel', 'Cancel slot booking request')
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

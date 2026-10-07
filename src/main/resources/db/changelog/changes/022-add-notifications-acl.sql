-- liquibase formatted sql

-- changeset dmitriyBreyfogel:036-add-notification-acl

INSERT INTO permissions (code, description)
VALUES
    ('VIEW_OWN_NOTIFICATIONS', 'Can view own notifications'),
    ('MARK_OWN_NOTIFICATIONS_READ', 'Can mark own notifications as read')
    ON CONFLICT (code) DO NOTHING;

INSERT INTO resources (http_method, url_pattern, description)
SELECT candidate.http_method, candidate.url_pattern, candidate.description
FROM (VALUES
          ('GET',  '/api/v1/me/notifications', 'Get own notifications'),
          ('GET',  '/api/v1/me/notifications/unread', 'Get own unread notifications'),
          ('GET',  '/api/v1/me/notifications/unread/count', 'Count own unread notifications'),
          ('POST', '/api/v1/me/notifications/{notificationId}/read', 'Mark own notification as read'),
          ('POST', '/api/v1/me/notifications/read-all', 'Mark all own notifications as read')
     ) AS candidate(http_method, url_pattern, description)
WHERE NOT EXISTS (
    SELECT 1
    FROM resources existing
    WHERE existing.http_method = candidate.http_method
      AND existing.url_pattern = candidate.url_pattern
);

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name IN ('ADMIN', 'MODERATOR', 'USER')
  AND p.code IN ('VIEW_OWN_NOTIFICATIONS', 'MARK_OWN_NOTIFICATIONS_READ')
    ON CONFLICT DO NOTHING;

INSERT INTO permission_resources (permission_id, resource_id)
SELECT p.id, r.id
FROM (VALUES
          ('VIEW_OWN_NOTIFICATIONS', 'GET',  '/api/v1/me/notifications'),
          ('VIEW_OWN_NOTIFICATIONS', 'GET',  '/api/v1/me/notifications/unread'),
          ('VIEW_OWN_NOTIFICATIONS', 'GET',  '/api/v1/me/notifications/unread/count'),
          ('MARK_OWN_NOTIFICATIONS_READ', 'POST', '/api/v1/me/notifications/{notificationId}/read'),
          ('MARK_OWN_NOTIFICATIONS_READ', 'POST', '/api/v1/me/notifications/read-all')
     ) AS link(permission_code, http_method, url_pattern)
         JOIN permissions p ON p.code = link.permission_code
         JOIN resources r
              ON r.http_method = link.http_method
                  AND r.url_pattern = link.url_pattern
    ON CONFLICT DO NOTHING;
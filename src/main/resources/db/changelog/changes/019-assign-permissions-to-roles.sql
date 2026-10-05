-- liquibase formatted sql

-- changeset dmitriyBreyfogel:033-assign-permissions-to-roles

-- ADMIN: все права
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p WHERE r.name = 'ADMIN';

-- MODERATOR: управление слотами, заявками, специалистами, просмотр организаций
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'MODERATOR' AND p.code IN (
    'CREATE_OWN_SPECIALIST', 'VIEW_ANY_SPECIALIST', 'VIEW_SPECIALIST_ORGANIZATIONS',
    'CREATE_SLOT', 'VIEW_SLOT', 'VIEW_ANY_SLOTS', 'MANAGE_ANY_SLOTS',
    'CREATE_BOOKING', 'VIEW_OWN_BOOKINGS', 'VIEW_ANY_BOOKINGS',
    'ACCEPT_BOOKING', 'REJECT_BOOKING', 'CANCEL_OWN_BOOKING',
    'CREATE_OWN_ORGANIZATION', 'VIEW_OWN_ORGANIZATIONS', 'VIEW_ORGANIZATIONS_FOR_BOOKING',
    'MANAGE_OWN_ORGANIZATION', 'VIEW_ORG_SPECIALISTS'
);

-- USER: только запись, просмотр своих данных и создание специалиста
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'USER' AND p.code IN (
    'CREATE_OWN_SPECIALIST', 'VIEW_SPECIALIST_ORGANIZATIONS',
    'VIEW_SLOT', 'VIEW_ORGANIZATIONS_FOR_BOOKING',
    'CREATE_BOOKING', 'VIEW_OWN_BOOKINGS', 'CANCEL_OWN_BOOKING',
    'CREATE_OWN_ORGANIZATION', 'VIEW_OWN_ORGANIZATIONS', 'VIEW_ORG_SPECIALISTS',
    'MANAGE_OWN_ORGANIZATION'
);
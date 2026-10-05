-- formatted liquibase sql

-- changeset dmitriyBreyfogel:028-assign-admin-role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@slotum.com' AND r.name = 'ADMIN';

-- changeset dmitriyBreyfogel:029-assign-moderator-role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'moderator@slotum.com' AND r.name = 'MODERATOR';

--changeset dmitriyBreyfogel:030-assign-user-role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'user@slotum.com' AND r.name = 'USER';
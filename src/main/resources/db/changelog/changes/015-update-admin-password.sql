-- liquibase formatted sql

-- changeset dmitriyBreyfogel:027-update-admin-password
UPDATE users SET password = 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk='
WHERE email = 'admin@slotum.com';
-- liquibase formatted sql

-- changeset dmitriyBreyfogel:025-add-roles
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Administrator with full access'),
    ('MODERATOR', 'Specialist who manages own slots and bookings'),
    ('USER', 'Regular user who can book slots');
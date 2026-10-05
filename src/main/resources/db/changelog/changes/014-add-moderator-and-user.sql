-- liquibase formatted sql

-- changeset dmitriyBreyfogel:026-add-moderator-and-user
INSERT INTO users (surname, first_name, second_name, email, password, phone) VALUES
    ('Moderator', 'Moderator', 'Moderator', 'moderator@slotum.com', 'TIQlsXQFPqaTWynCsOCqTi6rGgG3hOaskbi9zpwmI1o=', '88888888888'),
    ('User', 'User', 'User', 'user@slotum.com', 'gxwjeSjmISvtqkRRpRSs4xdFYvZ2H2oVei/lCCs24vs=', '87777777777')
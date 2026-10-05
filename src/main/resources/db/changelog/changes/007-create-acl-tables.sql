-- liquibase formatted sql

-- changeset dmitriyBreyfogel:014-create-roles-table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- changeset dmitriyBreyfogel:015-create-permissions-table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

-- changeset dmitriyBreyfogel:016-create-resources-table
CREATE TABLE resources (
    id BIGSERIAL PRIMARY KEY,
    http_method VARCHAR(10) NOT NULL,
    url_pattern VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

-- changeset dmitriyBreyfogel:017-create-role-permissions-table
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

-- changeset dmitriyBreyfogel:018-create-permission-resources-table
CREATE TABLE permission_resources (
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    resource_id BIGINT NOT NULL REFERENCES resources(id),
    PRIMARY KEY (permission_id, resource_id)
);

-- changeset dmitriyBreyfogel:019-create-user-roles-table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
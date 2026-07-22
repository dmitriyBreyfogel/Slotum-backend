-- liquibase formatted sql

-- changeset dmitriyBreyfogel:021-set-organization-grade-default
ALTER TABLE organizations ALTER COLUMN grade SET DEFAULT 0.0;
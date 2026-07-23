-- liquibase formatted sql

-- changeset dmitriyBreyfogel:022-set-default-specialist-grade.sql
ALTER TABLE specialists ALTER COLUMN grade SET DEFAULT 0.0;
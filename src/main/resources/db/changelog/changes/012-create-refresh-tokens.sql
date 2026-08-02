-- liquibase formatted sql

-- changeset dmitriyBreyfogel:024-create-refresh-tokens.sql
CREATE TABLE refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL REFERENCES users(id),
                                token_hash VARCHAR(255) NOT NULL UNIQUE,
                                issued_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                expires_at TIMESTAMP NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
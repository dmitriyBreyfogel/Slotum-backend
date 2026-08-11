-- formatted liquibase sql

-- changeset dmitriyBreyfogel:030-insert-refresh-token-api-resources
INSERT INTO resources (http_method, url_pattern, description) VALUES
    ('GET', '/api/v1/refreshTokens/{id}', 'Get refresh token by id'),
    ('GET', '/api/v1/refreshTokens/by-hash/{hash}', 'Get refresh token by id'),
    ('GET', '/api/v1/refreshTokens', 'Get all refresh tokens'),
    ('GET', '/api/v1/refreshTokens/by-user', 'Get user refresh tokens'),
    ('GET', '/api/v1/refreshTokens/count', 'Count refresh tokens'),
    ('GET', '/api/v1/refreshTokens/count/{revoked}', 'Count refresh tokens by revoked flag'),
    ('POST', '/api/v1/refreshTokens/revoke/{hash}', 'Revoke token by hash'),
    ('POST', '/api/v1/refreshTokens/users/{userId}/revoke', 'Revoke user tokens'),
    ('DELETE', '/api/v1/refreshTokens/cleanup', 'Cleanup tokens');
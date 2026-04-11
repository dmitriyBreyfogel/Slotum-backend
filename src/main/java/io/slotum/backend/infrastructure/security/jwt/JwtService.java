package io.slotum.backend.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Service
public class JwtService {
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey secretKey;
    private final Duration accessTtl;

    public JwtService(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.accessTtl}") Duration accessTtl
    ) {
        this.secretKey = parseKey(base64Secret);
        this.accessTtl = validateAccessTtl(accessTtl);
    }

    public String issueAccessToken(long userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTtl);

        return Jwts.builder()
                .subject(Long.toString(userId))
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public JwtPayload parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);

        Instant issuedAt = claims.getIssuedAt() == null ? null : claims.getIssuedAt().toInstant();
        Instant expiresAt = claims.getExpiration() == null ? null : claims.getExpiration().toInstant();

        return new JwtPayload(userId, email, issuedAt, expiresAt);
    }

    private static SecretKey parseKey(String base64Secret) {
        if (base64Secret == null || base64Secret.isBlank() || Objects.equals(base64Secret, "CHANGE_ME")) {
            throw new IllegalStateException(
                    "JWT secret is not configured. Set app.jwt.secret or env JWT_SECRET to a Base64-encoded key."
            );
        }

        try {
            byte[] secretBytes = Decoders.BASE64.decode(base64Secret);
            if (secretBytes.length < MIN_SECRET_BYTES) {
                throw new IllegalStateException(
                        "JWT secret is too short. Use at least 32 random bytes before Base64 encoding."
                );
            }
            return Keys.hmacShaKeyFor(secretBytes);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Invalid JWT secret. Set app.jwt.secret (or env JWT_SECRET) to a Base64-encoded key (>= 32 bytes).",
                    ex
            );
        }
    }

    private static Duration validateAccessTtl(Duration accessTtl) {
        if (accessTtl == null || accessTtl.isZero() || accessTtl.isNegative()) {
            throw new IllegalStateException("JWT access token TTL must be positive.");
        }
        return accessTtl;
    }

    public record JwtPayload(
            long userId,
            String email,
            Instant issuedAt,
            Instant expiresAt
    ) {
    }
}

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

@Service
public class JwtService {
    private final String base64Secret;
    private final Duration accessTtl;

    public JwtService(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.accessTtl}") Duration accessTtl
    ) {
        this.base64Secret = base64Secret;
        this.accessTtl = accessTtl;
    }

    public String issueAccessToken(long userId, String email) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTtl);

        return Jwts.builder()
                .subject(Long.toString(userId))
                .claim("email", email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    public JwtPayload parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long userId = Long.parseLong(claims.getSubject());
        String email = claims.get("email", String.class);

        Instant issuedAt = claims.getIssuedAt() == null ? null : claims.getIssuedAt().toInstant();
        Instant expiresAt = claims.getExpiration() == null ? null : claims.getExpiration().toInstant();

        return new JwtPayload(userId, email, issuedAt, expiresAt);
    }

    private SecretKey key() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "Invalid JWT secret. Set app.jwt.secret (or env JWT_SECRET) to a Base64-encoded key (>= 32 bytes).",
                    ex
            );
        }
    }

    public record JwtPayload(
            long userId,
            String email,
            Instant issuedAt,
            Instant expiresAt
    ) {
    }
}


package io.slotum.backend.infrastructure.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final Duration accessTtl;

    public JwtService(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.accessTtl}") Duration accessTtl
    ) {
        this.secretKey = parseKey(base64Secret);
        this.accessTtl = accessTtl;
    }

    public String issueAccessToken(long userId, String email, Set<String> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTtl);

        return Jwts.builder()
                .subject(Long.toString(userId))
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private static SecretKey parseKey(String base64Secret) {
        byte[] secretBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
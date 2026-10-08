package io.slotum.backend.infrastructure.security.jwt;

import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.util.Collection;
import java.util.Map;

public final class CustomJwtAuthenticationToken
        extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

    private final String name;

    public CustomJwtAuthenticationToken(
            Jwt jwt,
            AuthenticatedUser principal,
            Collection<? extends GrantedAuthority> authorities
    ) {
        super(jwt, principal, jwt, authorities);
        this.name = principal.email();
        setAuthenticated(true);
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return getToken().getClaims();
    }

    @Override
    public String getName() {
        return name;
    }
}

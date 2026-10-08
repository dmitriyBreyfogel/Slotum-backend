package io.slotum.backend.infrastructure.security.jwt;

import io.slotum.backend.infrastructure.security.AuthenticatedUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CustomJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter rolesConverter;
    private final JwtGrantedAuthoritiesConverter permissionsConverter;

    public CustomJwtAuthenticationConverter() {
        rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles");
        rolesConverter.setAuthorityPrefix("ROLE_");

        permissionsConverter = new JwtGrantedAuthoritiesConverter();
        permissionsConverter.setAuthoritiesClaimName("permissions");
        permissionsConverter.setAuthorityPrefix("");
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        long userId = Long.parseLong(jwt.getSubject());
        String email = jwt.getClaimAsString("email");

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, email);

        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.addAll(rolesConverter.convert(jwt));
        authorities.addAll(permissionsConverter.convert(jwt));

        return new CustomJwtAuthenticationToken(
                jwt,
                authenticatedUser,
                authorities
        );
    }
}

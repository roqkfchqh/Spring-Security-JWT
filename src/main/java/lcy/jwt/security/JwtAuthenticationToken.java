package lcy.jwt.security;

import lcy.jwt.dto.AuthUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final AuthUser authUser;

    public JwtAuthenticationToken(AuthUser authUser) {
        super(authUser.authorities());
        this.authUser = authUser;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authUser;
    }
}

package lcy.jwt.config;

import lcy.jwt.dto.AuthUser;
import lcy.jwt.security.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class TestSecurityContextFactory implements WithSecurityContextFactory<MockAuthUser> {

    @Override
    public SecurityContext createSecurityContext(MockAuthUser mockAuthUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        AuthUser authUser = AuthUser.of(mockAuthUser.userId(), mockAuthUser.role());
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
        context.setAuthentication(authenticationToken);
        return context;
    }
}

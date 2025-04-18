package lcy.jwt.security;

import lcy.jwt.domain.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

public record AuthUser(
        Long userId,
        Collection<? extends GrantedAuthority> authorities
) {
    public static AuthUser of(Long userId, UserRole role) {
        return new AuthUser(
                userId,
                List.of(new SimpleGrantedAuthority(role.getRoleName()))
        );
    }
}

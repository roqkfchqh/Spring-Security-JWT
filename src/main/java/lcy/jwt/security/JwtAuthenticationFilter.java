package lcy.jwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.AuthUser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lcy.jwt.utils.JwtProperties;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return jwtProperties.secret().whiteList().contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith(jwtProperties.token().prefix())) {
            String token = jwtUtil.substringToken(authorizationHeader);
            try {
                Claims claims = jwtUtil.extractClaims(token);

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthentication(claims);
                }
            } catch (ExpiredJwtException e) {
                log.error("Expired JWT token.", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT token 입니다.");
            } catch (SecurityException | MalformedJwtException e) {
                log.error("Invalid JWT signature.", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 서명 입니다.");
            } catch (UnsupportedJwtException e) {
                log.error("Unsupported JWT token.", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT token 입니다.");
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (Exception e) {
                log.error("예상치 못한 예외 발생", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }

    private void setAuthentication(Claims claims) {
        String[] data = claims.getSubject().split(":");
        Long userId = Long.valueOf(data[0]);
        UserRole userRole = UserRole.of(data[1]);

        AuthUser authUser = AuthUser.of(userId, userRole);
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(authUser);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}

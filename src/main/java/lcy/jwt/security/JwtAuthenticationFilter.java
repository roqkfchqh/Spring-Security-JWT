package lcy.jwt.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lcy.jwt.domain.UserRole;
import lcy.jwt.dto.AuthUser;
import lcy.jwt.exception.ErrorResponseHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lcy.jwt.utils.JwtProperties;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final ErrorResponseHandler errorResponseHandler;
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
                errorResponseHandler.send(response, HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.");
                return;
            } catch (SignatureException e) {
                errorResponseHandler.send(response, HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다.");
                return;
            } catch (SecurityException | MalformedJwtException e) {
                errorResponseHandler.send(response, HttpStatus.UNAUTHORIZED, "잘못된 JWT 토큰 형식입니다.");
                return;
            } catch (UnsupportedJwtException e) {
                errorResponseHandler.send(response, HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
                return;
            } catch (IllegalArgumentException e) {
                errorResponseHandler.send(response, HttpStatus.BAD_REQUEST, e.getMessage());
                return;
            } catch (JwtException e) {
                errorResponseHandler.send(response, HttpStatus.UNAUTHORIZED, "예상치 못한 JWT 토큰 오류: " + e.getMessage());
                return;
            } catch (Exception e) {
                errorResponseHandler.send(response, HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 서버 오류: " + e.getMessage());
                return;
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

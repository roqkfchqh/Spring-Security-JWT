package lcy.jwt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lcy.jwt.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import lcy.jwt.utils.JwtProperties;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        String secretKey = jwtProperties.secret().key();
        if (!StringUtils.hasText(secretKey)) {
            log.error("JWT secret key 가 비어있습니다.");
            throw new IllegalArgumentException("JWT secret key 가 비어있습니다.");
        }
        try {
            byte[] bytes = Base64.getDecoder().decode(secretKey);
            key = Keys.hmacShaKeyFor(bytes);
        } catch (IllegalArgumentException e) {
            log.error("Failed to decode JWT secret key: {}", e.getMessage());
            throw new IllegalArgumentException("JWT secret key 가 올바르지 않습니다.");
        }
    }

    public String createToken(Long userId, UserRole role) {
        Date now = new Date();
        String payload = userId + ":" + role;
        return jwtProperties.token().prefix() +
                Jwts.builder()
                        .setSubject(payload)
                        .setExpiration(new Date(now.getTime() + jwtProperties.token().expiration()))
                        .setIssuedAt(now)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    public String substringToken(String token) {
        String prefix = jwtProperties.token().prefix();
        if (StringUtils.hasText(token) && token.startsWith(prefix)) {
            return token.substring(prefix.length()).trim();
        }
        throw new IllegalArgumentException("Jwt token 을 찾을 수 없습니다.");
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

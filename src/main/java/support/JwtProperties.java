package support;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        Secret secret,
        Token token
) {
    public record Secret(
            String key,
            List<String> whiteList,
            List<String> adminList
    ) {}

    public record Token(
            String prefix,
            long expiration
    ) {}
}

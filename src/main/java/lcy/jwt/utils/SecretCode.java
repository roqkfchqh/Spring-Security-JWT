package lcy.jwt.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public record SecretCode(
        String code
) {
    public SecretCode() {
        this(UUID.randomUUID().toString());
    }

    @PostConstruct
    public void init() {
        log.info("Admin 용 Secret Code: {}", code);
    }
}

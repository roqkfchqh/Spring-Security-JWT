package lcy.jwt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("과제")
                .version("v1.0")
                .description("API 명세서")
                .contact(new Contact()
                    .name("이채영")
                    .url("https://github.com/roqkfchqh/Spring-Security-JWT"));

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearer-key");

        return new OpenAPI()
                .info(info)
                .tags(List.of(
                        new Tag().name("Auth").description("인증·인가 관련 API"),
                        new Tag().name("Admin").description("관리자 API")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearer-key", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}

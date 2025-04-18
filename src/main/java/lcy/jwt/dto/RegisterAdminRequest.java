package lcy.jwt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterAdminRequest(
        @NotBlank @Size(min = 1, max = 10) String username,
        @NotBlank @Size(min = 1, max = 10) String nickname,
        @NotBlank @Size(min = 8, max = 20) String password,
        String secretCode
) {
}
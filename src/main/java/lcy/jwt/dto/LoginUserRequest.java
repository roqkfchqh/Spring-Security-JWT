package lcy.jwt.dto;

public record LoginUserRequest(
        String username,
        String password
) {
}

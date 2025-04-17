package lcy.jwt.dto;

import lcy.jwt.domain.UserRole;

public record RegisterUserResponse(
        Long id,
        String username,
        String nickname,
        String role
) {
    public static RegisterUserResponse of(
            Long id,
            RegisterUserRequest request
    ) {
        return new RegisterUserResponse(
                id,
                request.username(),
                request.nickname(),
                UserRole.USER.toString()
        );
    }
}

package lcy.jwt.dto;

import lcy.jwt.domain.User;

public record UserResponse(
        Long id,
        String username,
        String nickname,
        String role
) {
    public static UserResponse of(
            User user
    ) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getRole().toString()
        );
    }
}

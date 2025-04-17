package lcy.jwt.dto;

public record LoginUserResponse(
        String token
) {
    public static LoginUserResponse of(
            String token
    ) {
        return new LoginUserResponse(
                token
        );
    }
}

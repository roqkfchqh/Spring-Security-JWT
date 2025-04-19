package lcy.jwt.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //auth
    USERNAME_ALREADY_USED(HttpStatus.BAD_REQUEST, "au400", "이미 사용중인 username 입니다."),
    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "au401", "패스워드가 일치하지 않습니다."),

    //user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "us404", "존재하지 않는 사용자입니다."),

    //admin
    ALREADY_ADMIN(HttpStatus.BAD_REQUEST, "ad400", "해당 유저는 이미 관리자입니다."),
    ADMIN_CODE_FORBIDDEN(HttpStatus.FORBIDDEN, "ad403", "관리자 코드가 잘못되었습니다."),
    ;
    private final HttpStatus status;
    private final String code;
    private final String message;
}

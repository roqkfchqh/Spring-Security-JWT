package lcy.jwt.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {
    private final String message;
    @Getter
    private final String code;
    private final HttpStatus httpStatus;

    protected BaseException(String message, String code, HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public HttpStatus getStatus(){
        return httpStatus;
    }

    @Override
    public String getMessage(){
        return message;
    }
}

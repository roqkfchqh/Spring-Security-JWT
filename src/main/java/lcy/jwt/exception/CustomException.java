package lcy.jwt.exception;

public class CustomException extends BaseException {
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getCode(), errorCode.getStatus());
    }
}

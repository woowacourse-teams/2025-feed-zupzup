package feedzupzup.backend.global.exception;

import feedzupzup.backend.global.response.ErrorCode;
import lombok.Getter;

@Getter
public abstract class CustomGlobalException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    protected CustomGlobalException(final ErrorCode errorCode, final String message) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }
}

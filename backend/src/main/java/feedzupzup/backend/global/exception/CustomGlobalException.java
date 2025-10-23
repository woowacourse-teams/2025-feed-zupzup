package feedzupzup.backend.global.exception;

import feedzupzup.backend.global.response.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class CustomGlobalException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    protected CustomGlobalException(final ErrorCode errorCode, final String message) {
        super(message);
        this.message = message;
        this.errorCode = errorCode;
    }

    protected CustomGlobalException(final ErrorCode errorCode, final String message, final Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}

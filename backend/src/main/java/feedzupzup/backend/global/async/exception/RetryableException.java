package feedzupzup.backend.global.async.exception;

import lombok.Getter;

@Getter
public class RetryableException extends RuntimeException {

    public RetryableException(final String message) {
        super(message);
    }

    public RetryableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

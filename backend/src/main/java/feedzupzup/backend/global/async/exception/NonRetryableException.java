package feedzupzup.backend.global.async.exception;

import lombok.Getter;

@Getter
public class NonRetryableException extends RuntimeException {

    public NonRetryableException(final String message) {
        super(message);
    }
}

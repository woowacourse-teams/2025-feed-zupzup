package feedzupzup.backend.auth.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class AuthException extends DomainException {

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

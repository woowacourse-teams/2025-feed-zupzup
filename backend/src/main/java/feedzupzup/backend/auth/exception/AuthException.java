package feedzupzup.backend.auth.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class AuthException extends DomainException {

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static final class InvalidPasswordException extends AuthException {

        private static final ErrorCode errorCode = ErrorCode.INVALID_LOGIN_CREDENTIALS;

        public InvalidPasswordException(String message) {
            super(errorCode, message);
        }
    }

    public static final class DuplicateLoginIdException extends AuthException {

        private static final ErrorCode errorCode = ErrorCode.DUPLICATE_LOGIN_ID;

        public DuplicateLoginIdException(String message) {
            super(errorCode, message);
        }
    }

    public static final class PasswordNotMatchException extends AuthException {

        private static final ErrorCode errorCode = ErrorCode.PASSWORD_NOT_MATCH;

        public PasswordNotMatchException(String message) {
            super(errorCode, message);
        }
    }
}

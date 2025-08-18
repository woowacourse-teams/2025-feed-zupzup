package feedzupzup.backend.global.exception;

import feedzupzup.backend.global.response.ErrorCode;

public class UnAuthorizeException extends CustomGlobalException{

    protected UnAuthorizeException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static final class InvalidAuthorizeException extends UnAuthorizeException {

        private static final ErrorCode errorCode = ErrorCode.INVALID_AUTHORIZE;

        public InvalidAuthorizeException(final String message) {
            super(errorCode, message);
        }

        public InvalidAuthorizeException() {
            super(errorCode, errorCode.getMessage());
        }
    }

}

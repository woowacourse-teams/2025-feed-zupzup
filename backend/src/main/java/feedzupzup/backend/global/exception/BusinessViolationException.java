package feedzupzup.backend.global.exception;

import feedzupzup.backend.global.response.ErrorCode;

public class BusinessViolationException extends CustomGlobalException {

    protected BusinessViolationException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static final class NotSupportedException extends BusinessViolationException {

        private static final ErrorCode errorCode = ErrorCode.NOT_SUPPORTED;

        public NotSupportedException(final String message) {
            super(errorCode, message);
        }

        public NotSupportedException() {
            super(errorCode, errorCode.getMessage());
        }
    }
}

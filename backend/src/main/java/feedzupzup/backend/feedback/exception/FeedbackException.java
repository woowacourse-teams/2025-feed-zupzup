package feedzupzup.backend.feedback.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class FeedbackException extends DomainException {

    public FeedbackException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static class FeedbackNegativeException extends FeedbackException {

        private static final ErrorCode errorCode = ErrorCode.INVALID_VALUE_RANGE;

        public FeedbackNegativeException(final String message) {
            super(errorCode, message);
        }
    }

    public static class DuplicateLikeException extends FeedbackException {

        private static final ErrorCode errorCode = ErrorCode.LIKE_ALREADY_EXISTS;

        public DuplicateLikeException(final String message) {
            super(errorCode, message);
        }
    }
}

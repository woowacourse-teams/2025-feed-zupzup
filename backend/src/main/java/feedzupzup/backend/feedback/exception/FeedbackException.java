package feedzupzup.backend.feedback.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class FeedbackException extends DomainException {

    public FeedbackException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}

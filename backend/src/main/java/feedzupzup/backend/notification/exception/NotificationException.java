package feedzupzup.backend.notification.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class NotificationException extends DomainException {

    protected NotificationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static final class RetryInterruptedException extends NotificationException {

        private static final ErrorCode errorCode = ErrorCode.NOTIFICATION_RETRY_INTERRUPTED;

        public RetryInterruptedException(String message) {
            super(errorCode, message);
        }
    }
}

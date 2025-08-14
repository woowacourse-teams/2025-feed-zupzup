package feedzupzup.backend.notification.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class NotificationException extends DomainException {

    protected NotificationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static final class FirebaseInitializationFailedException extends NotificationException {

        private static final ErrorCode errorCode = ErrorCode.FIREBASE_INITIALIZATION_FAILED;

        public FirebaseInitializationFailedException(String message) {
            super(errorCode, message);
        }
    }
}

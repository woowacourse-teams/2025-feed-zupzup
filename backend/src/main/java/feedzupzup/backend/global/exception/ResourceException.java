package feedzupzup.backend.global.exception;

import feedzupzup.backend.global.response.ErrorCode;

public class ResourceException extends CustomGlobalException {

    protected ResourceException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static final class ResourceNotFoundException extends ResourceException {

        private static final ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;

        public ResourceNotFoundException(final String message) {
            super(errorCode, message);
        }

        public ResourceNotFoundException() {
            super(errorCode, errorCode.getMessage());
        }
    }

    public static final class ResourceExistsException extends ResourceException {

        private static final ErrorCode errorCode = ErrorCode.RESOURCE_EXISTS;

        public ResourceExistsException(final String message) {
            super(errorCode, message);
        }

        public ResourceExistsException() {
            super(errorCode, errorCode.getMessage());
        }
    }
}

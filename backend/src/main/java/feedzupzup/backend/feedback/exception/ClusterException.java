package feedzupzup.backend.feedback.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class ClusterException extends DomainException {

    protected ClusterException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static class EmptyClusteringContentException extends ClusterException {

        private static final ErrorCode errorCode = ErrorCode.INVALID_VALUE_RANGE;

        public EmptyClusteringContentException(final String message) {
            super(errorCode, message);
        }

        public EmptyClusteringContentException() {
            super(errorCode, errorCode.getMessage());
        }
    }
}

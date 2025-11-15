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

    public static class InvalidLikeException extends FeedbackException {

        private static final ErrorCode errorCode = ErrorCode.INVALID_LIKE_REQUEST;

        public InvalidLikeException(final String message) {
            super(errorCode, message);
        }
    }

    public static class AlreadyClusteringException extends FeedbackException {

        private static final ErrorCode errorCode = ErrorCode.ALREADY_CLUSTERING_FEEDBACK;

        public AlreadyClusteringException(final String message) {
            super(errorCode, message);
        }
    }

    public static class InvalidVectorDimensionException extends FeedbackException {

        private static final ErrorCode errorCode = ErrorCode.INVALID_VECTOR_DIMENSION;

        public InvalidVectorDimensionException(final String message) {
            super(errorCode, message);
        }
    }

    public static class FeedbackDownloadException extends FeedbackException {

        private static final ErrorCode errorCode = ErrorCode.FEEDBACK_DOWNLOAD_ERROR;

        public FeedbackDownloadException(final String message) {
            super(errorCode, message);
        }
    }

    public static class DownloadJobNotCompletedException extends FeedbackException {

        private static final ErrorCode errorCode = ErrorCode.DOWNLOAD_JOB_NOT_COMPLETED;

        public DownloadJobNotCompletedException(final String message) {
            super(errorCode, message);
        }
    }

    public static class DownloadUrlNotGeneratedException extends FeedbackException {

        private static final ErrorCode errorCode = ErrorCode.DOWNLOAD_URL_NOT_GENERATED;

        public DownloadUrlNotGeneratedException(final String message) {
            super(errorCode, message);
        }
    }
}

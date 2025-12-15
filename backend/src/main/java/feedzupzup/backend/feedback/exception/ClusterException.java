package feedzupzup.backend.feedback.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class ClusterException extends DomainException {

    protected ClusterException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static class EmptyClusteringContentException extends ClusterException {

        private static final ErrorCode errorCode = ErrorCode.EMPTY_CLUSTERING_CONTENT;

        public EmptyClusteringContentException(final String message) {
            super(errorCode, message);
        }

        public EmptyClusteringContentException() {
            super(errorCode, errorCode.getMessage());
        }
    }

    public static class EmbeddingExtractionFailedException extends ClusterException {

        private static final ErrorCode errorCode = ErrorCode.EMBEDDING_EXTRACTION_FAILED;

        public EmbeddingExtractionFailedException(final String message) {
            super(errorCode, message);
        }

        public EmbeddingExtractionFailedException(final String message, final Throwable cause) {
            super(errorCode, message);
            initCause(cause);
        }
    }

    public static class VoyageRetryFailedException extends ClusterException {

        private static final ErrorCode errorCode = ErrorCode.VOYAGE_RETRY_FAILED;

        public VoyageRetryFailedException(final String message) {
            super(errorCode, message);
        }

        public VoyageRetryFailedException(final String message, final Throwable cause) {
            super(errorCode, message);
            initCause(cause);
        }
    }
}

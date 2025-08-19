package feedzupzup.backend.s3.exception;

import feedzupzup.backend.global.response.ErrorCode;

public class S3PresignedException extends RuntimeException {

    private static final ErrorCode errorCode = ErrorCode.S3_UPLOAD_FAILED;

    public S3PresignedException(final String message) {
        super(message);
    }

    public S3PresignedException() {
        super(errorCode.getMessage());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}

package feedzupzup.backend.s3.exception;

import feedzupzup.backend.global.response.ErrorCode;

public class S3UploadException extends RuntimeException {

    private static final ErrorCode errorCode = ErrorCode.S3_UPLOAD_FAILED;

    public S3UploadException(final String message) {
        super(message);
    }

    public S3UploadException() {
        super(errorCode.getMessage());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

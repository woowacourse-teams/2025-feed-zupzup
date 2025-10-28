package feedzupzup.backend.s3.exception;

import feedzupzup.backend.global.response.ErrorCode;

public class S3DownloadException extends RuntimeException {

    private static final ErrorCode errorCode = ErrorCode.S3_DOWNLOAD_FAILED;

    public S3DownloadException(final String message) {
        super(message);
    }

    public S3DownloadException() {
        super(errorCode.getMessage());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
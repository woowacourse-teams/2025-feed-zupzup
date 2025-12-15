package feedzupzup.backend.s3.exception;

import feedzupzup.backend.global.exception.InfrastructureException;
import feedzupzup.backend.global.response.ErrorCode;

public class S3DownloadException extends InfrastructureException {

    private static final ErrorCode errorCode = ErrorCode.S3_DOWNLOAD_FAILED;

    public S3DownloadException(final String message) {
        super(errorCode, message);
    }
}

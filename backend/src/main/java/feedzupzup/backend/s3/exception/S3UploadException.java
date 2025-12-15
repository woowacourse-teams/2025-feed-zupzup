package feedzupzup.backend.s3.exception;

import feedzupzup.backend.global.exception.InfrastructureException;
import feedzupzup.backend.global.response.ErrorCode;

public class S3UploadException extends InfrastructureException {

    private static final ErrorCode errorCode = ErrorCode.S3_UPLOAD_FAILED;

    public S3UploadException(final String message) {
        super(errorCode, message);
    }
}

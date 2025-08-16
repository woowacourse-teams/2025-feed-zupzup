package feedzupzup.backend.qr.infrastructure.exception;

import feedzupzup.backend.global.response.ErrorCode;

public class QRGenerationException extends RuntimeException {

    private static final ErrorCode errorCode = ErrorCode.QR_GENERATION_FAILED;

    public QRGenerationException(final String message) {
        super(message);
    }

    public QRGenerationException() {
        super(errorCode.getMessage());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

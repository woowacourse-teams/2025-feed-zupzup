package feedzupzup.backend.qr.domain.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class QRException extends DomainException {

    protected QRException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static final class QRGenerationException extends QRException {

        private static final ErrorCode errorCode = ErrorCode.QR_GENERATION_FAILED;

        public QRGenerationException(String message) {
            super(errorCode, message);
        }
    }
}

package feedzupzup.backend.global.exception;

import feedzupzup.backend.global.response.ErrorCode;

public class InfrastructureException extends CustomGlobalException {

    protected InfrastructureException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public InfrastructureException(final ErrorCode errorCode, final String message, final Throwable throwable) {
        super(errorCode, message, throwable);
    }

    public static final class RestClientServerException extends InfrastructureException{

        private static final ErrorCode errorCode = ErrorCode.REST_CLIENT_SERVER_FAIL;

        public RestClientServerException(final String message, final Throwable throwable) {
            super(errorCode, message, throwable);
        }

        public RestClientServerException(final String message) {
            super(errorCode, message);
        }

        public RestClientServerException() {
            super(errorCode, errorCode.getMessage());
        }
    }

    public static final class PoiExcelExportException extends InfrastructureException {

        private static final ErrorCode errorCode = ErrorCode.EXCEL_EXPORT_FAILED;

        public PoiExcelExportException(final String message, final Throwable throwable) {
            super(errorCode, message, throwable);
        }

        public PoiExcelExportException(final String message) {
            super(errorCode, message);
        }

        public PoiExcelExportException() {
            super(errorCode, errorCode.getMessage());
        }
    }
}

package feedzupzup.backend.admin.domain.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class AdminException extends DomainException {

    public AdminException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static final class InvalidAdminNameException extends AdminException {

        private static final ErrorCode errorcode = ErrorCode.INVALID_ADMIN_NAME_FORMAT;

        public InvalidAdminNameException(String message) {
            super(errorcode, message);
        }
    }

    public static final class InvalidAdminIdException extends AdminException {

        private static final ErrorCode errorcode = ErrorCode.INVALID_ADMIN_ID_FORMAT;

        public InvalidAdminIdException(String message) {
            super(errorcode, message);
        }
    }

    public static final class InvalidAdminPasswordException extends AdminException {

        private static final ErrorCode errorcode = ErrorCode.INVALID_PASSWORD_FORMAT;

        public InvalidAdminPasswordException(String message) {
            super(errorcode, message);
        }
    }
}

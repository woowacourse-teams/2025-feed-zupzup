package feedzupzup.backend.admin.exception;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;

public class AdminException extends DomainException {

    public AdminException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}

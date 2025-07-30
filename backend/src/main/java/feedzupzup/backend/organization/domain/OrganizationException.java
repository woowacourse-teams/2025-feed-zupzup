package feedzupzup.backend.organization.domain;

import feedzupzup.backend.global.exception.DomainException;
import feedzupzup.backend.global.response.ErrorCode;
import lombok.Getter;

@Getter
public class OrganizationException extends DomainException {


    protected OrganizationException(final ErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    public static class OrganizationNumberException extends OrganizationException {

        private static final ErrorCode errorCode = ErrorCode.CHEERING_INVALID_NUMBER;

        protected OrganizationNumberException(final String message) {
            super(errorCode, message);
        }
    }
}

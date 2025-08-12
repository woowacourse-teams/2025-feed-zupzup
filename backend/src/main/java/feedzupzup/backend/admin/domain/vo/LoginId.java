package feedzupzup.backend.admin.domain.vo;

import feedzupzup.backend.admin.exception.AdminException;
import feedzupzup.backend.global.response.ErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginId {

    private static final int MAX_LENGTH = 10;
    private static final String BLANK_SPACE = " ";

    private String value;

    public LoginId(final String value) {
        validateLength(value);
        validateFormat(value);
        this.value = value;
    }

    private void validateLength(final String value) {
        if(value.isEmpty() || value.length() > MAX_LENGTH)
            throw new AdminException(ErrorCode.INVALID_ADMIN_ID_FORMAT, "value = " + value + " length = " + value.length());
    }

    private void validateFormat(final String value) {
        if (value.contains(BLANK_SPACE)) {
            throw new AdminException(ErrorCode.INVALID_ADMIN_ID_FORMAT, "value = " + value + " contains whitespace");
        }
    }

}

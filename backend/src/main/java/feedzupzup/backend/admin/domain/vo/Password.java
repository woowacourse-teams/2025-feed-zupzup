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
public class Password {

    private static final int MIN_LENGTH = 5;
    private static final String BLANK_SPACE = " ";

    private String value;

    public Password(final String value) {
        validateLength(value);
        validateFormat(value);
        this.value = value;
    }

    private void validateLength(final String value) {
        if (value.length() < MIN_LENGTH) {
            throw new AdminException(ErrorCode.INVALID_PASSWORD_FORMAT, "value = " + value + " length = " + value.length());
        }
    }

    private void validateFormat(final String value) {
        if (value.contains(BLANK_SPACE)) {
            throw new AdminException(ErrorCode.INVALID_PASSWORD_FORMAT, "value = " + value + " contains whitespace");
        }
    }
}

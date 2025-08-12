package feedzupzup.backend.admin.domain.vo;

import feedzupzup.backend.admin.exception.AdminException;
import feedzupzup.backend.global.response.ErrorCode;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    private static final int MIN_LENGTH = 5;
    private static final String BLANK_SPACE = " ";
    private static final Pattern ALLOWED_PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?`~]+$");

    private String password;

    public Password(final String password) {
        validateLength(password);
        validateFormat(password);
        this.password = password;
    }

    private void validateLength(final String password) {
        if (password.length() < MIN_LENGTH) {
            throw new AdminException(
                    ErrorCode.INVALID_PASSWORD_FORMAT,
                    "password = " + password + " length = " + password.length()
            );
        }
    }

    private void validateFormat(final String password) {
        if (password.contains(BLANK_SPACE)) {
            throw new AdminException(
                    ErrorCode.INVALID_PASSWORD_FORMAT,
                    "password = " + password + " 공백이 포함 되어있습니다."
            );
        }
        if (!ALLOWED_PASSWORD_PATTERN.matcher(password).matches()) {
            throw new AdminException(
                    ErrorCode.INVALID_PASSWORD_FORMAT,
                    "password = " + password + " 은(는) 영어, 숫자, 특수문자만 포함해야 합니다."
            );
        }
    }

    public boolean matches(final String rawPassword) {
        return this.password.equals(rawPassword);
    }
}

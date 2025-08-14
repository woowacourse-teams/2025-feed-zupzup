package feedzupzup.backend.admin.domain.vo;

import feedzupzup.backend.admin.domain.exception.AdminException.InvalidAdminPasswordException;
import java.util.regex.Pattern;

public record Password(
        String value
) {

    private static final int MIN_LENGTH = 5;
    private static final String BLANK_SPACE = " ";
    // 영문 숫자와 특수문자만 가능
    private static final Pattern ALLOWED_PASSWORD_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9" +
                    "!@#$%^&*()_+\\-=\\[\\]{}" +
                    ";':\"\\\\|,.<>/?`~" +
                    "]+$"
    );

    public Password {
        validateLength(value);
        validateFormat(value);
    }

    private static void validateLength(final String password) {
        if (password.length() < MIN_LENGTH) {
            throw new InvalidAdminPasswordException("password = " + password + " length = " + password.length());
        }
    }

    private static void validateFormat(final String password) {
        if (password.contains(BLANK_SPACE)) {
            throw new InvalidAdminPasswordException("password = " + password + " 공백이 포함 되어있습니다.");
        }
        if (!ALLOWED_PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidAdminPasswordException("password = " + password + " 은(는) 영어, 숫자, 특수문자만 포함해야 합니다.");
        }
    }
}

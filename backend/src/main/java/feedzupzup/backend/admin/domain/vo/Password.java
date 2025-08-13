package feedzupzup.backend.admin.domain.vo;

import feedzupzup.backend.auth.encoder.PasswordEncoder;
import feedzupzup.backend.auth.exception.AuthException;
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
    // 영문 숫자와 특수문자만 가능
    private static final Pattern ALLOWED_PASSWORD_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9" +
                    "!@#$%^&*()_+\\-=\\[\\]{}" +
                    ";':\"\\\\|,.<>/?`~" +
                    "]+$"
    );

    private String password;

    public Password(String password) {
        this.password = password;
    }

    public static Password createEncodedPassword(final String rawPassword, final PasswordEncoder passwordEncoder) {
        validateLength(rawPassword);
        validateFormat(rawPassword);
        final String encodedPassword = passwordEncoder.encode(rawPassword);
        return new Password(encodedPassword);
    }

    public boolean matches(final String rawPassword, final PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, this.password);
    }

    private static void validateLength(final String password) {
        if (password.length() < MIN_LENGTH) {
            throw new AuthException(
                    ErrorCode.INVALID_PASSWORD_FORMAT,
                    "password = " + password + " length = " + password.length()
            );
        }
    }

    private static void validateFormat(final String password) {
        if (password.contains(BLANK_SPACE)) {
            throw new AuthException(
                    ErrorCode.INVALID_PASSWORD_FORMAT,
                    "password = " + password + " 공백이 포함 되어있습니다."
            );
        }
        if (!ALLOWED_PASSWORD_PATTERN.matcher(password).matches()) {
            throw new AuthException(
                    ErrorCode.INVALID_PASSWORD_FORMAT,
                    "password = " + password + " 은(는) 영어, 숫자, 특수문자만 포함해야 합니다."
            );
        }
    }
}

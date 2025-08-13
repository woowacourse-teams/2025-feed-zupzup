package feedzupzup.backend.admin.domain.vo;

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
public class LoginId {

    private static final int MAX_LENGTH = 10;
    private static final String BLANK_SPACE = " ";
    private static final Pattern ALLOWD_LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$"); // 영여, 숫자만 가능

    private String loginId;

    public LoginId(final String loginId) {
        validateLength(loginId);
        validateFormat(loginId);
        this.loginId = loginId;
    }

    private void validateLength(final String loginId) {
        if (loginId.isEmpty() || loginId.length() > MAX_LENGTH) {
            throw new AuthException(
                    ErrorCode.INVALID_ADMIN_ID_FORMAT,
                    "loginId = " + loginId + " length = " + loginId.length()
            );
        }
    }

    private void validateFormat(final String loginId) {
        if (loginId.contains(BLANK_SPACE)) {
            throw new AuthException(
                    ErrorCode.INVALID_ADMIN_ID_FORMAT,
                    "loginId = " + loginId + " 공백이 포함 되어있습니다."
            );
        }
        if (!ALLOWD_LOGIN_PATTERN.matcher(loginId).matches()) {
            throw new AuthException(
                    ErrorCode.INVALID_ADMIN_ID_FORMAT,
                    "loginId = " + loginId + " 은(는) 영문과 숫자만 포함해야 합니다."
            );
        }
    }
}

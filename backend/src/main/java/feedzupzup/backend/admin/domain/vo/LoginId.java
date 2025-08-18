package feedzupzup.backend.admin.domain.vo;

import feedzupzup.backend.admin.domain.exception.AdminException.InvalidAdminIdException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public record LoginId(
        @Column(name = "login_id", nullable = false, unique = true)
        String value
) {

    private static final int MAX_LENGTH = 10;
    private static final String BLANK_SPACE = " ";
    private static final Pattern ALLOWD_LOGIN_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$"); // 영여, 숫자만 가능

    public LoginId {
        validateLength(value);
        validateFormat(value);
    }

    private void validateLength(final String loginId) {
        if (loginId.isEmpty() || loginId.length() > MAX_LENGTH) {
            throw new InvalidAdminIdException("loginId = " + loginId + " length = " + loginId.length());
        }
    }

    private void validateFormat(final String loginId) {
        if (loginId.contains(BLANK_SPACE)) {
            throw new InvalidAdminIdException("loginId = " + loginId + " 공백이 포함 되어있습니다.");
        }
        if (!ALLOWD_LOGIN_PATTERN.matcher(loginId).matches()) {
            throw new InvalidAdminIdException("loginId = " + loginId + " 은(는) 영문과 숫자만 포함해야 합니다.");
        }
    }
}

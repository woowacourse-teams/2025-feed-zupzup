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
public class AdminName {

    private static final int MAX_LENGTH = 10;
    private static final Pattern ALLOWED_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣]+$"); // 영어,숫자,한글 가능

    private String adminName;

    public AdminName(final String adminName) {
        validateLength(adminName);
        validateFormat(adminName);
        this.adminName = adminName;
    }

    private void validateLength(final String adminName) {
        if(adminName.isEmpty() || adminName.length() > MAX_LENGTH)
            throw new AuthException(ErrorCode.INVALID_ADMIN_NAME_FORMAT, "adminName = " + adminName + " length = " + adminName.length());
    }

    private void validateFormat(final String adminName) {
        if (!ALLOWED_NAME_PATTERN.matcher(adminName).matches()) {
            throw new AuthException(
                    ErrorCode.INVALID_ADMIN_NAME_FORMAT,
                    "adminName = " + adminName + " 은(는) 영어, 숫자, 한글만 포함해야 합니다."
            );
        }
    }
}

package feedzupzup.backend.admin.domain.vo;

import feedzupzup.backend.admin.domain.exception.AdminException.InvalidAdminNameException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public record AdminName(
        @Column(name = "admin_name", nullable = false)
        String value
) {

    private static final int MAX_LENGTH = 20;
    private static final Pattern ALLOWED_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣]+$"); // 영어,숫자,한글 가능

    public AdminName {
        validateLength(value);
        validateFormat(value);
    }

    private void validateLength(final String adminName) {
        if(adminName.isEmpty() || adminName.length() > MAX_LENGTH)
            throw new InvalidAdminNameException("adminName = " + adminName + " length = " + adminName.length());
    }

    private void validateFormat(final String adminName) {
        if (!ALLOWED_NAME_PATTERN.matcher(adminName).matches()) {
            throw new InvalidAdminNameException("adminName = " + adminName + " 은(는) 영어, 숫자, 한글만 포함해야 합니다.");
        }
    }
}

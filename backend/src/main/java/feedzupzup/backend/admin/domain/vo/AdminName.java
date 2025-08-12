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
public class AdminName {

    private static final int MAX_LENGTH = 10;
    private static final String BLANK_SPACE = " ";

    private String adminName;

    public AdminName(final String adminName) {
        validateLength(adminName);
        validateFormat(adminName);
        this.adminName = adminName;
    }

    private void validateLength(final String adminName) {
        if(adminName.isEmpty() || adminName.length() > MAX_LENGTH)
            throw new AdminException(ErrorCode.INVALID_ADMIN_NAME_FORMAT, "adminName = " + adminName + " length = " + adminName.length());
    }

    private void validateFormat(final String adminName) {
        if (adminName.contains(BLANK_SPACE)) {
            throw new AdminException(ErrorCode.INVALID_ADMIN_NAME_FORMAT, "adminName = " + adminName + " contains whitespace");
        }
    }
}

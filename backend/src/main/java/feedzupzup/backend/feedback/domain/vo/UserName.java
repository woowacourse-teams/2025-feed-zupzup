package feedzupzup.backend.feedback.domain.vo;

import feedzupzup.backend.feedback.exception.FeedbackException;
import feedzupzup.backend.global.response.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserName {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 10;

    @Column(name = "user_name", nullable = false)
    private String value;

    public UserName(final String value) {
        validateNameLength(value);
        this.value = value;
    }

    private void validateNameLength(final String value) {
        if (!(value.length() >= MIN_LENGTH && value.length() <= MAX_LENGTH)) {
            throw new FeedbackException(ErrorCode.INVALID_USERNAME_LENGTH, "value = " + value + " length = " + value.length());
        }
    }
}

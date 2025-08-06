package feedzupzup.backend.feedback.domain;

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
public class Comment {

    private static final int MAX_LENGTH = 500;

    @Column(name = "comment")
    private String value;

    public Comment(final String value) {
        validateLength(value);
        this.value = value;
    }

    public void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new FeedbackException(
                    ErrorCode.INVALID_USERNAME_LENGTH,
                    "value의 length은 " + MAX_LENGTH + "를 초과할 수 없습니다.");
        }
    }

}

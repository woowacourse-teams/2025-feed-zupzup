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
public class Content {

    private static final int MAX_LENGTH = 500;

    @Column(name = "content")
    private String value;

    public Content(final String value) {
        validateLength(value);
        this.value = value;
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_LENGTH) {
            throw new FeedbackException(
                    ErrorCode.INVALID_CONTENT_LENGTH,
                    "content의 length은 " + MAX_LENGTH + "를 초과할 수 없습니다.");
        }
    }
}

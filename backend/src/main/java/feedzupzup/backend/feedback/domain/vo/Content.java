package feedzupzup.backend.feedback.domain.vo;

import feedzupzup.backend.feedback.exception.FeedbackException;
import feedzupzup.backend.global.response.ErrorCode;
import com.vane.badwordfiltering.BadWordFiltering;
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
    private static final String[] SUBSTITUTION_WORDS = {"멍멍", "야옹", "음메"};
    private static final BadWordFiltering FILTER = new BadWordFiltering();

    @Column(name = "content")
    private String value;

    public Content(final String value) {
        validateLength(value);
        this.value = filterProfanity(value);
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_LENGTH) {
            throw new FeedbackException(
                    ErrorCode.INVALID_CONTENT_LENGTH,
                    "content의 length은 " + MAX_LENGTH + "를 초과할 수 없습니다.");
        }
    }

    private String filterProfanity(final String value) {
        if (!FILTER.check(value)) {
            return value;
        }

        String result = FILTER.change(value);
        int index = 0;
        while (result.contains("*")) {
            result = result.replaceFirst("\\*+", SUBSTITUTION_WORDS[index++ % SUBSTITUTION_WORDS.length]);
        }
        return result;
    }
}

package feedzupzup.backend.feedback.domain.vo;

import feedzupzup.backend.feedback.exception.FeedbackException;
import feedzupzup.backend.feedback.exception.FeedbackException.FeedbackNegativeException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeCount {

    @Column(name = "like_count")
    private int value;

    public LikeCount(final int value) {
        validateLikeCount(value);
        this.value = value;
    }

    public LikeCount increase() {
        return new LikeCount(this.value + 1);
    }

    public LikeCount decrease() {
        return new LikeCount(this.value - 1);
    }

    private void validateLikeCount(final int value) {
        if (value < 0) {
            throw new FeedbackNegativeException("좋아요는 음수가 될 수 없습니다.");
        }
    }
}

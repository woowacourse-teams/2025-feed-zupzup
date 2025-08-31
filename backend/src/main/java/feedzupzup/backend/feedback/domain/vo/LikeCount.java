package feedzupzup.backend.feedback.domain.vo;

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
        this.value = value;
    }

    public LikeCount updateLikeCount(int likeCount) {
        int newLikeCount = this.value + likeCount;
        if (newLikeCount < 0) {
            newLikeCount = 0;
        }
        return new LikeCount(newLikeCount);
    }
}

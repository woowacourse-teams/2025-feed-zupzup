package feedzupzup.backend.feedback.domain.vo;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostedAt {

    private static final String TIME_ZONE_AREA = "Asia/Seoul";

    private LocalDateTime postedAt;

    private PostedAt(final LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public static PostedAt createTimeInSeoul() {
        return new PostedAt(LocalDateTime.now(ZoneId.of(TIME_ZONE_AREA)));
    }

    /**
     * 테스트 코드를 위한 생성자 입니다.
     */
    public static PostedAt from(LocalDateTime postedAt) {
        return new PostedAt(postedAt);
    }

    public LocalDateTime getValue() {
        return postedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostedAt postedAt1 = (PostedAt) o;
        return Objects.equals(postedAt.truncatedTo(ChronoUnit.MICROS), postedAt1.postedAt.truncatedTo(ChronoUnit.MICROS));
    }

    @Override
    public int hashCode() {
        return Objects.hash(postedAt.truncatedTo(ChronoUnit.MICROS));
    }
}

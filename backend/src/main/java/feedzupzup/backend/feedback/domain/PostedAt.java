package feedzupzup.backend.feedback.domain;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public LocalDate getPostedDate() {
        return postedAt.toLocalDate();
    }
}

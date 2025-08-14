package feedzupzup.backend.feedback.domain.vo;

import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostedAt {

    private LocalDateTime postedAt;

    public PostedAt(final LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public LocalDateTime getValue() {
        return postedAt;
    }
}

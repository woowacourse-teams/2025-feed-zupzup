package feedzupzup.backend.feedback.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostedAt {

    @Column(name = "posted_at", nullable = false)
    private LocalDateTime value;

    public PostedAt(final LocalDateTime postedAt) {
        this.value = postedAt;
    }
}

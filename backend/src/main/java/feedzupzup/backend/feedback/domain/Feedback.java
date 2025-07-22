package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private boolean isSecret;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    private Long placeId;

    private String imageUrl;

    private int likeCount;

    @Builder
    public Feedback(
            final String content,
            final boolean isSecret,
            final ProcessStatus status,
            final Long placeId,
            final String imageUrl,
            final int likeCount
    ) {
        this.content = content;
        this.isSecret = isSecret;
        this.status = status;
        this.placeId = placeId;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
    }

    public Feedback copyOfLikeCount(final int likeCount) {
        return Feedback.builder()
                .likeCount(likeCount)
                .isSecret(isSecret)
                .status(status)
                .placeId(placeId)
                .imageUrl(imageUrl)
                .content(content)
                .build();
    }

    public void updateStatus(final ProcessStatus status) {
        this.status = status;
    }

    public void updateSecret(final boolean isSecret) {
        this.isSecret = isSecret;
    }
}

package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Embedded;
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

    @Embedded
    private ImageUrl imageUrl;

    private int likeCount;

    @Embedded
    private UserName userName;

    @Builder
    public Feedback(
            final String content,
            final boolean isSecret,
            final ProcessStatus status,
            final Long placeId,
            final ImageUrl imageUrl,
            final int likeCount,
            final UserName userName
    ) {
        this.content = content;
        this.isSecret = isSecret;
        this.status = status;
        this.placeId = placeId;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
        this.userName = userName;
    }

    public void updateStatus(final ProcessStatus status) {
        this.status = status;
    }

    public String getUserName() {
        return userName.getValue();
    }

    public void updateSecret(final boolean isSecret) {
        this.isSecret = isSecret;
    }

    public String getImageUrl() {
        return imageUrl.imageUrl();
    }

    public void updateLikeCount(int likeCount) {
        if (likeCount < 0) {
            likeCount = 0;
        }
        this.likeCount = likeCount;
    }
}

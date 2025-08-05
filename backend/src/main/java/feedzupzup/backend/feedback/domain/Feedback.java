package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.category.domain.AvailableCategory;
import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private boolean isSecret;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @Column(nullable = false)
    private Long organizationId;

    private int likeCount;

    @Embedded
    @Column(nullable = false)
    private UserName userName;

    @Embedded
    @Column(nullable = false)
    private PostedAt postedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private AvailableCategory availableCategory;

    @Builder
    public Feedback(
            final @NonNull String content,
            final boolean isSecret,
            final @NonNull ProcessStatus status,
            final @NonNull Long organizationId,
            final int likeCount,
            final @NonNull UserName userName,
            final @NonNull PostedAt postedAt,
            final @NonNull AvailableCategory availableCategory
    ) {
        this.content = content;
        this.isSecret = isSecret;
        this.status = status;
        this.organizationId = organizationId;
        this.likeCount = likeCount;
        this.userName = userName;
        this.postedAt = postedAt;
        this.availableCategory = availableCategory;
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

    public void updateLikeCount(int likeCount) {
        if (likeCount < 0) {
            likeCount = 0;
        }
        this.likeCount = likeCount;
    }

    public boolean isConfirmed() {
        return this.status == ProcessStatus.CONFIRMED;
    }

    public boolean isWaiting() {
        return this.status == ProcessStatus.WAITING;
    }

    public LocalDate getPostedDate() {
        return postedAt.getPostedDate();
    }
}

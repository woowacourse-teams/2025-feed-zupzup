package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.feedback.domain.vo.Comment;
import feedzupzup.backend.feedback.domain.vo.Content;
import feedzupzup.backend.feedback.domain.vo.PostedAt;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.domain.vo.UserName;
import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.organization.domain.Organization;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE feedback SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Content content;

    private boolean isSecret;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    private int likeCount;

    @Embedded
    @Column(nullable = false)
    private UserName userName;

    @Embedded
    @Column(nullable = false)
    private PostedAt postedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrganizationCategory organizationCategory;

    @Embedded
    private Comment comment;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @Builder
    public Feedback(
            final @NonNull Content content,
            final boolean isSecret,
            final @NonNull ProcessStatus status,
            final @NonNull Organization organization,
            final int likeCount,
            final @NonNull UserName userName,
            final @NonNull PostedAt postedAt,
            final @NonNull OrganizationCategory organizationCategory,
            final Comment comment
    ) {
        this.content = content;
        this.isSecret = isSecret;
        this.status = status;
        this.organization = organization;
        this.likeCount = likeCount;
        this.userName = userName;
        this.postedAt = postedAt;
        this.organizationCategory = organizationCategory;
        this.comment = comment;
    }

    public void updateStatus(final ProcessStatus status) {
        this.status = status;
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

    public void updateCommentAndStatus(final Comment comment) {
        this.comment = comment;
        this.status = ProcessStatus.CONFIRMED;
    }

    public boolean isWaiting() {
        return this.status == ProcessStatus.WAITING;
    }
}

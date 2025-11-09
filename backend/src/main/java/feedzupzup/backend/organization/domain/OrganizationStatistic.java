package feedzupzup.backend.organization.domain;

import feedzupzup.backend.feedback.domain.FeedbackAmount;
import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE organization_statistic SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class OrganizationStatistic extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @Embedded
    private FeedbackAmount feedbackAmount;

    public OrganizationStatistic(
            final Organization organization
    ) {
        this.organization = organization;
        this.feedbackAmount = new FeedbackAmount(0, 0, 0);
    }

    public void increaseConfirmedCount() {
        feedbackAmount.increaseConfirmedCount();
    }

    public void increaseWaitingCount() {
        feedbackAmount.increaseWaitingCount();
    }

    public void decreaseConfirmedCount() {
        feedbackAmount.decreaseConfirmedCount();
    }

    public void decreaseWaitingCount() {
        feedbackAmount.decreaseWaitingCount();
    }
}

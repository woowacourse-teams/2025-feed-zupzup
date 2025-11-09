package feedzupzup.backend.organization.domain;

import feedzupzup.backend.feedback.domain.FeedbackAmount;
import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.CascadeType;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

}

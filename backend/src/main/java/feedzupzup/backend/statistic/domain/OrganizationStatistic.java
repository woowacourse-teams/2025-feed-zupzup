package feedzupzup.backend.statistic.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.organization.domain.Organization;
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

    private long feedbackTotalCount;

    private long feedbackConfirmedCount;

    private long feedbackWaitingCount;

    public void increaseConfirmedCount() {
        this.feedbackTotalCount ++;
        this.feedbackConfirmedCount ++;
    }

    public void increaseWaitingCount() {
        this.feedbackTotalCount ++;
        this.feedbackWaitingCount ++;
    }

}

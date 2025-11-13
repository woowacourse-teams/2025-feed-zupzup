package feedzupzup.backend.organization.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackAmount {

    private long feedbackTotalCount;
    private long feedbackConfirmedCount;
    private long feedbackWaitingCount;

    public FeedbackAmount(
            final long feedbackTotalCount,
            final long feedbackConfirmedCount,
            final long feedbackWaitingCount
    ) {
        this.feedbackTotalCount = feedbackTotalCount;
        this.feedbackConfirmedCount = feedbackConfirmedCount;
        this.feedbackWaitingCount = feedbackWaitingCount;
    }

    public int calculateReflectionRate() {
        if (feedbackTotalCount == 0) {
            return 0;
        }
        final double value = (double) feedbackConfirmedCount / feedbackTotalCount * 100.0;
        return (int) Math.round(value);
    }
}
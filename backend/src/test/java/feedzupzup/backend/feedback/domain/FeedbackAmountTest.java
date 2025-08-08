package feedzupzup.backend.feedback.domain;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FeedbackAmountTest {

    @Test
    @DisplayName("모든 피드백이 대기 상태일 때 통계를 계산한다")
    void calculateStatistics_withAllWaitingFeedbacks() {
        // given
        final FeedbackAmount feedbackAmount = new FeedbackAmount(3, 0, 3);

        // when & then
        assertThat(feedbackAmount.calculateReflectionRate()).isEqualTo(0);
    }

    @Test
    @DisplayName("모든 피드백이 확인 상태일 때 통계를 계산한다")
    void calculateStatistics_withAllConfirmedFeedbacks() {
        // given
        final FeedbackAmount feedbackAmount = new FeedbackAmount(3, 3, 0);

        // when & then
        assertThat(feedbackAmount.calculateReflectionRate()).isEqualTo(100);
    }

    @Test
    @DisplayName("대기 상태와 확인 상태가 혼합된 피드백에 대해 통계를 계산한다")
    void calculateStatistics_withMixedStatusFeedbacks() {
        // given
        final FeedbackAmount feedbackAmount = new FeedbackAmount(5, 3, 2);

        // when & then
        assertThat(feedbackAmount.calculateReflectionRate()).isEqualTo(60);
    }

    @Test
    @DisplayName("반영률이 소수점 한 자리에서 반올림 해 정수로 반환되는지 확인한다.")
    void calculateReflectionRate_roundsToTwoDecimalPlaces() {
        // given
        final FeedbackAmount feedbackAmount = new FeedbackAmount(3, 1, 2);

        // when & then
        assertThat(feedbackAmount.calculateReflectionRate()).isEqualTo(33);
    }
}

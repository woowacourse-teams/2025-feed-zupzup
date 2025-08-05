package feedzupzup.backend.feedback.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.CheeringCount;
import feedzupzup.backend.organization.domain.Organization;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class FeedbacksTest {

    private final Organization organization = new Organization("test", new CheeringCount(1));
    private final OrganizationCategory organizationCategory = new OrganizationCategory(organization, Category.FACILITY);

    @Test
    @DisplayName("피드백이 없을 때 통계를 계산한다")
    void calculateStatistics_withEmptyFeedbacks() {
        // given
        final Feedbacks feedbacks = new Feedbacks(new ArrayList<>());

        // when & then
        assertAll(
                () -> assertThat(feedbacks.calculateReflectionRate()).isZero(),
                () -> assertThat(feedbacks.calculateConfirmedCount()).isZero(),
                () -> assertThat(feedbacks.calculateWaitingCount()).isZero(),
                () -> assertThat(feedbacks.getSize()).isZero()
        );
    }

    @Test
    @DisplayName("모든 피드백이 대기 상태일 때 통계를 계산한다")
    void calculateStatistics_withAllWaitingFeedbacks() {
        // given

        final List<Feedback> feedbackList = List.of(
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.WAITING,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.WAITING,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.WAITING,
                        organizationCategory)
        );
        final Feedbacks feedbacks = new Feedbacks(feedbackList);

        // when & then
        assertAll(
                () -> assertThat(feedbacks.calculateReflectionRate()).isZero(),
                () -> assertThat(feedbacks.calculateConfirmedCount()).isZero(),
                () -> assertThat(feedbacks.calculateWaitingCount()).isEqualTo(3),
                () -> assertThat(feedbacks.getSize()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("모든 피드백이 확인 상태일 때 통계를 계산한다")
    void calculateStatistics_withAllConfirmedFeedbacks() {
        // given
        final List<Feedback> feedbackList = List.of(
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.CONFIRMED,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.CONFIRMED,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.CONFIRMED,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.CONFIRMED,
                        organizationCategory)
        );
        final Feedbacks feedbacks = new Feedbacks(feedbackList);

        // when & then
        assertAll(
                () -> assertThat(feedbacks.calculateReflectionRate()).isEqualTo(100),
                () -> assertThat(feedbacks.calculateConfirmedCount()).isEqualTo(4),
                () -> assertThat(feedbacks.calculateWaitingCount()).isZero(),
                () -> assertThat(feedbacks.getSize()).isEqualTo(4)
        );
    }

    @Test
    @DisplayName("대기 상태와 확인 상태가 혼합된 피드백에 대해 통계를 계산한다")
    void calculateStatistics_withMixedStatusFeedbacks() {
        // given
        final List<Feedback> feedbackList = List.of(
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.WAITING,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.CONFIRMED,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.WAITING,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.CONFIRMED,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.CONFIRMED,
                        organizationCategory)
        );
        final Feedbacks feedbacks = new Feedbacks(feedbackList);

        // when & then
        assertAll(
                () -> assertThat(feedbacks.calculateReflectionRate()).isEqualTo(60),
                () -> assertThat(feedbacks.calculateConfirmedCount()).isEqualTo(3),
                () -> assertThat(feedbacks.calculateWaitingCount()).isEqualTo(2),
                () -> assertThat(feedbacks.getSize()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("반영률이 소수점 한 자리에서 반올림 해 정수로 반환되는지 확인한다.")
    void calculateReflectionRate_roundsToTwoDecimalPlaces() {
        // given
        final List<Feedback> feedbackList = List.of(
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.WAITING,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.WAITING,
                        organizationCategory),
                FeedbackFixture.createFeedbackWithStatus(ProcessStatus.CONFIRMED,
                        organizationCategory)
        );
        final Feedbacks feedbacks = new Feedbacks(feedbackList);

        // when
        final double reflectionRate = feedbacks.calculateReflectionRate();

        // then
        assertThat(reflectionRate).isEqualTo(33);
    }
}

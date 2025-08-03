package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.PostedAt;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FeedbackStatisticServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private FeedbackStatisticService feedbackStatisticService;

    @Test
    @DisplayName("특정 날짜 이후의 피드백에 대한 통계를 계산한다")
    void calculateStatistic_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final Feedback confirmedFeedback1 = FeedbackFixture.createFeedbackWithStatus(
                ProcessStatus.CONFIRMED);
        final Feedback waitingFeedback = FeedbackFixture.createFeedbackWithStatus(
                ProcessStatus.WAITING);
        final Feedback confirmedFeedback2 = FeedbackFixture.createFeedbackWithStatus(
                ProcessStatus.CONFIRMED);

        System.out.println(confirmedFeedback1.getPostedAt().getPostedDate());

        // 피드백 저장
        feedBackRepository.save(confirmedFeedback1);
        feedBackRepository.save(waitingFeedback);
        feedBackRepository.save(confirmedFeedback2);

        final String daysAgo = "WEEK"; // 7일 전 이후의 피드백만 조회

        // when
        final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                organization.getId(),
                daysAgo
        );

        // then
        // 모든 피드백이 현재 날짜에 생성되므로 모두 포함됨
        assertAll(
                () -> assertThat(response.totalCount()).isEqualTo(3), // 모든 피드백 포함
                () -> assertThat(response.confirmedCount()).isEqualTo(2), // CONFIRMED 상태 피드백 2개
                () -> assertThat(response.waitingCount()).isEqualTo(1), // WAITING 상태 피드백 1개
                () -> assertThat(response.reflectionRate()).isEqualTo(67)
        );
    }

    @Test
    @DisplayName("통계 메서드 호출 중 존재하지 않는 place가 주어진다면, 예외가 발생해야 한다.")
    void calculateStatistic_withNoFeedbacks() {
        // given
        final Long organizationId = 999L; // 존재하지 않는 장소 ID
        final String daysAgo = "WEEK";

        // when & then
        assertThatThrownBy(() -> feedbackStatisticService.calculateStatistic(organizationId, daysAgo))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Nested
    @DisplayName("period 값에 따라, 통계 메서드를 호출할 수 있어야 한다.")
    class CalculateStatisticWithVariousStatuses {

        @Test
        @DisplayName("당일 조회 테스트 케이스")
        void one_day() {
            final Organization organization = OrganizationFixture.createAllBlackBox();
            Organization savedOrganization = organizationRepository.save(organization);
            // given
            final PostedAt postedAt1 = PostedAt.from(LocalDateTime.now().minusDays(2L));// 이틀 전
            final PostedAt postedAt2 = PostedAt.from(LocalDateTime.now().minusDays(1L)); // 하루 전
            final PostedAt postedAt3 = PostedAt.from(LocalDateTime.now()); //오늘
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt1, ProcessStatus.WAITING);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt2, ProcessStatus.WAITING);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt3, ProcessStatus.WAITING);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);

            // when
            final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                    savedOrganization.getId(), "TODAY");

            // then
            assertThat(response.totalCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("당일 조회 경곗값 케이스 테스트")
        void one_day_boundary_value() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            Organization savedOrganization = organizationRepository.save(organization);

            // 당일 전날의 23:59시
            final LocalDateTime targetDate1 = LocalDate.now().atStartOfDay().minusMinutes(1);

            // 당일 00:00시
            final LocalDateTime targetDate2 = LocalDate.now().atStartOfDay();
            final LocalDateTime targetDate3 = LocalDate.now().atStartOfDay().plusMinutes(1);

            final PostedAt postedAt1 = PostedAt.from(targetDate1);
            final PostedAt postedAt2 = PostedAt.from(targetDate2);
            final PostedAt postedAt3 = PostedAt.from(targetDate3);
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt1, ProcessStatus.WAITING);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt2, ProcessStatus.WAITING);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt3, ProcessStatus.WAITING);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);

            // when
            final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                    savedOrganization.getId(), "TODAY");

            // then
            assertThat(response.totalCount()).isEqualTo(2); //targetDate2, targetDate3
        }

        @Test
        @DisplayName("일주일 간의 피드백에 대한 통계 테스트")
        void once_a_week() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            Organization savedOrganization = organizationRepository.save(organization);
            final PostedAt postedAt1 = PostedAt.from(LocalDateTime.now().minusDays(7L));
            final PostedAt postedAt2 = PostedAt.from(LocalDateTime.now().minusDays(6L));
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt1, ProcessStatus.WAITING);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt2, ProcessStatus.WAITING);
            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);

            // when
            final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                    savedOrganization.getId(), "WEEK");

            // then
            assertThat(response.totalCount()).isEqualTo(1);
        }
    }
}

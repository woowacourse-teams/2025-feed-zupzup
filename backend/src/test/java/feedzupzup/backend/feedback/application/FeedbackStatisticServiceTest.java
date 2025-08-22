package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.response.StatisticResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.UUID;
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

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Test
    @DisplayName("피드백 상태에 따른 통계를 계산한다.")
    void calculateStatistic_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback confirmedFeedback1 = FeedbackFixture.createFeedbackWithStatus(
                organization, ProcessStatus.CONFIRMED, organizationCategory);
        final Feedback waitingFeedback = FeedbackFixture.createFeedbackWithStatus(
                organization, ProcessStatus.WAITING, organizationCategory);
        final Feedback confirmedFeedback2 = FeedbackFixture.createFeedbackWithStatus(
                organization, ProcessStatus.CONFIRMED, organizationCategory);

        // 피드백 저장
        feedBackRepository.save(confirmedFeedback1);
        feedBackRepository.save(waitingFeedback);
        feedBackRepository.save(confirmedFeedback2);

        // when
        final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                organization.getUuid());

        // then
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
        final UUID organizationUuid = UUID.randomUUID(); // 존재하지 않는 장소 ID

        // when & then
        assertThatThrownBy(() -> feedbackStatisticService.calculateStatistic(organizationUuid))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Nested
    @DisplayName("ProcessStatus와 organizationId 따라, 통계를 계산할 수 있어야 한다.")
    class CalculateStatisticWithVariousStatuses {

        @Test
        @DisplayName("본인 organizationId에 속한 피드백만 계산을 해야한다.")
        void only_calculate_own_organization_feedback() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            final Organization otherOrganization = OrganizationFixture.createAllBlackBox();

            organizationRepository.save(organization);
            organizationRepository.save(otherOrganization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.WAITING, organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.WAITING, organizationCategory);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);

            // when
            final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                    otherOrganization.getUuid());

            // then
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(0),
                    () -> assertThat(response.waitingCount()).isEqualTo(0),
                    () -> assertThat(response.reflectionRate()).isEqualTo(0),
                    () -> assertThat(response.confirmedCount()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("확인 3개, 대기 2개일 경우, 총 5개의 피드백, 60개의 처리율이 구해져야 한다.")
        void one_day_boundary_value() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            Organization savedOrganization = organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.WAITING, organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.WAITING, organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.CONFIRMED, organizationCategory);
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.CONFIRMED, organizationCategory);
            final Feedback feedback5 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.CONFIRMED, organizationCategory);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);
            feedBackRepository.save(feedback4);
            feedBackRepository.save(feedback5);

            // when
            final StatisticResponse response = feedbackStatisticService.calculateStatistic(
                    savedOrganization.getUuid());

            // then
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(5),
                    () -> assertThat(response.waitingCount()).isEqualTo(2),
                    () -> assertThat(response.confirmedCount()).isEqualTo(3),
                    () -> assertThat(response.reflectionRate()).isEqualTo(60)
            );
        }
    }
}

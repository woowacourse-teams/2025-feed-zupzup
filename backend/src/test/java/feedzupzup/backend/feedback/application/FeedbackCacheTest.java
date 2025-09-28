package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.LATEST;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class FeedbackCacheTest extends ServiceIntegrationHelper {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private UserFeedbackService userFeedbackService;

    @MockitoSpyBean
    private FeedbackRepository feedbackRepository;

    private Organization organization;

    private OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
    }

    @Nested
    @DisplayName("유저 피드백 조회(최신순) 시 캐시 히트 테스트")
    class CacheTest {

        @Test
        @DisplayName("최신순 피드백을 가져올 때, DB 접근을 하지 않고, 캐시 데이터를 가져와야 한다. (두 번 조회)")
        void latest_feedback_case_two_retrieve() {
            // given
            // 첫 번째 조회는 캐시가 존재하지 않기에, DB에 접근해야 한다.
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);

            // when
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);

            // then
            verify(feedbackRepository, times(1)).findByLatest(any(), any(), any(), any());
        }

        @Test
        @DisplayName("최신순 피드백을 가져올 때, DB 접근을 하지 않고, 캐시 데이터를 가져와야 한다. (세 번 조회)")
        void latest_feedback_case_three_retrieve() {
            // given
            // 첫 번째 조회는 캐시가 존재하지 않기에, DB에 접근해야 한다.
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);

            // when
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);

            // then
            verify(feedbackRepository, times(1)).findByLatest(any(), any(), any(), any());
        }

        @Test
        @DisplayName("최신순 피드백을 가져올 때, 전체 상태 조회가 아니라면, 캐시가 되어서는 안 된다.")
        void latest_feedback_not_all_status() {
            // given
            // 첫 번째 조회는 캐시가 존재하지 않기에, DB에 접근해야 한다.
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, CONFIRMED, LATEST);

            // when
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, CONFIRMED, LATEST);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, WAITING, LATEST);

            // then
            verify(feedbackRepository, times(3)).findByLatest(any(), any(), any(), any());
        }

        @Test
        @DisplayName("최신순 캐시 도중, 새로운 값이 저장된다면 캐시 값이 바뀌어야 한다.(사이즈 10 이하인 경우)")
        void when_save_new_feedback_then_update_latest_cache() {
            // given
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);

            feedbackRepository.save(feedback1);
            feedbackRepository.save(feedback2);
            feedbackRepository.save(feedback3);

            final UserFeedbackListResponse feedbackPage = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);

            final CreateFeedbackRequest request = new CreateFeedbackRequest("맛있어요", false, "젠슨", "건의");

            userFeedbackService.create(request, organization.getUuid());

            final UserFeedbackListResponse feedbackPage2 = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);;

            verify(feedbackRepository, times(1)).findByLatest(any(), any(), any(), any());

            final List<FeedbackItem> feedbacks = feedbackPage2.feedbacks();
            assertAll(
                    () -> assertThat(feedbacks.get(0).feedbackId()).isEqualTo(4),
                    () -> assertThat(feedbacks.get(1).feedbackId()).isEqualTo(3),
                    () -> assertThat(feedbacks.get(2).feedbackId()).isEqualTo(2),
                    () -> assertThat(feedbacks.get(3).feedbackId()).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("최신순 캐시 도중, 새로운 값이 저장된다면 캐시 값이 바뀌어야 한다.(사이즈 10 이상인 경우)")
        void when_save_new_feedback_then_update_latest_cache_over_10() {
            // given
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback5 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback6 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback7 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback8 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback9 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback10 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);

            feedbackRepository.save(feedback1);
            feedbackRepository.save(feedback2);
            feedbackRepository.save(feedback3);
            feedbackRepository.save(feedback4);
            feedbackRepository.save(feedback5);
            feedbackRepository.save(feedback6);
            feedbackRepository.save(feedback7);
            feedbackRepository.save(feedback8);
            feedbackRepository.save(feedback9);
            feedbackRepository.save(feedback10);

            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);

            final CreateFeedbackRequest request = new CreateFeedbackRequest("맛있어요", false, "젠슨", "건의");

            userFeedbackService.create(request, organization.getUuid());

            final UserFeedbackListResponse feedbackPage2 = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);;

            verify(feedbackRepository, times(1)).findByLatest(any(), any(), any(), any());

            final List<FeedbackItem> feedbacks = feedbackPage2.feedbacks();

            assertThat(feedbacks.size()).isEqualTo(10);
            assertAll(
                    () -> assertThat(feedbacks.get(0).feedbackId()).isEqualTo(11),
                    () -> assertThat(feedbacks.get(1).feedbackId()).isEqualTo(10),
                    () -> assertThat(feedbacks.get(2).feedbackId()).isEqualTo(9),
                    () -> assertThat(feedbacks.get(3).feedbackId()).isEqualTo(8),
                    () -> assertThat(feedbacks.get(4).feedbackId()).isEqualTo(7),
                    () -> assertThat(feedbacks.get(5).feedbackId()).isEqualTo(6),
                    () -> assertThat(feedbacks.get(6).feedbackId()).isEqualTo(5),
                    () -> assertThat(feedbacks.get(7).feedbackId()).isEqualTo(4),
                    () -> assertThat(feedbacks.get(8).feedbackId()).isEqualTo(3),
                    () -> assertThat(feedbacks.get(9).feedbackId()).isEqualTo(2)
            );
        }
    }

}

package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.LATEST;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.LIKES;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.OLDEST;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
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
import feedzupzup.backend.feedback.dto.response.UserFeedbackItem;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import java.util.UUID;
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

    @Autowired
    private FeedbackLikeService feedbackLikeService;

    @MockitoSpyBean
    private FeedbackRepository feedbackRepository;

    private Organization organization;

    private OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(organization,
                SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
    }

    @Nested
    @DisplayName("유저 피드백 조회(최신순)시 캐시 테스트")
    class LatestFeedbacksCacheTest {

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
            saveMultipleFeedbacks(3);
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);

            final CreateFeedbackRequest request = new CreateFeedbackRequest("맛있어요", false, "젠슨",
                    "건의");

            userFeedbackService.create(request, organization.getUuid());

            // when
            final UserFeedbackListResponse feedbackPage2 = userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);
            final List<UserFeedbackItem> feedbacks = feedbackPage2.feedbacks();

            // then
            verify(feedbackRepository, times(1)).findByLatest(any(), any(), any(), any());
            assertFeedbackIds(feedbacks, 4L, 3L, 2L, 1L);
        }

        @Test
        @DisplayName("최신순 캐시 도중, 새로운 값이 저장된다면 캐시 값이 바뀌어야 한다.(사이즈 10 이상인 경우)")
        void when_save_new_feedback_then_update_latest_cache_over_10() {
            // given
            saveMultipleFeedbacks(10);

            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);

            final CreateFeedbackRequest request = new CreateFeedbackRequest("맛있어요", false, "젠슨",
                    "건의");

            userFeedbackService.create(request, organization.getUuid());

            // when
            final UserFeedbackListResponse feedbackPage2 = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST);

            // then
            verify(feedbackRepository, times(1)).findByLatest(any(), any(), any(), any());

            final List<UserFeedbackItem> feedbacks = feedbackPage2.feedbacks();

            assertThat(feedbacks.size()).isEqualTo(10);
            assertFeedbackIds(feedbacks, 11L, 10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L);
        }
    }

    @Nested
    @DisplayName("유저 피드백 조회(좋아요 순)시 캐시 테스트")
    class LikesFeedbacksCacheTest {

        @DisplayName("상위 10개의 좋아요 값을 캐싱해놓는다.")
        @Test
        void upper_10_likes_cache() {
            // given
            // 첫 번째 조회는 캐시가 존재하지 않기에, DB에 접근해야 한다.
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LIKES);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LIKES);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LIKES);

            verify(feedbackRepository, times(1)).findByLikes(any(), any(), any(), any());
        }

        @DisplayName("특정 상태가 정해진 경우, 캐시가 되면 안 된다.")
        @Test
        void only_cached_all_status() {
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, CONFIRMED, LIKES);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, WAITING, LIKES);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, CONFIRMED, LIKES);

            verify(feedbackRepository, times(3)).findByLikes(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("유저 피드백 조회(오래된 순)시 캐시 테스트")
    class OldestFeedbacksCacheTest {

        @DisplayName("가장 오래된 WAITING 상태의 10개의 피드백 값을 캐싱해놓는다.")
        @Test
        void oldest_waiting_feedback_cache() {
            // given
            // 첫 번째 조회는 캐시가 존재하지 않기에, DB에 접근해야 한다.
            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, WAITING, OLDEST);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, WAITING, OLDEST);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, WAITING, OLDEST);

            verify(feedbackRepository, times(1)).findByOldest(any(), any(), any(), any());
        }


        @DisplayName("WAITING 상태가 아니라면, 캐시되어서는 안 된다.")
        @Test
        void not_waiting_status_then_not_cached() {

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, CONFIRMED, OLDEST);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, CONFIRMED, OLDEST);

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, CONFIRMED, OLDEST);

            verify(feedbackRepository, times(3)).findByOldest(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("캐시를 재정의 하거나, 비우는 상황 테스트")
    class HandleCacheTest {

        @Nested
        @DisplayName("좋아요 케이스")
        class LikestCacheCase {

            @DisplayName("이미 캐시에 속해있는 피드백에 대해 좋아요 증가를 할 경우, 캐시가 재정렬되어야 한다.")
            @Test
            void already_exist_likes_cache_when_like_increase_then_resort() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 2));

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                feedbackLikeService.like(5L, UUID.randomUUID());

                final UserFeedbackListResponse feedbackPage = userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                final List<UserFeedbackItem> feedbacks = feedbackPage.feedbacks();

                verify(feedbackRepository, times(1)).findByLikes(any(), any(), any(), any());
                assertFeedbackIds(feedbacks, 5L, 1L, 2L, 3L, 4L, 6L, 7L, 8L, 9L, 10L);
            }

            @DisplayName("캐시에 속하지 않으면서 증가된 좋아요 값이 마지막 캐시값보다 크거나 같을 경우, 캐시를 재구성 해야한다")
            @Test
            void not_contains_cache_and_cache_last_value_over_or_equals_then_reorganize_cache() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 1, 1));
                userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LIKES);

                // when
                feedbackLikeService.like(11L, UUID.randomUUID());

                userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LIKES);

                final UserFeedbackListResponse feedbackPage = userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LIKES);

                final List<UserFeedbackItem> feedbacks = feedbackPage.feedbacks();

                // then
                verify(feedbackRepository, times(1)).findByLikes(any(), any(), any(), any());
                assertFeedbackIds(feedbacks, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 11L);
            }

            @DisplayName("캐시의 값이 최대 캐시 수보다 작다면, 새로운 캐시에 들어가야 한다.")
            @Test
            void saved_cache_size_under_max_then_put() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10));

                userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LIKES);

                final Feedback feedback4 = FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory, 0);
                feedbackRepository.save(feedback4);

                // when
                feedbackLikeService.like(feedback4.getId(), UUID.randomUUID());

                userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LIKES);

                final UserFeedbackListResponse feedbackPage = userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LIKES);

                final List<UserFeedbackItem> feedbacks = feedbackPage.feedbacks();

                // then
                verify(feedbackRepository, times(1)).findByLikes(any(), any(), any(), any());
                assertFeedbackIds(feedbacks, 1L, 2L, 3L, 4L);
            }

            @DisplayName("좋아요 피드백이 캐시에 속하지도 않고, 제일 작은 캐시값보다 작다면, 기존의 캐시값을 반환해야 한다.")
            @Test
            void like_count_less_then_saved_cache_then_return_basic_cache() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 2));

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                final Feedback feedback11 = FeedbackFixture.createFeedbackWithLikes(
                        organization, organizationCategory, 0);

                feedbackRepository.save(feedback11);

                // when
                feedbackLikeService.like(feedback11.getId(), UUID.randomUUID());

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                final UserFeedbackListResponse feedbackPage = userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                final List<UserFeedbackItem> feedbacks = feedbackPage.feedbacks();

                // then
                verify(feedbackRepository, times(1)).findByLikes(any(), any(), any(), any());
                assertFeedbackIds(feedbacks, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
            }

            @DisplayName("좋아요 감소 요청을 할 때, 마지막 캐시값과 같거나 작다면, 캐시를 비워야한다.")
            @Test
            void ike_count_same_last_cache_then_clear_cache() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 1));

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                final UUID userUuid = UUID.randomUUID();
                feedbackLikeService.like(10L, userUuid);

                // when
                feedbackLikeService.unlike(10L, userUuid);

                // then
                verify(feedbackRepository, times(1)).findByLikes(any(), any(), any(), any());

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                // then
                verify(feedbackRepository, times(2)).findByLikes(any(), any(), any(), any());
            }

            @DisplayName("캐시를 비운 상황에서, 다시 요청이 온다면, DB 에서 새로운 캐시값을 받아와야한다.")
            @Test
            void clear_cache_case_then_re_request_then_connect_db() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 1));

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                final UUID userUuid = UUID.randomUUID();
                feedbackLikeService.like(10L, userUuid);

                // when
                feedbackLikeService.unlike(10L, userUuid);

                // then
                verify(feedbackRepository, times(1)).findByLikes(any(), any(), any(), any());

                // 다시 요청
                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                verify(feedbackRepository, times(2)).findByLikes(any(), any(), any(), any());
            }

            @DisplayName("좋아요 감소 요청을 할 때, 캐시에 존재하는 값이라면, 캐시를 재정의 해야한다.")
            @Test
            void like_count_already_exist_cache_then_reorganize_cache() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 9, 10, 10, 10, 5, 4, 4, 3, 1));

                userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LIKES);

                final UUID userUuid = UUID.randomUUID();
                feedbackLikeService.like(2L, userUuid);

                // when
                feedbackLikeService.unlike(2L, userUuid);
                final UserFeedbackListResponse feedbackPage = userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                final List<UserFeedbackItem> feedbacks = feedbackPage.feedbacks();

                // then
                verify(feedbackRepository, times(1)).findByLikes(any(), any(), any(), any());
                assertFeedbackIds(feedbacks, 1L, 3L, 4L, 5L, 2L, 6L, 7L, 8L, 9L, 10L);
            }

        }
    }

    private void saveMultipleFeedbacks(int count) {
        for (int i=0; i<count; i++) {
            final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization, organizationCategory);
            feedbackRepository.save(feedback);
        }
    }

    private void saveMultipleFeedbacksFromLikeCounts(List<Integer> likeCounts) {
        for (Integer amount : likeCounts) {
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization, organizationCategory, amount);
            feedbackRepository.save(feedback);
        }
    }

    private void assertFeedbackIds(List<UserFeedbackItem> feedbacks, Long... expectedIds) {
        assertThat(feedbacks)
                .extracting("feedbackId")
                .containsExactly(expectedIds);
    }
}

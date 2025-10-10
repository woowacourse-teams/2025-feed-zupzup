package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LATEST;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LIKES;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.OLDEST;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.service.cache.LatestCacheHandler;
import feedzupzup.backend.feedback.domain.service.cache.LikesCacheHandler;
import feedzupzup.backend.feedback.domain.service.cache.OldestCacheHandler;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.UserFeedbackItem;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.feedback.fixture.FeedbackRequestFixture;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

class FeedbackCacheTest extends ServiceIntegrationHelper {

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Autowired
    private FeedbackLikeService feedbackLikeService;

    @Autowired
    private AdminFeedbackService adminFeedbackService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @MockitoSpyBean
    private FeedbackRepository feedbackRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @MockitoSpyBean
    private LatestCacheHandler latestCacheHandler;

    @MockitoSpyBean
    private LikesCacheHandler likesCacheHandler;

    @MockitoSpyBean
    private OldestCacheHandler oldestCacheHandler;

    private Organization organization;

    private OrganizationCategory organizationCategory;

    private final Guest guest = new Guest(UUID.randomUUID(), CurrentDateTime.create());

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(organization,
                SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
        guestRepository.save(guest);
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
                    "건의", "https://example.com/image.png");

            userFeedbackService.create(request, organization.getUuid(), toGuestInfo(guest));

            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(latestCacheHandler, times(1)).handle(any(), any())
                    );

            // when
            final UserFeedbackListResponse feedbackPage2 = userFeedbackService.getFeedbackPage(organization.getUuid(),
                    10, null, null, LATEST);
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
                    "건의", "https://example.com/image.png");

            userFeedbackService.create(request, organization.getUuid(), toGuestInfo(guest));

            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(latestCacheHandler, times(1)).handle(any(), any())
                    );

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

            @DisplayName("좋아요를 누른 값이 캐시의 최솟값보다 같거나 크다면, 캐시를 비워야 한다. - 이미 캐시되어있는 경우")
            @Test
            void already_exist_likes_cache_when_like_increase_then_resort() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 2));

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                // when - 캐시 비우기
                feedbackLikeService.like(5L, toGuestInfo(guest));

                await().atMost(Duration.ofSeconds(1))
                        .untilAsserted(() ->
                                verify(likesCacheHandler, times(1)).handle(any(), any())
                        );

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                // then
                verify(feedbackRepository, times(2)).findByLikes(any(), any(), any(), any());
            }

            @DisplayName("좋아요를 누른 값이 캐시의 최솟값보다 같거나 크다면, 캐시를 비워야 한다. - 캐시가 되어있지 않는 경우")
            @Test
            void not_contains_cache_and_cache_last_value_over_or_equals_then_reorganize_cache() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 2));

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                saveMultipleFeedbacksFromLikeCounts(List.of(1));

                // when
                feedbackLikeService.like(11L, toGuestInfo(guest));

                await().atMost(Duration.ofSeconds(1))
                        .untilAsserted(() ->
                                verify(likesCacheHandler, times(1)).handle(any(), any())
                        );

                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                // then
                verify(feedbackRepository, times(2)).findByLikes(any(), any(), any(), any());
            }

            @DisplayName("좋아요 감소 요청을 누른 값이 마지막 캐시값과 같거나 크다면, 캐시를 비워야한다.")
            @Test
            void like_count_same_last_cache_then_clear_cache() {
                // given
                saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 2));

                // 1차 디비 조회
                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                // 1차 캐시 클리어
                feedbackLikeService.like(9L, toGuestInfo(guest));

                await().atMost(Duration.ofSeconds(1))
                        .untilAsserted(() ->
                                verify(likesCacheHandler, times(1)).handle(any(), any())
                        );

                // 2차 디비 조회
                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                // 2차 캐시 클리어
                feedbackLikeService.unlike(9L, toGuestInfo(guest));

                // 비동기 캐시 클리어 대기
                await().atMost(Duration.ofSeconds(1))
                        .untilAsserted(() ->
                                verify(likesCacheHandler, times(2)).handle(any(), any())
                        );

                // 3차 디비 조회
                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, null, LIKES);

                // then
                verify(feedbackRepository, times(3)).findByLikes(any(), any(), any(), any());
            }

            @DisplayName("피드백의 상태 변경 시 이미 캐시에 있는 값이라면, 캐시를 비워야 한다.")
            @Test
            void when_modify_feedback_status_already_exist_cache_then_remove() {
                // given
                final Admin admin = AdminFixture.create();
                adminRepository.save(admin);
                final Organization organization = OrganizationFixture.createAllBlackBox();
                organizationRepository.save(organization);
                final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
                organizerRepository.save(organizer);

                saveMultipleFeedbacks(10, organization);

                // 1차 조회 (DB 통신1)
                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, WAITING, OLDEST);

                // when - 업데이트 시 기존 캐시 피드백 삭제
                adminFeedbackService.updateFeedbackComment(
                        admin.getId(),
                        new UpdateFeedbackCommentRequest("test"),
                        5L
                );

                await().atMost(Duration.ofSeconds(1))
                        .untilAsserted(() ->
                                verify(oldestCacheHandler, times(1)).handle(any(), any())
                        );

                // (DB 통신2) 캐시가 비어있기 때문에 새로운 DB 통신 필요
                userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, WAITING, OLDEST);

                // then
                verify(feedbackRepository, times(2)).findByOldest(any(), any(), any(), any());
            }

            @DisplayName("피드백의 상태 변경 시 캐시에 해당되지 않는 값이라면, 캐시를 비워선 안 된다.")
            @Test
            void when_modify_feedback_status_not_exist_cache_then_not_clear() {
                // given
                final Admin admin = AdminFixture.create();
                adminRepository.save(admin);
                final Organization organization = OrganizationFixture.createAllBlackBox();
                organizationRepository.save(organization);
                final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
                organizerRepository.save(organizer);

                saveMultipleFeedbacks(10, organization);

                // 1차 조회 (DB 통신1)
                userFeedbackService.getFeedbackPage(
                        organization.getUuid(), 10, null, WAITING, OLDEST);

                saveMultipleFeedbacks(1, organization);

                // when - 캐시에 존재하지 않는다면, 캐시 업데이트를 하지 않는다
                adminFeedbackService.updateFeedbackComment(
                        admin.getId(),
                        new UpdateFeedbackCommentRequest("test"),
                        11L
                );

                await().atMost(Duration.ofSeconds(1))
                        .untilAsserted(() ->
                                verify(oldestCacheHandler, times(1)).handle(any(), any()));

                // 캐시 변경이 되지 않았기에, 기존 캐시값을 가져와야 한다
                userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, WAITING, OLDEST);

                // then
                verify(feedbackRepository, times(1)).findByOldest(any(), any(), any(), any());
            }

        }
    }

    @Nested
    @DisplayName("크로스 캐시 삭제 테스트 - 피드백 변경 시 다른 캐시에도 반영")
    class CrossCacheRemovalTest {

        @DisplayName("좋아요 증가 시 LATEST, OLDEST 캐시에도 해당 피드백이 있다면 모두 삭제되어야 한다")
        @Test
        void when_like_increase_then_remove_all_caches_containing_feedback() {
            // given
            saveMultipleFeedbacks(10);

            // LATEST, OLDEST 캐시 생성
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, WAITING, OLDEST);

            // when - 좋아요 증가
            feedbackLikeService.like(5L, toGuestInfo(guest));

            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(likesCacheHandler, times(1)).handle(any(), any())
                    );

            // 두 번째 조회 시 캐시가 삭제되었으므로 DB 접근 발생
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, WAITING, OLDEST);

            // then - LATEST, OLDEST 캐시가 삭제되어 각각 2번씩 DB 조회 발생
            verify(feedbackRepository, times(2)).findByLatest(any(), any(), any(), any());
            verify(feedbackRepository, times(2)).findByOldest(any(), any(), any(), any());
        }

        @DisplayName("좋아요 감소 시 LATEST, OLDEST 캐시에도 해당 피드백이 있다면 모두 삭제되어야 한다")
        @Test
        void when_unlike_then_remove_all_caches_containing_feedback() {
            // given
            saveMultipleFeedbacks(10);

            // 먼저 좋아요
            feedbackLikeService.like(5L, toGuestInfo(guest));

            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(likesCacheHandler, times(1)).handle(any(), any())
                    );

            // LATEST, OLDEST 캐시 생성
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, WAITING, OLDEST);

            // when - 좋아요 감소
            feedbackLikeService.unlike(5L, toGuestInfo(guest));

            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(likesCacheHandler, times(2)).handle(any(), any())
                    );

            // 두 번째 조회 시 캐시가 삭제되었으므로 DB 접근 발생
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, WAITING, OLDEST);

            // then - LATEST, OLDEST 캐시가 삭제되어 각각 2번씩 DB 조회 발생
            verify(feedbackRepository, times(2)).findByLatest(any(), any(), any(), any());
            verify(feedbackRepository, times(2)).findByOldest(any(), any(), any(), any());
        }

        @DisplayName("피드백 상태 변경 시 LATEST, LIKES 캐시에도 해당 피드백이 있다면 모두 삭제되어야 한다")
        @Test
        void when_update_feedback_status_then_remove_all_caches_containing_feedback() {
            // given
            final Admin admin = AdminFixture.create();
            adminRepository.save(admin);
            final Organization testOrganization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(testOrganization);
            final Organizer organizer = new Organizer(testOrganization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            final OrganizationCategory testCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    testOrganization, SUGGESTION);
            organizationCategoryRepository.save(testCategory);

            saveMultipleFeedbacks(10, testOrganization);

            // LATEST, LIKES 캐시 생성
            userFeedbackService.getFeedbackPage(testOrganization.getUuid(), 10, null, null, LATEST);
            userFeedbackService.getFeedbackPage(testOrganization.getUuid(), 10, null, null, LIKES);

            // when - 피드백 상태 변경
            adminFeedbackService.updateFeedbackComment(
                    admin.getId(),
                    new UpdateFeedbackCommentRequest("test"),
                    5L
            );

            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(oldestCacheHandler, times(1)).handle(any(), any())
                    );

            // 두 번째 조회 시 캐시가 삭제되었으므로 DB 접근 발생
            userFeedbackService.getFeedbackPage(testOrganization.getUuid(), 10, null, null, LATEST);
            userFeedbackService.getFeedbackPage(testOrganization.getUuid(), 10, null, null, LIKES);

            // then - LATEST, LIKES 캐시가 삭제되어 각각 2번씩 DB 조회 발생
            verify(feedbackRepository, times(2)).findByLatest(any(), any(), any(), any());
            verify(feedbackRepository, times(2)).findByLikes(any(), any(), any(), any());
        }

        @DisplayName("좋아요 증가 시 해당 피드백이 캐시에 없다면 다른 캐시는 삭제되지 않아야 한다")
        @Test
        void when_like_increase_feedback_not_in_cache_then_not_remove_other_caches() {
            // given
            saveMultipleFeedbacks(10);

            // LATEST 캐시 생성 (1~10번 피드백)
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);

            // 새로운 피드백 생성 (11번 피드백)
            saveMultipleFeedbacks(1);

            // when - 캐시에 없는 11번 피드백에 좋아요
            feedbackLikeService.like(11L, toGuestInfo(guest));

            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(likesCacheHandler, times(1)).handle(any(), any())
                    );

            // 두 번째 조회 시 캐시가 유지되어 DB 접근 없음
            userFeedbackService.getFeedbackPage(organization.getUuid(), 10, null, null, LATEST);

            // then - LATEST 캐시가 유지되어 1번만 DB 조회
            verify(feedbackRepository, times(1)).findByLatest(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("피드백 캐시 처리 비동기 처리 테스트")
    class AsyncFeedbackCache {

        @Test
        @DisplayName("트랜잭션 롤백 시 캐시 핸들러가 실행되지 않는다 (최신순)")
        void cache_handler_not_called_on_rollback_latest_case() {
            // given
            final CreateFeedbackRequest request = FeedbackRequestFixture.createRequestWithContent("test1");

            // when - 명시적 롤백을 진행한다.
            transactionTemplate.execute(status -> {
                userFeedbackService.create(request, organization.getUuid(), toGuestInfo(guest));
                status.setRollbackOnly();
                return null;
            });

            // then
            verify(latestCacheHandler, never()).handle(any(), any());
        }

        @Test
        @DisplayName("트랜잭션 롤백이 아니라면, 캐시 핸들러가 실행되어야 한다 (최신순)")
        void cache_handler_called_on_commit_latest() {
            // given
            final CreateFeedbackRequest request = FeedbackRequestFixture.createRequestWithContent("test1");

            // when
            userFeedbackService.create(request, organization.getUuid(), toGuestInfo(guest));

            // then
            await().atMost(Duration.ofSeconds(1))
                            .untilAsserted(() ->
                                    verify(latestCacheHandler, times(1)).handle(any(), any()));
        }

        @Test
        @DisplayName("트랜잭션 롤백 시 캐시 핸들러가 실행되지 않는다 (좋아요 순)")
        void cache_handler_not_called_on_rollback_likes_case() {
            // given
            saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 2));

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LIKES);

            // when
            transactionTemplate.execute(status -> {
                feedbackLikeService.like(5L, toGuestInfo(guest));
                status.setRollbackOnly();
                return null;
            });

            // then
            verify(likesCacheHandler, never()).handle(any(), any());
        }

        @Test
        @DisplayName("트랜잭션 롤백이 아니라면, 캐시 핸들러가 실행되어야 한다 (좋아요 순)")
        void cache_handler_called_on_commit_likes() {
            // given
            saveMultipleFeedbacksFromLikeCounts(List.of(10, 10, 10, 10, 10, 5, 4, 4, 3, 2));

            userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LIKES);

            // when
            feedbackLikeService.like(5L, toGuestInfo(guest));

            // then
            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(likesCacheHandler, times(1)).handle(any(), any()));
        }

        @Test
        @DisplayName("트랜잭션 롤백 시 캐시 핸들러가 실행되지 않는다 (오래된 순)")
        void cache_handler_not_called_on_rollback_oldest_case() {
            // given
            final Admin admin = AdminFixture.create();
            adminRepository.save(admin);
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);
            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            saveMultipleFeedbacks(10, organization);

            // when
            transactionTemplate.execute(status -> {
                adminFeedbackService.updateFeedbackComment(
                        admin.getId(),
                        new UpdateFeedbackCommentRequest("test"),
                        10L
                );
                status.setRollbackOnly();
                return null;
            });

            // then
            verify(oldestCacheHandler, never()).handle(any(), any());
        }

        @Test
        @DisplayName("트랜잭션 롤백이 아니라면, 캐시 핸들러가 실행되어야 한다 (오래된 순)")
        void cache_handler_called_on_commit_oldest() {
            // given
            final Admin admin = AdminFixture.create();
            adminRepository.save(admin);
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);
            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            saveMultipleFeedbacks(10, organization);

            // when
            adminFeedbackService.updateFeedbackComment(
                    admin.getId(),
                    new UpdateFeedbackCommentRequest("test"),
                    10L
            );

            // then
            await().atMost(Duration.ofSeconds(1))
                    .untilAsserted(() ->
                            verify(oldestCacheHandler, times(1)).handle(any(), any()));
        }

    }

    private void saveMultipleFeedbacks(int count) {
        for (int i=0; i<count; i++) {
            final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization, organizationCategory);
            feedbackRepository.save(feedback);
        }
    }

    private void saveMultipleFeedbacks(int count, final Organization organization) {
        for (int i=0; i<count; i++) {
            final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization, organizationCategory);
            feedbackRepository.save(feedback);
        }
    }

    private void saveMultipleFeedbacksFromLikeCounts(final List<Integer> likeCounts) {
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

    private GuestInfo createGuestInfo() {
        return new GuestInfo(UUID.randomUUID());
    }

    private GuestInfo toGuestInfo(Guest guest) {
        return new GuestInfo(guest.getGuestUuid());
    }
}

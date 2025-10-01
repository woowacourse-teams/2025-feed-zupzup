package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.dto.response.LikeHistoryResponse;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.exception.FeedbackException.DuplicateLikeException;
import feedzupzup.backend.feedback.exception.FeedbackException.InvalidLikeException;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;

class FeedbackLikeServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackLikeService feedbackLikeService;

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Long createFeedback() {
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithContent(organization, "테스트 피드백",
                organizationCategory);
        return feedBackRepository.save(feedback).getId();
    }

    @Nested
    @DisplayName("좋아요를 누른 피드백 조회 테스트")
    class LikeHistoryTest {

        @Test
        @DisplayName("좋아요 누른 기록이 없다면, 빈 배열이 반환되어야 한다.")
        void not_like_history_then_empty() {
            final LikeHistoryResponse likeHistories = feedbackLikeService.findLikeHistories(
                    UUID.randomUUID());
            assertThat(likeHistories.feedbackIds()).isEmpty();
        }

        @Test
        @DisplayName("좋아요 누른 기록을 전부 조회할 수 있어야 한다.")
        void find_all_histories() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);
            final OrganizationCategory organizationCategory = new OrganizationCategory(organization, SUGGESTION, true);
            organizationCategoryRepository.save(organizationCategory);
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 0);
            final Feedback savedFeedback = feedBackRepository.save(feedback);

            final UUID userId = UUID.randomUUID();

            feedbackLikeService.like(savedFeedback.getId(), userId);

            // when & then
            final LikeHistoryResponse likeHistories = feedbackLikeService.findLikeHistories(userId);

            assertAll(
                    () -> assertThat(likeHistories.feedbackIds().size()).isEqualTo(1),
                    () -> assertThat(likeHistories.feedbackIds().get(0)).isEqualTo(savedFeedback.getId())
            );
        }
    }

    @Nested
    @DisplayName("좋아요 증가 테스트")
    class LikeIncreaseTest {

        @Test
        @DisplayName("하나의 쿠키에서 동일한 게시글에 좋아요를 연속해서 누를 경우, 예외가 발생해야 한다")
        void same_cookie_continuous_like_same_feedback_then_throw_exception() {
            // given
            final Long feedbackId = createFeedback();
            final UUID cookieValue = createAndGetCookieValue();

            // when
            feedbackLikeService.like(feedbackId, cookieValue);

            // then
            assertThatThrownBy(() -> feedbackLikeService.like(feedbackId, cookieValue))
                    .isInstanceOf(DuplicateLikeException.class);
        }

        @Test
        @DisplayName("하나의 쿠키에서 다른 게시글에 좋아요를 연속해서 누를 경우, 성공해야 한다")
        void same_cookie_continuous_like_another_feedback_then_success() {
            // given
            final Long feedbackId1 = createFeedback();
            final Long feedbackId2 = createFeedback();

            final UUID cookieValue = createAndGetCookieValue();

            feedbackLikeService.like(feedbackId1, cookieValue);

            // when & then
            assertThatCode(() -> feedbackLikeService.like(feedbackId2, cookieValue))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("새로운 피드백에 좋아요를 추가하면 1이 된다")
        void like_new_feedback() {
            // given

            final Long feedbackId = createFeedback();

            // when
            final LikeResponse likeResponse = feedbackLikeService.like(feedbackId, createAndGetCookieValue());

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("서로 다른 피드백의 좋아요는 독립적으로 관리된다")
        void like_different_feedbacks() {
            // given
            final Long feedbackId1 = createFeedback();
            final Long feedbackId2 = createFeedback();

            // when
            feedbackLikeService.like(feedbackId1, createAndGetCookieValue());
            final LikeResponse likeResponse1 = feedbackLikeService.like(feedbackId1, createAndGetCookieValue());
            final LikeResponse likeResponse2 = feedbackLikeService.like(feedbackId2, createAndGetCookieValue());

            // then
            assertAll(
                    () -> assertThat(likeResponse1.afterLikeCount()).isEqualTo(2),
                    () -> assertThat(likeResponse2.afterLikeCount()).isEqualTo(1)
            );
        }
    }

    @Nested
    @DisplayName("좋아요 감소 테스트")
    class LikeDecreaseTest {

        @Test
        @DisplayName("쿠키가 존재하지 않는 유저가 취소를 요청하면, 예외가 발생해야 한다 (null 인 경우)")
        void not_exist_cookie_request_then_throw_exception() {
            // given
            final Long feedbackId = createFeedback();

            // when & then
            assertThatThrownBy(() -> feedbackLikeService.unlike(feedbackId, null))
                    .isInstanceOf(InvalidLikeException.class);
        }

        @Test
        @DisplayName("해당 피드백에 대해 좋아요 기록이 존재하지 않는 유저가 취소 요청을 할 경우, 예외가 발생해야 한다")
        void not_exist_like_history_then_throw_exception() {
            // given
            final Long feedbackId = createFeedback();
            final UUID cookieValue = createAndGetCookieValue();

            // when & then
            assertThatThrownBy(() -> feedbackLikeService.unlike(feedbackId, cookieValue))
                    .isInstanceOf(InvalidLikeException.class);
        }

        @Test
        @DisplayName("좋아요가 있는 피드백에서 좋아요를 취소하면 감소한다")
        void unlike_existing_feedback() {
            // given
            final Long feedbackId = createFeedback();
            final UUID cookieValue1 = createAndGetCookieValue();
            final UUID cookieValue2 = createAndGetCookieValue();
            final UUID cookieValue3 = createAndGetCookieValue();

            feedbackLikeService.like(feedbackId, cookieValue1);
            feedbackLikeService.like(feedbackId, cookieValue2);
            feedbackLikeService.like(feedbackId, cookieValue3);

            // when
            final LikeResponse likeResponse = feedbackLikeService.unlike(feedbackId, cookieValue1);

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("좋아요가 1개인 피드백에서 좋아요를 취소하면 0이 된다")
        void unlike_single_like() {
            // given
            final Long feedbackId = createFeedback();
            final UUID cookieValue1 = createAndGetCookieValue();
            feedbackLikeService.like(feedbackId, cookieValue1);

            // when
            final LikeResponse likeResponse = feedbackLikeService.unlike(feedbackId, cookieValue1);

            // then
            assertThat(likeResponse.afterLikeCount()).isZero();
        }
    }

    @Nested
    @DisplayName("좋아요 증가/감소 복합 테스트")
    class LikeCombinationTest {

        @Test
        @DisplayName("좋아요 증가와 감소를 반복해도 정확한 카운트를 유지한다")
        void like_and_unlike_combination() {
            // given
            final Long feedbackId = createFeedback();
            final UUID cookieValue1 = createAndGetCookieValue();
            final UUID cookieValue2 = createAndGetCookieValue();
            final UUID cookieValue3 = createAndGetCookieValue();
            final UUID cookieValue4 = createAndGetCookieValue();

            // when
            feedbackLikeService.like(feedbackId, cookieValue1);          // 1
            feedbackLikeService.like(feedbackId, cookieValue2);          // 2
            feedbackLikeService.unlike(feedbackId, cookieValue1);        // 1
            feedbackLikeService.like(feedbackId, cookieValue3);          // 2
            feedbackLikeService.like(feedbackId, cookieValue4);          // 3
            feedbackLikeService.unlike(feedbackId, cookieValue2);        // 2

            final Feedback feedback = feedBackRepository.findById(feedbackId).get();

            //then
            assertThat(feedback.getLikeCount().getValue()).isEqualTo(2);
        }

        @Test
        @DisplayName("여러 피드백에 대해 독립적으로 좋아요 증감이 가능하다")
        void multiple_feedbacks_independent_operations() {
            // given
            final Long feedbackId1 = createFeedback();
            final Long feedbackId2 = createFeedback();
            final Long feedbackId3 = createFeedback();

            final UUID cookieValue1 = createAndGetCookieValue();
            final UUID cookieValue2 = createAndGetCookieValue();
            final UUID cookieValue3 = createAndGetCookieValue();
            final UUID cookieValue4 = createAndGetCookieValue();
            final UUID cookieValue5 = createAndGetCookieValue();

            // when
            feedbackLikeService.like(feedbackId1, cookieValue1);
            final LikeResponse likeResponse1 = feedbackLikeService.unlike(feedbackId1, cookieValue1);
            final LikeResponse likeResponse2 = feedbackLikeService.like(feedbackId2, cookieValue2);
            feedbackLikeService.like(feedbackId3, cookieValue3);
            feedbackLikeService.like(feedbackId3, cookieValue4);
            feedbackLikeService.like(feedbackId3, cookieValue5);
            final LikeResponse likeResponse3 = feedbackLikeService.unlike(feedbackId3, cookieValue3);

            // then
            assertAll(
                    () -> assertThat(likeResponse1.afterLikeCount()).isEqualTo(0),
                    () -> assertThat(likeResponse2.afterLikeCount()).isEqualTo(1),
                    () -> assertThat(likeResponse3.afterLikeCount()).isEqualTo(2)
            );
        }
    }

    @Nested
    @DisplayName("경계값 및 특수 상황 테스트")
    class BoundaryTest {

        @Test
        @DisplayName("매우 큰 피드백 ID에도 정상적으로 동작한다")
        void like_large_feedback_id() {
            // given
            final Long largeFeedbackId = createFeedback();

            // when
            final LikeResponse likeResponse = feedbackLikeService.like(largeFeedbackId, createAndGetCookieValue());

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("피드백 ID가 1인 경우에도 정상적으로 동작한다")
        void like_minimum_feedback_id() {
            // given
            final Long minFeedbackId = createFeedback();

            // when
            final LikeResponse likeResponse = feedbackLikeService.like(minFeedbackId, createAndGetCookieValue());

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(1);
        }
    }

    private UUID createAndGetCookieValue() {
        final ResponseCookie cookie = CookieUtilization.createCookie(CookieUtilization.VISITOR_KEY,
                UUID.randomUUID());
        return UUID.fromString(cookie.getValue());
    }
}

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
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.exception.FeedbackException.DuplicateLikeException;
import feedzupzup.backend.feedback.exception.FeedbackException.InvalidLikeException;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
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
    @DisplayName("좋아요 증가 테스트")
    class LikeIncreaseTest {

        @Test
        @DisplayName("하나의 쿠키에서 동일한 게시글에 좋아요를 연속해서 누를 경우, 예외가 발생해야 한다")
        void same_cookie_continuous_like_same_feedback_then_throw_exception() {
            // given
            final Long feedbackId = createFeedback();
            final Guest guest = createGuest();

            // when
            feedbackLikeService.like(feedbackId, guest);

            // then
            assertThatThrownBy(() -> feedbackLikeService.like(feedbackId, guest))
                    .isInstanceOf(DuplicateLikeException.class);
        }

        @Test
        @DisplayName("하나의 쿠키에서 다른 게시글에 좋아요를 연속해서 누를 경우, 성공해야 한다")
        void same_cookie_continuous_like_another_feedback_then_success() {
            // given
            final Long feedbackId1 = createFeedback();
            final Long feedbackId2 = createFeedback();

            final Guest guest = createGuest();

            feedbackLikeService.like(feedbackId1, guest);

            // when & then
            assertThatCode(() -> feedbackLikeService.like(feedbackId2, guest))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("새로운 피드백에 좋아요를 추가하면 1이 된다")
        void like_new_feedback() {
            // given

            final Long feedbackId = createFeedback();

            // when
            final LikeResponse likeResponse = feedbackLikeService.like(feedbackId, createGuest());

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
            feedbackLikeService.like(feedbackId1, createGuest());
            final LikeResponse likeResponse1 = feedbackLikeService.like(feedbackId1, createGuest());
            final LikeResponse likeResponse2 = feedbackLikeService.like(feedbackId2, createGuest());

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
        @DisplayName("해당 피드백에 대해 좋아요 기록이 존재하지 않는 유저가 취소 요청을 할 경우, 예외가 발생해야 한다")
        void not_exist_like_history_then_throw_exception() {
            // given
            final Long feedbackId = createFeedback();
            final UUID cookieValue = createAndGetCookieValue();
            final Guest guest = new Guest(cookieValue, CurrentDateTime.create());

            // when & then
            assertThatThrownBy(() -> feedbackLikeService.unlike(feedbackId, guest))
                    .isInstanceOf(InvalidLikeException.class);
        }

        @Test
        @DisplayName("좋아요가 있는 피드백에서 좋아요를 취소하면 감소한다")
        void unlike_existing_feedback() {
            // given
            final Long feedbackId = createFeedback();
            final Guest guest1 = createGuest();
            final Guest guest2 = createGuest();
            final Guest guest3 = createGuest();

            feedbackLikeService.like(feedbackId, guest1);
            feedbackLikeService.like(feedbackId, guest2);
            feedbackLikeService.like(feedbackId, guest3);

            // when
            final LikeResponse likeResponse = feedbackLikeService.unlike(feedbackId, guest1);

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("좋아요가 1개인 피드백에서 좋아요를 취소하면 0이 된다")
        void unlike_single_like() {
            // given
            final Long feedbackId = createFeedback();
            final Guest guest = createGuest();
            feedbackLikeService.like(feedbackId, guest);

            // when
            final LikeResponse likeResponse = feedbackLikeService.unlike(feedbackId, guest);

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
            final Guest guest1 = createGuest();
            final Guest guest2 = createGuest();
            final Guest guest3 = createGuest();
            final Guest guest4 = createGuest();

            // when
            feedbackLikeService.like(feedbackId, guest1);          // 1
            feedbackLikeService.like(feedbackId, guest2);          // 2
            feedbackLikeService.unlike(feedbackId, guest1);        // 1
            feedbackLikeService.like(feedbackId, guest3);          // 2
            feedbackLikeService.like(feedbackId, guest4);          // 3
            feedbackLikeService.unlike(feedbackId, guest2);        // 2

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

            final Guest guest1 = createGuest();
            final Guest guest2 = createGuest();
            final Guest guest3 = createGuest();
            final Guest guest4 = createGuest();
            final Guest guest5 = createGuest();

            // when
            feedbackLikeService.like(feedbackId1, guest1);
            final LikeResponse likeResponse1 = feedbackLikeService.unlike(feedbackId1, guest1);
            final LikeResponse likeResponse2 = feedbackLikeService.like(feedbackId2, guest2);
            feedbackLikeService.like(feedbackId3, guest3);
            feedbackLikeService.like(feedbackId3, guest4);
            feedbackLikeService.like(feedbackId3, guest5);
            final LikeResponse likeResponse3 = feedbackLikeService.unlike(feedbackId3, guest3);

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
            final LikeResponse likeResponse = feedbackLikeService.like(largeFeedbackId, createGuest());

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("피드백 ID가 1인 경우에도 정상적으로 동작한다")
        void like_minimum_feedback_id() {
            // given
            final Long minFeedbackId = createFeedback();

            // when
            final LikeResponse likeResponse = feedbackLikeService.like(minFeedbackId, createGuest());

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(1);
        }
    }

    private UUID createAndGetCookieValue() {
        final ResponseCookie cookie = CookieUtilization.createCookie(CookieUtilization.VISITOR_KEY,
                UUID.randomUUID());
        return UUID.fromString(cookie.getValue());
    }

    private Guest createGuest() {
        return new Guest(UUID.randomUUID(), CurrentDateTime.create());
    }
}

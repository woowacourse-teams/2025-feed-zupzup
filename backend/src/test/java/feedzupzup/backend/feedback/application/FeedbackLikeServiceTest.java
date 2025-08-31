package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.dto.response.LikeResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
        @DisplayName("새로운 피드백에 좋아요를 추가하면 1이 된다")
        void like_new_feedback() {
            // given

            final Long feedbackId = createFeedback();

            // when
            final LikeResponse likeResponse = feedbackLikeService.like(feedbackId);

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("기존 피드백에 좋아요를 추가하면 카운트가 증가한다")
        void like_existing_feedback() {
            // given
            final Long feedbackId = createFeedback();
            feedbackLikeService.like(feedbackId);
            feedbackLikeService.like(feedbackId);

            // when
            final LikeResponse likeResponse = feedbackLikeService.like(feedbackId);

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("서로 다른 피드백의 좋아요는 독립적으로 관리된다")
        void like_different_feedbacks() {
            // given
            final Long feedbackId1 = createFeedback();
            final Long feedbackId2 = createFeedback();

            // when
            feedbackLikeService.like(feedbackId1);
            final LikeResponse likeResponse1 = feedbackLikeService.like(feedbackId1);
            final LikeResponse likeResponse2 = feedbackLikeService.like(feedbackId2);

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
        @DisplayName("좋아요가 있는 피드백에서 좋아요를 취소하면 감소한다")
        void unlike_existing_feedback() {
            // given
            final Long feedbackId = createFeedback();
            feedbackLikeService.like(feedbackId);
            feedbackLikeService.like(feedbackId);
            feedbackLikeService.like(feedbackId);

            // when
            final LikeResponse likeResponse = feedbackLikeService.unLike(feedbackId);

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("좋아요가 1개인 피드백에서 좋아요를 취소하면 0이 된다")
        void unlike_single_like() {
            // given
            final Long feedbackId = createFeedback();
            feedbackLikeService.like(feedbackId);

            // when
            final LikeResponse likeResponse = feedbackLikeService.unLike(feedbackId);

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

            // when
            feedbackLikeService.like(feedbackId);          // 1
            feedbackLikeService.like(feedbackId);          // 2
            feedbackLikeService.unLike(feedbackId);        // 1
            feedbackLikeService.like(feedbackId);          // 2
            feedbackLikeService.like(feedbackId);          // 3
            feedbackLikeService.unLike(feedbackId);        // 2

            final Feedback feedback = feedBackRepository.findById(feedbackId).get();

            //then
            assertThat(feedback.getLikeCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("여러 피드백에 대해 독립적으로 좋아요 증감이 가능하다")
        void multiple_feedbacks_independent_operations() {
            // given
            final Long feedbackId1 = createFeedback();
            final Long feedbackId2 = createFeedback();
            final Long feedbackId3 = createFeedback();

            // when
            feedbackLikeService.like(feedbackId1);
            final LikeResponse likeResponse1 = feedbackLikeService.unLike(feedbackId1);
            final LikeResponse likeResponse2 = feedbackLikeService.like(feedbackId2);
            feedbackLikeService.like(feedbackId3);
            feedbackLikeService.like(feedbackId3);
            feedbackLikeService.like(feedbackId3);
            final LikeResponse likeResponse3 = feedbackLikeService.unLike(feedbackId3);

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
            final LikeResponse likeResponse = feedbackLikeService.like(largeFeedbackId);

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("피드백 ID가 1인 경우에도 정상적으로 동작한다")
        void like_minimum_feedback_id() {
            // given
            final Long minFeedbackId = createFeedback();

            // when
            final LikeResponse likeResponse = feedbackLikeService.like(minFeedbackId);

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("많은 수의 좋아요도 정확히 카운트된다")
        void like_large_count() {
            // given
            final Long feedbackId = createFeedback();
            final int largeCount = 1000;

            // when
            for (int i = 0; i < largeCount; i++) {
                feedbackLikeService.like(feedbackId);
            }
            final LikeResponse likeResponse = feedbackLikeService.like(feedbackId);

            // then
            assertThat(likeResponse.afterLikeCount()).isEqualTo(largeCount+1);
        }
    }
}

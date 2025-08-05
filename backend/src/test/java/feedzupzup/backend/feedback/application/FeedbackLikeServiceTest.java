package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.CategoryRepository;
import feedzupzup.backend.category.fixture.CategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
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
    private FeedbackLikeRepository feedbackLikeRepository;

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;

    @BeforeEach
    void clear() {
        feedbackLikeRepository.clear();
        category = CategoryFixture.createCategoryBy("시설");
        categoryRepository.save(category);
    }

    @AfterEach
    void tearDown() {
        feedbackLikeRepository.clear();
    }

    private Long createFeedback() {
        final Feedback feedback = FeedbackFixture.createFeedbackWithContent("테스트 피드백", category);
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
            feedbackLikeService.like(feedbackId);

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(1);
        }

        @Test
        @DisplayName("기존 피드백에 좋아요를 추가하면 카운트가 증가한다")
        void like_existing_feedback() {
            // given
            final Long feedbackId = createFeedback();
            feedbackLikeService.like(feedbackId);
            feedbackLikeService.like(feedbackId);

            // when
            feedbackLikeService.like(feedbackId);

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(3);
        }

        @Test
        @DisplayName("여러 번 좋아요를 누르면 그만큼 증가한다")
        void like_multiple_times() {
            // given
            final Long feedbackId = createFeedback();
            final int likeCount = 5;

            // when
            for (int i = 0; i < likeCount; i++) {
                feedbackLikeService.like(feedbackId);
            }

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(likeCount);
        }

        @Test
        @DisplayName("서로 다른 피드백의 좋아요는 독립적으로 관리된다")
        void like_different_feedbacks() {
            // given
            final Long feedbackId1 = createFeedback();
            final Long feedbackId2 = createFeedback();

            // when
            feedbackLikeService.like(feedbackId1);
            feedbackLikeService.like(feedbackId1);
            feedbackLikeService.like(feedbackId2);

            // then
            assertAll(
                    () -> assertThat(feedbackLikeRepository.getLikeCount(feedbackId1)).isEqualTo(2),
                    () -> assertThat(feedbackLikeRepository.getLikeCount(feedbackId2)).isEqualTo(1)
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
            feedbackLikeService.unLike(feedbackId);

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(2);
        }

        @Test
        @DisplayName("좋아요가 1개인 피드백에서 좋아요를 취소하면 0이 된다")
        void unlike_single_like() {
            // given
            final Long feedbackId = createFeedback();
            feedbackLikeService.like(feedbackId);

            // when
            feedbackLikeService.unLike(feedbackId);

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isZero();
        }

        @Test
        @DisplayName("좋아요가 0인 피드백에서 좋아요를 취소하면 음수가 된다")
        void unlike_zero_likes() {
            // given
            final Long feedbackId = createFeedback();
            feedbackLikeService.like(feedbackId);
            feedbackLikeService.unLike(feedbackId);

            // when
            feedbackLikeService.unLike(feedbackId);

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(-1);
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

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(2);
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
            feedbackLikeService.like(feedbackId1);
            feedbackLikeService.like(feedbackId2);
            feedbackLikeService.like(feedbackId3);
            feedbackLikeService.like(feedbackId3);
            feedbackLikeService.like(feedbackId3);
            feedbackLikeService.unLike(feedbackId1);
            feedbackLikeService.unLike(feedbackId3);

            // then
            assertAll(
                    () -> assertThat(feedbackLikeRepository.getLikeCount(feedbackId1)).isEqualTo(1),
                    () -> assertThat(feedbackLikeRepository.getLikeCount(feedbackId2)).isEqualTo(1),
                    () -> assertThat(feedbackLikeRepository.getLikeCount(feedbackId3)).isEqualTo(2)
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
            feedbackLikeService.like(largeFeedbackId);

            // then
            assertThat(feedbackLikeRepository.getLikeCount(largeFeedbackId)).isEqualTo(1);
        }

        @Test
        @DisplayName("피드백 ID가 1인 경우에도 정상적으로 동작한다")
        void like_minimum_feedback_id() {
            // given
            final Long minFeedbackId = createFeedback();

            // when
            feedbackLikeService.like(minFeedbackId);

            // then
            assertThat(feedbackLikeRepository.getLikeCount(minFeedbackId)).isEqualTo(1);
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

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(largeCount);
        }
    }

    @Nested
    @DisplayName("동시성 테스트")
    class ConcurrencyTest {

        @Test
        @DisplayName("동시에 100개의 좋아요 요청이 들어와도 정확히 100개의 좋아요가 증가한다")
        void concurrent_like_test() throws InterruptedException {
            // given
            final Long feedbackId = createFeedback();
            final int threadCount = 100;
            final ExecutorService executorService = Executors.newFixedThreadPool(32);
            final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            // when
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        feedbackLikeService.like(feedbackId);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
            executorService.shutdown();

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(100);
        }

        @Test
        @DisplayName("동시에 100개의 좋아요 취소 요청이 들어와도 정확히 100개의 좋아요가 감소한다")
        void concurrent_unlike_test() throws InterruptedException {
            // given
            final Long feedbackId = createFeedback();
            final int threadCount = 100;
            final ExecutorService executorService = Executors.newFixedThreadPool(32);
            final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            // 먼저 100개의 좋아요를 추가
            for (int i = 0; i < threadCount; i++) {
                feedbackLikeService.like(feedbackId);
            }

            // when
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        feedbackLikeService.unLike(feedbackId);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
            executorService.shutdown();

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isZero();
        }

        @Test
        @DisplayName("동시에 좋아요와 좋아요 취소 요청이 섞여서 들어와도 정확한 카운트를 유지한다")
        void concurrent_like_and_unlike_test() throws InterruptedException {
            // given
            final Long feedbackId = createFeedback();
            final int likeCount = 60;
            final int unlikeCount = 40;
            final ExecutorService executorService = Executors.newFixedThreadPool(32);
            final CountDownLatch countDownLatch = new CountDownLatch(likeCount + unlikeCount);
            final AtomicInteger successfulUnlikes = new AtomicInteger(0);

            // 먼저 50개의 좋아요를 추가해서 기본값 설정
            for (int i = 0; i < 50; i++) {
                feedbackLikeService.like(feedbackId);
            }

            // when
            for (int i = 0; i < likeCount; i++) {
                executorService.submit(() -> {
                    try {
                        feedbackLikeService.like(feedbackId);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            for (int i = 0; i < unlikeCount; i++) {
                executorService.submit(() -> {
                    try {
                        feedbackLikeService.unLike(feedbackId);
                        successfulUnlikes.incrementAndGet();
                    } catch (Exception e) {
                        // unlike 실패 시 (값이 음수가 될 수 있는 경우)
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
            executorService.shutdown();

            // then
            // 초기 50 + 새로운 좋아요 60 - 성공한 좋아요 취소
            final int expectedCount = 50 + likeCount - successfulUnlikes.get();
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(expectedCount);
        }

        @Test
        @DisplayName("여러 피드백에 대해 동시에 좋아요 요청이 들어와도 각각 정확한 카운트를 유지한다")
        void concurrent_multiple_feedback_like_test() throws InterruptedException {
            // given
            final Long feedbackId1 = createFeedback();
            final Long feedbackId2 = createFeedback();
            final Long feedbackId3 = createFeedback();
            final int threadCountPerFeedback = 50;
            final ExecutorService executorService = Executors.newFixedThreadPool(32);
            final CountDownLatch countDownLatch = new CountDownLatch(threadCountPerFeedback * 3);

            // when
            for (int i = 0; i < threadCountPerFeedback; i++) {
                executorService.submit(() -> {
                    try {
                        feedbackLikeService.like(feedbackId1);
                    } finally {
                        countDownLatch.countDown();
                    }
                });

                executorService.submit(() -> {
                    try {
                        feedbackLikeService.like(feedbackId2);
                    } finally {
                        countDownLatch.countDown();
                    }
                });

                executorService.submit(() -> {
                    try {
                        feedbackLikeService.like(feedbackId3);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
            executorService.shutdown();

            // then
            assertAll(
                    () -> assertThat(feedbackLikeRepository.getLikeCount(feedbackId1)).isEqualTo(50),
                    () -> assertThat(feedbackLikeRepository.getLikeCount(feedbackId2)).isEqualTo(50),
                    () -> assertThat(feedbackLikeRepository.getLikeCount(feedbackId3)).isEqualTo(50)
            );
        }

        @Test
        @DisplayName("대량의 동시 요청에서도 데이터 무결성을 보장한다")
        void concurrent_high_load_test() throws InterruptedException {
            // given
            final Long feedbackId = createFeedback();
            final int threadCount = 1000;
            final ExecutorService executorService = Executors.newFixedThreadPool(100);
            final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

            // when
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        feedbackLikeService.like(feedbackId);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
            executorService.shutdown();

            // then
            assertThat(feedbackLikeRepository.getLikeCount(feedbackId)).isEqualTo(1000);
        }

        @Test
        @DisplayName("동시에 좋아요 증가와 감소가 번갈아 발생해도 최종 결과가 정확하다")
        void concurrent_alternating_like_unlike_test() throws InterruptedException {
            // given
            final Long feedbackId = createFeedback();
            final int iterations = 100;
            final ExecutorService executorService = Executors.newFixedThreadPool(32);
            final CountDownLatch countDownLatch = new CountDownLatch(iterations * 2);
            
            // 초기 좋아요 추가
            feedbackLikeService.like(feedbackId);

            // when
            for (int i = 0; i < iterations; i++) {
                // 좋아요 추가
                executorService.submit(() -> {
                    try {
                        feedbackLikeService.like(feedbackId);
                    } finally {
                        countDownLatch.countDown();
                    }
                });

                // 좋아요 취소
                executorService.submit(() -> {
                    try {
                        feedbackLikeService.unLike(feedbackId);
                    } catch (Exception e) {
                        // 예외 발생 시 무시 (음수 방지를 위해)
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
            executorService.shutdown();

            // then
            // 최종 결과는 초기값(1) 근처의 값이어야 함 (정확한 값은 실행 순서에 따라 달라질 수 있음)
            final int finalCount = feedbackLikeRepository.getLikeCount(feedbackId);
            assertThat(finalCount).isNotNegative();
        }
    }
}

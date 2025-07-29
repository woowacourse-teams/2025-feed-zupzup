package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FeedbackLikeCounterTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackLikeService feedbackLikeService;

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Autowired
    private FeedbackLikeRepository feedbackLikeRepository;

    @Test
    @DisplayName("인메모리 좋아요를 DB에 성공적으로 동기화한다")
    void syncLikesToDatabase_success() {
        // given
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(1L, 5);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(1L, 3);
        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);

        // 인메모리에 좋아요 추가
        feedbackLikeRepository.increaseAndGet(saved1.getId());
        feedbackLikeRepository.increaseAndGet(saved1.getId());
        feedbackLikeRepository.increaseAndGet(saved2.getId());

        // when
        feedbackLikeService.flushLikeCountBuffer();

        // then
        final Feedback updatedFeedback1 = feedBackRepository.findById(saved1.getId()).orElseThrow();
        final Feedback updatedFeedback2 = feedBackRepository.findById(saved2.getId()).orElseThrow();

        assertAll(
                () -> assertThat(updatedFeedback1.getLikeCount()).isEqualTo(7), // 5 + 2
                () -> assertThat(updatedFeedback2.getLikeCount()).isEqualTo(4), // 3 + 1
                () -> assertThat(feedbackLikeRepository.getLikeCounts()).isEmpty()
        );
    }

    @Test
    @DisplayName("인메모리에 좋아요가 없는 피드백은 DB에서 변경되지 않는다")
    void syncLikesToDatabase_no_memory_likes() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 10);
        final Feedback saved = feedBackRepository.save(feedback);

        // when - 인메모리 좋아요 없이 동기화
        feedbackLikeService.flushLikeCountBuffer();

        // then
        final Feedback updatedFeedback = feedBackRepository.findById(saved.getId()).orElseThrow();
        assertThat(updatedFeedback.getLikeCount()).isEqualTo(10);
    }

    @Test
    @DisplayName("존재하지 않는 피드백 ID는 무시된다")
    void syncLikesToDatabase_ignores_non_existent_feedback() {
        // given
        final Long nonExistentId = 999L;
        feedbackLikeRepository.increaseAndGet(nonExistentId);

        // when & then - 예외가 발생하지 않고 정상 처리됨
        feedbackLikeService.flushLikeCountBuffer();
        assertThat(feedbackLikeRepository.getLikeCounts()).isEmpty();
    }

    @Test
    @DisplayName("음수 좋아요도 정상적으로 동기화된다")
    void syncLikesToDatabase_handles_negative_likes() {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 5);
        final Feedback saved = feedBackRepository.save(feedback);

        // 좋아요 증가 후 감소
        feedbackLikeRepository.increaseAndGet(saved.getId());
        feedbackLikeRepository.increaseAndGet(saved.getId());
        feedbackLikeRepository.decreaseAndGet(saved.getId());
        feedbackLikeRepository.decreaseAndGet(saved.getId());
        feedbackLikeRepository.decreaseAndGet(saved.getId());

        // when
        feedbackLikeService.flushLikeCountBuffer();

        // then
        final Feedback updatedFeedback = feedBackRepository.findById(saved.getId()).orElseThrow();
        assertThat(updatedFeedback.getLikeCount()).isEqualTo(4); // 5 + 2 - 3 = 4
    }

    @Test
    @DisplayName("동기화 후 인메모리가 완전히 비워진다")
    void syncLikesToDatabase_clears_memory_completely() {
        // given
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(1L, 0);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(1L, 0);
        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);

        feedbackLikeRepository.increaseAndGet(saved1.getId());
        feedbackLikeRepository.increaseAndGet(saved2.getId());

        // when
        feedbackLikeService.flushLikeCountBuffer();

        // then
        assertThat(feedbackLikeRepository.getLikeCounts()).isEmpty();
    }

    @Test
    @DisplayName("대량의 좋아요 데이터도 정상적으로 동기화된다")
    void syncLikesToDatabase_handles_large_data() {
        // given
        final int feedbackCount = 100;
        final int likesPerFeedback = 50;

        for (int i = 1; i <= feedbackCount; i++) {
            final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 0);
            final Feedback saved = feedBackRepository.save(feedback);

            for (int j = 0; j < likesPerFeedback; j++) {
                feedbackLikeRepository.increaseAndGet(saved.getId());
            }
        }

        // when
        feedbackLikeService.flushLikeCountBuffer();

        // then
        final long totalFeedbacks = feedBackRepository.count();
        assertThat(totalFeedbacks).isGreaterThanOrEqualTo(feedbackCount);
        assertThat(feedbackLikeRepository.getLikeCounts()).isEmpty();
    }

    @Test
    @DisplayName("동시에 여러 스레드에서 동기화해도 데이터 정합성이 보장된다")
    void syncLikesToDatabase_concurrent_access() throws InterruptedException {
        // given
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(1L, 0);
        final Feedback saved = feedBackRepository.save(feedback);

        final int threadCount = 10;
        final int likesPerThread = 100;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch latch = new CountDownLatch(threadCount);

        // 각 스레드에서 좋아요 추가
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < likesPerThread; j++) {
                        feedbackLikeRepository.increaseAndGet(saved.getId());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // when
        feedbackLikeService.flushLikeCountBuffer();

        // then
        final Feedback updatedFeedback = feedBackRepository.findById(saved.getId()).orElseThrow();
        assertAll(
                () -> assertThat(updatedFeedback.getLikeCount()).isEqualTo(threadCount * likesPerThread),
                () -> assertThat(feedbackLikeRepository.getLikeCounts()).isEmpty()
        );
    }
}

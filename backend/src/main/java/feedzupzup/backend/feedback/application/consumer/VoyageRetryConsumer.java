package feedzupzup.backend.feedback.application.consumer;

import com.github.sonus21.rqueue.annotation.RqueueListener;
import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.feedback.application.VoyageRetryQueueService;
import feedzupzup.backend.feedback.application.dto.VoyageRetryTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Voyage AI 재시도 Rqueue Consumer
 *
 * Rqueue가 재시도 관리:
 * - 5번 재시도 (랜덤 지수 백오프)
 *   1차: 0~1초, 2차: 1~2초, 3차: 2~4초, 4차: 4~8초, 5차: 8~16초
 * - 모두 실패 시 DLQ로 이동
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VoyageRetryConsumer {

    private static final String VOYAGE_RETRY_QUEUE = "voyage-retry-queue";
    private static final String VOYAGE_RETRY_DLQ = "voyage-retry-dlq";

    private final FeedbackClusteringService clusteringService;
    private final VoyageRetryQueueService voyageRetryQueueService;

    /**
     * 메인 큐 리스너
     *
     * @param task 재시도 작업
     *
     * numRetries: 5회 재시도 (총 6번 실행: 최초 1회 + 재시도 5회)
     * visibilityTimeout: 메시지 가시성 타임아웃 (60초)
     * deadLetterQueue: 5회 실패 후 DLQ로 이동
     * concurrency: 10개 스레드로 병렬 처리
     */
    @RqueueListener(
            value = VOYAGE_RETRY_QUEUE,
            numRetries = "5",
            visibilityTimeout = "60000",
            deadLetterQueue = VOYAGE_RETRY_DLQ,
            concurrency = "10"
    )
    public void consumeRetryTask(VoyageRetryTask task) {
        Long feedbackId = task.getFeedbackId();
        try {
            clusteringService.clusterForRetry(feedbackId);
            voyageRetryQueueService.deleteOutboxByFeedbackId(feedbackId);

        } catch (Exception e) {
            log.error("[Consumer] 처리 실패: feedbackId={}", feedbackId, e);
            throw new RuntimeException("Voyage AI 재시도 실패", e);
        }
    }

    /**
     * DLQ 리스너 (최종 실패 처리)
     *
     * 5회 재시도 모두 실패 시 호출
     * DLQ는 Redis에서 관리, 수동 처리 필요
     */
    @RqueueListener(value = VOYAGE_RETRY_DLQ, concurrency = "1")
    public void consumeDLQ(VoyageRetryTask task) {
        Long feedbackId = task.getFeedbackId();

        log.error("====================================================");
        log.error("[DLQ] 최종 실패 - 수동 처리 필요!");
        log.error("[DLQ] feedbackId: {}", feedbackId);
        log.error("[DLQ] 총 6회 시도 실패 (최초 1회 + 재시도 5회)");
        log.error("[DLQ] Redis DLQ에서 확인 가능: {}", VOYAGE_RETRY_DLQ);
        log.error("====================================================");

        // Discord 알림 발송 구현
    }
}

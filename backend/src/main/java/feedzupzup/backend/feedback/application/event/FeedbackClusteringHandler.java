package feedzupzup.backend.feedback.application.event;

import static feedzupzup.backend.global.async.TargetType.*;
import static feedzupzup.backend.global.async.TaskType.*;

import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.feedback.domain.event.FeedbackCreatedEvent2;
import feedzupzup.backend.global.async.AsyncTaskFailureService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedbackClusteringHandler {

    private final FeedbackClusteringService feedbackClusteringService;
    private final AsyncTaskFailureService asyncTaskFailureService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("externalApiExecutor")
    public void handleFeedbackCreatedEvent(final FeedbackCreatedEvent2 event) {
        cluster(event.feedbackId())
                .ifPresent(this::createLabel);
    }

    private Optional<Long> cluster(final Long feedbackId) {
        try {
            return Optional.of(feedbackClusteringService.cluster(feedbackId));
        } catch (Exception ex) {
            log.error("피드백 클러스터링 작업 최종 실패: 피드백ID={}", feedbackId, ex);
            Long failureId = asyncTaskFailureService.recordFailure(FEEDBACK_CLUSTERING, FEEDBACK_CLUSTER, feedbackId.toString(), ex);
            asyncTaskFailureService.alertFinalFail(failureId);
            return Optional.empty();
        }
    }

    private void createLabel(final Long createdFeedbackClusterId) {
        if (createdFeedbackClusterId == null) {
            return;
        }
        try {
            feedbackClusteringService.createLabel(createdFeedbackClusterId);
        } catch (Exception ex) {
            log.error("피드백 클러스터링 라벨 생성 작업 최종 실패: 피드백 클러스터 ID={}", createdFeedbackClusterId, ex);
            Long failureId = asyncTaskFailureService.recordFailure(CLUSTER_LABEL_GENERATION, FEEDBACK, createdFeedbackClusterId.toString(), ex);
            asyncTaskFailureService.alertFinalFail(failureId);
        }
    }
}

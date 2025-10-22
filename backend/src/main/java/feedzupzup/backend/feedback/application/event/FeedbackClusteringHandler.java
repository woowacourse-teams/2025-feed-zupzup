package feedzupzup.backend.feedback.application.event;

import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.event.FeedbackCreatedEvent2;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class FeedbackClusteringHandler {

    private final FeedbackClusteringService feedbackClusteringService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleFeedbackCreatedEvent(final FeedbackCreatedEvent2 event) {
        FeedbackEmbeddingCluster createdCluster = feedbackClusteringService.cluster(event.feedbackId());
        feedbackClusteringService.createLabel(createdCluster.getId());
    }
}

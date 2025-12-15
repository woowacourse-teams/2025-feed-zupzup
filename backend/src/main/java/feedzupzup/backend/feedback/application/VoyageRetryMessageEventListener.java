package feedzupzup.backend.feedback.application;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import feedzupzup.backend.feedback.application.dto.VoyageRetryTask;
import feedzupzup.backend.feedback.domain.VoyageRetryOutbox;
import feedzupzup.backend.feedback.domain.VoyageRetryOutboxRepository;
import feedzupzup.backend.feedback.domain.event.VoyageRetryOutboxCreatedEvent;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoyageRetryMessageEventListener {

    private static final String VOYAGE_RETRY_EXECUTION_QUEUE = "voyage-retry-execution-queue";

    private final RqueueMessageEnqueuer rqueueMessageEnqueuer;
    private final VoyageRetryOutboxRepository voyageRetryOutboxRepository;

    @Async("retryExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOutboxCreated(final VoyageRetryOutboxCreatedEvent event) {
        final Long feedbackId = event.feedbackId();
        final VoyageRetryTask task = VoyageRetryTask.of(feedbackId);
        try {
            rqueueMessageEnqueuer.enqueue(VOYAGE_RETRY_EXECUTION_QUEUE, task);
            voyageRetryOutboxRepository.deleteByFeedbackId(feedbackId);
        } catch (Exception e) {
            final VoyageRetryOutbox voyageRetryOutbox = voyageRetryOutboxRepository.findByFeedbackId(feedbackId)
                    .orElseThrow(() -> new ResourceNotFoundException("voyage_retry_outbox에 존재하지 않는 feedback 입니다."));
            voyageRetryOutbox.updateErrorMessage(e.getMessage());
        }
    }
}

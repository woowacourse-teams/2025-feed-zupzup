package feedzupzup.backend.feedback.application;

import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import feedzupzup.backend.feedback.application.dto.VoyageRetryTask;
import feedzupzup.backend.feedback.domain.VoyageRetryOutboxRepository;
import feedzupzup.backend.feedback.domain.event.OutboxCreatedEvent;
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

    private static final String VOYAGE_RETRY_QUEUE = "voyage-retry-queue";

    private final RqueueMessageEnqueuer rqueueMessageEnqueuer;
    private final VoyageRetryOutboxRepository outboxRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOutboxCreated(final OutboxCreatedEvent event) {
        final Long feedbackId = event.getFeedbackId();
        final VoyageRetryTask task = VoyageRetryTask.of(feedbackId);
        rqueueMessageEnqueuer.enqueue(VOYAGE_RETRY_QUEUE, task);

        try {
            outboxRepository.deleteByFeedbackId(feedbackId);
        } catch (Exception e) {
            log.warn("Redis 메세지 발송 후 Outbox에서 메세지 삭제 실패");
        }

    }
}

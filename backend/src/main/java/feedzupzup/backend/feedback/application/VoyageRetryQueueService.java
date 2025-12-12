package feedzupzup.backend.feedback.application;

import feedzupzup.backend.feedback.domain.VoyageRetryOutbox;
import feedzupzup.backend.feedback.domain.VoyageRetryOutboxRepository;
import feedzupzup.backend.feedback.domain.event.OutboxCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoyageRetryQueueService {

    private final VoyageRetryOutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void scheduleRetryWithOutbox(final Long feedbackId, final String errorMessage) {
        VoyageRetryOutbox outbox = VoyageRetryOutbox.create(feedbackId, errorMessage);
        outboxRepository.save(outbox);
        OutboxCreatedEvent event = OutboxCreatedEvent.of(feedbackId);
        eventPublisher.publishEvent(event);
    }

    @Transactional
    public void deleteOutboxByFeedbackId(final Long feedbackId) {
        outboxRepository.deleteByFeedbackId(feedbackId);
    }
}

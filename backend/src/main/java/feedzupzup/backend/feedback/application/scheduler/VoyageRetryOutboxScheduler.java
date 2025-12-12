package feedzupzup.backend.feedback.application.scheduler;

import feedzupzup.backend.feedback.domain.VoyageRetryOutbox;
import feedzupzup.backend.feedback.domain.VoyageRetryOutboxRepository;
import feedzupzup.backend.feedback.domain.event.OutboxCreatedEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoyageRetryOutboxScheduler {

    private final VoyageRetryOutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void pollOutboxAndRetry() {
        List<VoyageRetryOutbox> outboxes = outboxRepository.findAll();

        if (outboxes.isEmpty()) {
            return;
        }

        for (VoyageRetryOutbox outbox : outboxes) {
            try {
                OutboxCreatedEvent event = OutboxCreatedEvent.of(outbox.getFeedbackId());
                eventPublisher.publishEvent(event);
                log.info("폴링 성공: feedbackId={} 이벤트 재발행", outbox.getFeedbackId());
            } catch (Exception e) {
                log.error("폴링 실패: feedbackId={} 이벤트 발행 중 오류 발생", outbox.getFeedbackId(), e);
            }
        }

        log.info("폴링 완료: {}개 메시지 처리 시도 완료", outboxes.size());
    }
}

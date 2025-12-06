package feedzupzup.backend.feedback.application.event;

import feedzupzup.backend.feedback.event.OrganizationFeedbackCountEvent;
import feedzupzup.backend.organization.application.OrganizationStatisticService;
import feedzupzup.backend.organization.domain.FeedbackAmount;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.organization.dto.response.OrganizationStatisticResponse;
import feedzupzup.backend.sse.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class OrganizationFeedbackCountHandler {

    private final OrganizationStatisticRepository organizationStatisticRepository;
    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleFeedbackCreatedEvent(final OrganizationFeedbackCountEvent organizationFeedbackCountEvent) {
        final FeedbackAmount feedbackAmount = organizationStatisticRepository.findFeedbackAmountByOrganizationId(
                organizationFeedbackCountEvent.organizationId());

        sseService.sendFeedbackNotificationToOrganization(
                organizationFeedbackCountEvent.organizationId(),
                feedbackAmount.getFeedbackTotalCount()
        );
    }
}

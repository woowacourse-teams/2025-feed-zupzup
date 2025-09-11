package feedzupzup.backend.notification.event;

import feedzupzup.backend.auth.application.SessionValidationService;
import feedzupzup.backend.feedback.event.FeedbackCreatedEvent;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.notification.application.PushNotifier;
import feedzupzup.backend.notification.domain.NotificationPayload;
import feedzupzup.backend.notification.exception.NotificationException;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final PushNotifier pushNotifier;
    private final OrganizerRepository organizerRepository;
    private final OrganizationRepository organizationRepository;
    private final SessionValidationService sessionValidationService;

    @Async
    @EventListener
    public void handleFeedbackCreatedEvent(FeedbackCreatedEvent event) {
        log.info("알림 이벤트 수신: organizationId={}", event.organizationId());
        try {
            Organization organization = organizationRepository.findById(event.organizationId())
                    .orElseThrow(() -> new ResourceNotFoundException("조직을 찾을 수 없습니다."));

            List<Organizer> organizers = organizerRepository.findByOrganizationId(event.organizationId());
            
            List<Long> candidateAdminIds = organizers.stream()
                    .map(Organizer::getAdminId)
                    .toList();
            
            List<Long> validSessionAdminIds = sessionValidationService.getValidSessionAdminIds(candidateAdminIds);
            log.debug("알림 대상 관리자 수: {}", validSessionAdminIds.size());
            if (!validSessionAdminIds.isEmpty()) {
                String organizationName = organization.getName().getValue();
                List<NotificationPayload> payloads = validSessionAdminIds.stream()
                        .map(adminId -> new NotificationPayload(adminId, event.title(), organizationName))
                        .toList();
                pushNotifier.sendBatchMessage(payloads);
                log.info(organizationName + "알림 전송 완료");
            }
        } catch (NotificationException e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}

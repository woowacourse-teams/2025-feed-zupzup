package feedzupzup.backend.notification.event;

import feedzupzup.backend.notification.application.PushNotifier;
import feedzupzup.backend.notification.domain.NotificationPayload;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final PushNotifier pushNotifier;

    @EventListener
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("알림 이벤트 수신: adminCount={}, organizationName={}", 
                event.adminIds().size(), event.organizationName());

        List<NotificationPayload> payloads = event.adminIds().stream()
                .map(adminId -> new NotificationPayload(adminId, event.title(), event.organizationName()))
                .toList();

        pushNotifier.sendBatchMessage(payloads);
        
        log.info("배치 알림 처리 요청 완료: {}명의 관리자에게 발송 요청", event.adminIds().size());
    }
}

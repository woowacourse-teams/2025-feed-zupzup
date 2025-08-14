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
        try {
            List<NotificationPayload> payloads = event.adminIds().stream()
                    .map(adminId -> new NotificationPayload(adminId, event.title(), event.organizationName()))
                    .toList();
            pushNotifier.sendBatchMessage(payloads);
        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}

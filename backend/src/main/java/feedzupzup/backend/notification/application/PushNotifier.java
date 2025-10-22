package feedzupzup.backend.notification.application;

import feedzupzup.backend.notification.domain.NotificationPayload;

import java.util.List;
import java.util.UUID;

public interface PushNotifier {

    void sendBatchMessage(List<NotificationPayload> payloads, final UUID organizationUuid);
}

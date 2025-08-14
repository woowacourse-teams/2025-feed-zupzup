package feedzupzup.backend.notification.application;

import feedzupzup.backend.notification.domain.NotificationPayload;

import java.util.List;

public interface PushNotifier {

    void sendMessage(NotificationPayload notificationPayload);
    
    void sendBatchMessage(List<NotificationPayload> payloads);
}

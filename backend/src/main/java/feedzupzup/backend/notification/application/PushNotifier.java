package feedzupzup.backend.notification.application;

import feedzupzup.backend.notification.domain.NotificationPayload;

import java.util.List;

public interface PushNotifier {

    void sendBatchMessage(List<NotificationPayload> payloads);
}

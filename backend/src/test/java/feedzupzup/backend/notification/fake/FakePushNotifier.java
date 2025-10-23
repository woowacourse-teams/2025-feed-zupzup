package feedzupzup.backend.notification.fake;

import feedzupzup.backend.notification.application.PushNotifier;
import feedzupzup.backend.notification.domain.NotificationPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakePushNotifier implements PushNotifier {

    private final List<NotificationPayload> sentPayloads = new ArrayList<>();
    private boolean shouldThrowException = false;
    private RuntimeException exceptionToThrow;

    @Override
    public void sendBatchMessage(List<NotificationPayload> payloads, final UUID organizationUuid) {
        if (shouldThrowException) {
            throw exceptionToThrow;
        }
        sentPayloads.addAll(payloads);
    }

    public List<NotificationPayload> getSentPayloads() {
        return new ArrayList<>(sentPayloads);
    }

    public int getSentCount() {
        return sentPayloads.size();
    }

    public void simulateException(RuntimeException exception) {
        this.shouldThrowException = true;
        this.exceptionToThrow = exception;
    }

    public void clear() {
        sentPayloads.clear();
        shouldThrowException = false;
        exceptionToThrow = null;
    }
}

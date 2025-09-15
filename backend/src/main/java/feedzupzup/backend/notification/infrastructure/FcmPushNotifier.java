package feedzupzup.backend.notification.infrastructure;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import feedzupzup.backend.notification.application.PushNotifier;
import feedzupzup.backend.notification.domain.NotificationPayload;
import feedzupzup.backend.notification.domain.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmPushNotifier implements PushNotifier {

    private final NotificationRepository notificationRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final FcmErrorHandler fcmErrorHandler;

    @Override
    public void sendBatchMessage(List<NotificationPayload> payloads) {
        if (payloads.isEmpty()) {
            log.warn("전송할 NotificationPayload가 없습니다.");
            return;
        }

        String title = payloads.get(0).title();
        String organizationName = payloads.get(0).organizationName();
        String message = String.format("%s에 피드백이 등록되었습니다.", organizationName);

        List<String> tokens = new ArrayList<>();

        for (NotificationPayload payload : payloads) {
            log.info("adminId: {}", payload.adminId());
            List<feedzupzup.backend.notification.domain.Notification> userTokens = notificationRepository.findByAdminId(payload.adminId());
            for (feedzupzup.backend.notification.domain.Notification token : userTokens) {
                log.info("tokenOpt: {}", token.getValue());
                tokens.add(token.getValue());
            }
        }

        if (tokens.isEmpty()) {
            log.warn("전송할 유효한 토큰이 없습니다. payloadCount: {}", payloads.size());
            return;
        }

        log.info("배치 FCM 전송 시작: {}개 토큰", tokens.size());

        int chunkSize = 500;
        for (int i = 0; i < tokens.size(); i += chunkSize) {
            List<String> tokenChunk = tokens.subList(i, Math.min(i + chunkSize, tokens.size()));
            sendBatch(tokenChunk, title, message);
        }
    }

    private void sendBatch(List<String> tokens, String title, String message) {
        try {
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .addAllTokens(tokens)
                    .build();

            BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);

            if (response.getFailureCount() > 0) {
                fcmErrorHandler.handleFailures(response, tokens);
            }

        } catch (Exception e) {
            log.error("FCM 배치 전송 중 예외 발생", e);
        }
    }
}

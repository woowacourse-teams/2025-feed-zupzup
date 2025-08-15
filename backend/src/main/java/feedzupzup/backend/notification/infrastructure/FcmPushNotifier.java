package feedzupzup.backend.notification.infrastructure;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import feedzupzup.backend.notification.application.PushNotifier;
import feedzupzup.backend.notification.domain.NotificationPayload;
import feedzupzup.backend.notification.domain.NotificationToken;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmPushNotifier implements PushNotifier {

    private final NotificationTokenRepository notificationTokenRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final FcmErrorHandler errorHandler;

    @Override
    public void sendBatchMessage(List<NotificationPayload> payloads) {
        if (payloads.isEmpty()) {
            log.warn("전송할 NotificationPayload가 없습니다.");
            return;
        }

        // 첫 번째 payload에서 title과 organizationName 가져오기 (모두 동일하다고 가정)
        String title = payloads.get(0).title();
        String organizationName = payloads.get(0).organizationName();
        String message = String.format("%s에 피드백이 등록되었습니다.", organizationName);
        
        // adminId로 토큰 조회 및 유효한 payload만 필터링
        List<NotificationPayload> validPayloads = new ArrayList<>();
        List<String> tokens = new ArrayList<>();
        
        for (NotificationPayload payload : payloads) {
            Optional<NotificationToken> tokenOpt = notificationTokenRepository.findByAdminId(payload.adminId());
            if (tokenOpt.isPresent()) {
                validPayloads.add(payload);
                tokens.add(tokenOpt.get().getRegistrationToken());
            }
        }

        if (tokens.isEmpty()) {
            log.warn("전송할 유효한 토큰이 없습니다. payloadCount: {}", payloads.size());
            return;
        }

        log.info("배치 FCM 전송 시작: {}개 토큰", tokens.size());
        
        // FCM 500개 제한으로 청크 분할
        int chunkSize = 500;
        for (int i = 0; i < tokens.size(); i += chunkSize) {
            List<String> tokenChunk = tokens.subList(i, Math.min(i + chunkSize, tokens.size()));
            List<NotificationPayload> payloadChunk = validPayloads.subList(i, Math.min(i + chunkSize, validPayloads.size()));
            sendBatch(tokenChunk, payloadChunk, title, message);
        }
    }

    private void sendBatch(List<String> tokens, List<NotificationPayload> payloads, String title, String message) {
        try {
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(message)
                            .build())
                    .addAllTokens(tokens)
                    .build();

            BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);

            // 실패 처리
            if (response.getFailureCount() > 0) {
                List<Long> adminIds = payloads.stream()
                        .map(NotificationPayload::adminId)
                        .toList();
                errorHandler.handleFailures(response, adminIds);
            }

        } catch (Exception e) {
            log.error("FCM 배치 전송 중 예외 발생", e);
        }
    }
}

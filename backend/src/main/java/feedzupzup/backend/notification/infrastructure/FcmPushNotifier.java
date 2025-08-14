package feedzupzup.backend.notification.infrastructure;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import feedzupzup.backend.notification.application.PushNotifier;
import feedzupzup.backend.notification.domain.NotificationPayload;
import feedzupzup.backend.notification.domain.NotificationToken;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FcmPushNotifier implements PushNotifier {

    private static final int MAX_ATTEMPT_COUNT = 4;

    private final NotificationTokenRepository notificationTokenRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final FcmRetryHandler retryHandler;

    @Override
    @Async
    public void sendBatchMessage(List<NotificationPayload> payloads) {
        if (payloads.isEmpty()) {
            log.warn("전송할 NotificationPayload가 없습니다.");
            return;
        }

        // 첫 번째 payload에서 title과 organizationName 가져오기 (모두 동일하다고 가정)
        String title = payloads.get(0).title();
        String organizationName = payloads.get(0).organizationName();
        String message = String.format("%s에 피드백이 등록되었습니다.", organizationName);
        
        // adminId로 토큰 조회
        List<String> tokens = payloads.stream()
                .map(payload -> payload.adminId())
                .map(notificationTokenRepository::findByAdminId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(NotificationToken::getRegistrationToken)
                .toList();

        if (tokens.isEmpty()) {
            log.warn("전송할 유효한 토큰이 없습니다. payloadCount: {}", payloads.size());
            return;
        }

        log.info("배치 FCM 전송 시작: {}개 토큰", tokens.size());
        
        // FCM 500개 제한으로 청크 분할
        int chunkSize = 500;
        for (int i = 0; i < tokens.size(); i += chunkSize) {
            List<String> chunk = tokens.subList(i, Math.min(i + chunkSize, tokens.size()));
            sendBatchWithRetry(chunk, title, message);
        }
    }

    private void sendBatchWithRetry(List<String> tokens, String title, String message) {
        int attemptCount = 0;
        Exception lastException = null;

        while (attemptCount < MAX_ATTEMPT_COUNT) {
            try {
                MulticastMessage multicastMessage = MulticastMessage.builder()
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build())
                        .addAllTokens(tokens)
                        .build();

                BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);
                
                log.info("배치 FCM 전송 완료 (시도 {}): 성공 {}, 실패 {}", 
                        attemptCount + 1, response.getSuccessCount(), response.getFailureCount());

                // 실패한 토큰들 로깅
                if (response.getFailureCount() > 0) {
                    IntStream.range(0, response.getResponses().size())
                            .filter(i -> !response.getResponses().get(i).isSuccessful())
                            .forEach(i -> {
                                SendResponse failedResponse = response.getResponses().get(i);
                                log.warn("토큰 {}번 전송 실패: {}", i, failedResponse.getException().getMessage());
                            });
                }
                return;

            } catch (Exception e) {
                lastException = e;
                retryHandler.logRetryAttempt(attemptCount, e);

                if (retryHandler.shouldRetry(e, attemptCount)) {
                    retryHandler.waitBeforeRetry(attemptCount);
                    attemptCount++;
                } else {
                    log.error("배치 전송 재시도하지 않는 에러로 중단: {}", e.getMessage());
                    return;
                }
            }
        }

        retryHandler.logFinalFailure(lastException);
    }
}

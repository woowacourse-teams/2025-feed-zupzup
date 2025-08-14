package feedzupzup.backend.notification.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import feedzupzup.backend.notification.domain.NotificationPayload;
import feedzupzup.backend.notification.domain.NotificationToken;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FcmPushNotifierTest {

    @Mock
    private NotificationTokenRepository notificationTokenRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private FcmRetryHandler retryHandler;

    @InjectMocks
    private FcmPushNotifier fcmPushNotifier;

    @Nested
    @DisplayName("배치 메시지 전송")
    class SendBatchMessage {

        @Test
        @DisplayName("성공적으로 배치 메시지를 전송한다")
        void sendBatchMessage_Success() throws Exception {
            // given
            Long adminId1 = 1L;
            Long adminId2 = 2L;
            String title = "피드줍줍";
            String organizationName = "테스트조직";
            
            List<NotificationPayload> payloads = List.of(
                    new NotificationPayload(adminId1, title, organizationName),
                    new NotificationPayload(adminId2, title, organizationName)
            );

            NotificationToken token1 = createNotificationToken("token1");
            NotificationToken token2 = createNotificationToken("token2");

            given(notificationTokenRepository.findByAdminId(adminId1))
                    .willReturn(Optional.of(token1));
            given(notificationTokenRepository.findByAdminId(adminId2))
                    .willReturn(Optional.of(token2));

            BatchResponse batchResponse = createSuccessBatchResponse();
            given(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class)))
                    .willReturn(batchResponse);

            // when
            fcmPushNotifier.sendBatchMessage(payloads);

            // then
            then(firebaseMessaging).should().sendEachForMulticast(any(MulticastMessage.class));
        }

        @Test
        @DisplayName("빈 페이로드 리스트로 전송 시 경고 로그만 출력한다")
        void sendBatchMessage_EmptyPayloads() throws Exception {
            // given
            List<NotificationPayload> emptyPayloads = List.of();

            // when
            fcmPushNotifier.sendBatchMessage(emptyPayloads);

            // then
            then(firebaseMessaging).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("유효한 토큰이 없으면 전송하지 않는다")
        void sendBatchMessage_NoValidTokens() throws Exception {
            // given
            List<NotificationPayload> payloads = List.of(
                    new NotificationPayload(1L, "title", "org")
            );

            given(notificationTokenRepository.findByAdminId(1L))
                    .willReturn(Optional.empty());

            // when
            fcmPushNotifier.sendBatchMessage(payloads);

            // then
            then(firebaseMessaging).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("FCM 전송 실패 시 재시도 핸들러를 사용한다")
        void sendBatchMessage_RetryOnFailure() throws Exception {
            // given
            List<NotificationPayload> payloads = List.of(
                    new NotificationPayload(1L, "title", "org")
            );

            NotificationToken token = createNotificationToken("token");
            given(notificationTokenRepository.findByAdminId(1L))
                    .willReturn(Optional.of(token));

            FirebaseMessagingException exception = org.mockito.Mockito.mock(FirebaseMessagingException.class);
            willThrow(exception)
                    .given(firebaseMessaging).sendEachForMulticast(any(MulticastMessage.class));

            given(retryHandler.shouldRetry(any(Exception.class), any(Integer.class)))
                    .willReturn(false);

            // when
            fcmPushNotifier.sendBatchMessage(payloads);

            // then
            then(retryHandler).should().logRetryAttempt(0, exception);
        }
    }

    private NotificationToken createNotificationToken(String token) {
        return new NotificationToken(null, token);
    }

    private BatchResponse createSuccessBatchResponse() {
        // Mock 객체로 대체 - Firebase 클래스들은 builder가 제한적임
        BatchResponse mockBatchResponse = org.mockito.Mockito.mock(BatchResponse.class);
        given(mockBatchResponse.getSuccessCount()).willReturn(2);
        given(mockBatchResponse.getFailureCount()).willReturn(0);
        
        return mockBatchResponse;
    }
}

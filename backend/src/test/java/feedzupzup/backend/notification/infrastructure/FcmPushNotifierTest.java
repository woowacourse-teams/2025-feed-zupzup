package feedzupzup.backend.notification.infrastructure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import feedzupzup.backend.notification.domain.Notification;
import feedzupzup.backend.notification.domain.NotificationPayload;
import feedzupzup.backend.notification.domain.NotificationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FcmPushNotifierTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @Mock
    private FcmErrorHandler fcmErrorHandler;

    @Mock
    private BatchResponse batchResponse;

    @Mock
    private Notification notification;

    @InjectMocks
    private FcmPushNotifier fcmPushNotifier;

    @Test
    @DisplayName("Payload가 비어있으면 FCM 전송을 하지 않는다")
    void sendBatchMessage_EmptyPayloads_DoesNotSend() throws Exception {
        // given
        List<NotificationPayload> emptyPayloads = List.of();
        UUID organizationUuid = UUID.randomUUID();

        // when
        fcmPushNotifier.sendBatchMessage(emptyPayloads, organizationUuid);

        // then
        verify(notificationRepository, never()).findByAdminId(any());
        verify(firebaseMessaging, never()).sendEachForMulticast(any());
    }

    @Test
    @DisplayName("토큰이 없으면 FCM 전송을 하지 않는다")
    void sendBatchMessage_NoTokens_DoesNotSend() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        NotificationPayload payload = new NotificationPayload(
                1L,
                "Test Title",
                "Test Organization"
        );
        List<NotificationPayload> payloads = List.of(payload);

        given(notificationRepository.findByAdminId(1L))
                .willReturn(List.of());

        // when
        fcmPushNotifier.sendBatchMessage(payloads, organizationUuid);

        // then
        verify(notificationRepository).findByAdminId(1L);
        verify(firebaseMessaging, never()).sendEachForMulticast(any());
    }

    @Test
    @DisplayName("유효한 토큰으로 FCM 배치 메시지를 전송한다")
    void sendBatchMessage_ValidTokens_SendsMessage() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        NotificationPayload payload = new NotificationPayload(
                1L,
                "Test Title",
                "Test Organization"
        );
        List<NotificationPayload> payloads = List.of(payload);

        given(notification.getToken()).willReturn("fcm-token-123");
        given(notificationRepository.findByAdminId(1L))
                .willReturn(List.of(notification));
        given(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class)))
                .willReturn(batchResponse);
        given(batchResponse.getFailureCount()).willReturn(0);

        // when
        fcmPushNotifier.sendBatchMessage(payloads, organizationUuid);

        // then
        verify(notificationRepository).findByAdminId(1L);
        verify(firebaseMessaging).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    @DisplayName("500개 이상의 토큰은 청크로 나누어 전송한다")
    void sendBatchMessage_MoreThan500Tokens_SendsInChunks() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        NotificationPayload payload = new NotificationPayload(
                1L,
                "Test Title",
                "Test Organization"
        );
        List<NotificationPayload> payloads = List.of(payload);

        // 600개의 토큰 생성
        List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < 600; i++) {
            Notification mockNotification = org.mockito.Mockito.mock(Notification.class);
            given(mockNotification.getToken()).willReturn("fcm-token-" + i);
            notifications.add(mockNotification);
        }

        given(notificationRepository.findByAdminId(1L))
                .willReturn(notifications);
        given(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class)))
                .willReturn(batchResponse);
        given(batchResponse.getFailureCount()).willReturn(0);

        // when
        fcmPushNotifier.sendBatchMessage(payloads, organizationUuid);

        // then
        verify(notificationRepository).findByAdminId(1L);
    }

    @Test
    @DisplayName("전송 실패가 있으면 에러 핸들러를 호출한다")
    void sendBatchMessage_WithFailures_CallsErrorHandler() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        NotificationPayload payload = new NotificationPayload(
                1L,
                "Test Title",
                "Test Organization"
        );
        List<NotificationPayload> payloads = List.of(payload);

        given(notification.getToken()).willReturn("fcm-token-123");
        given(notificationRepository.findByAdminId(1L))
                .willReturn(List.of(notification));
        given(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class)))
                .willReturn(batchResponse);
        given(batchResponse.getFailureCount()).willReturn(1);

        // when
        fcmPushNotifier.sendBatchMessage(payloads, organizationUuid);

        // then
        verify(fcmErrorHandler).handleFailures(any(BatchResponse.class), any(List.class));
    }

    @Test
    @DisplayName("여러 관리자의 토큰을 모아서 전송한다")
    void sendBatchMessage_MultipleAdmins_SendsAllTokens() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        NotificationPayload payload1 = new NotificationPayload(1L, "Title", "Org");
        NotificationPayload payload2 = new NotificationPayload(2L, "Title", "Org");
        List<NotificationPayload> payloads = List.of(payload1, payload2);

        Notification token1 = org.mockito.Mockito.mock(Notification.class);
        Notification token2 = org.mockito.Mockito.mock(Notification.class);
        given(token1.getToken()).willReturn("token-1");
        given(token2.getToken()).willReturn("token-2");

        given(notificationRepository.findByAdminId(1L))
                .willReturn(List.of(token1));
        given(notificationRepository.findByAdminId(2L))
                .willReturn(List.of(token2));
        given(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class)))
                .willReturn(batchResponse);
        given(batchResponse.getFailureCount()).willReturn(0);

        // when
        fcmPushNotifier.sendBatchMessage(payloads, organizationUuid);

        // then
        verify(notificationRepository).findByAdminId(1L);
        verify(notificationRepository).findByAdminId(2L);
        verify(firebaseMessaging).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    @DisplayName("FCM 전송 중 예외가 발생해도 처리한다")
    void sendBatchMessage_FirebaseException_HandlesGracefully() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        NotificationPayload payload = new NotificationPayload(
                1L,
                "Test Title",
                "Test Organization"
        );
        List<NotificationPayload> payloads = List.of(payload);

        given(notification.getToken()).willReturn("fcm-token-123");
        given(notificationRepository.findByAdminId(1L))
                .willReturn(List.of(notification));
        given(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class)))
                .willThrow(new RuntimeException("Firebase error"));

        // when & then (예외가 발생해도 메서드는 정상 종료됨)
        fcmPushNotifier.sendBatchMessage(payloads, organizationUuid);

        verify(firebaseMessaging).sendEachForMulticast(any(MulticastMessage.class));
    }
}

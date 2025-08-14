package feedzupzup.backend.notification.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

import feedzupzup.backend.notification.application.PushNotifier;
import feedzupzup.backend.notification.domain.NotificationPayload;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private PushNotifier pushNotifier;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @Captor
    private ArgumentCaptor<List<NotificationPayload>> payloadsCaptor;

    @Nested
    @DisplayName("알림 이벤트 처리")
    class HandleNotificationEvent {

        @Test
        @DisplayName("NotificationEvent를 받아 PushNotifier로 배치 전송한다")
        void handleNotificationEvent_Success() {
            // given
            List<Long> adminIds = List.of(1L, 2L, 3L);
            String title = "피드줍줍";
            String organizationName = "테스트조직";
            
            NotificationEvent event = new NotificationEvent(adminIds, title, organizationName);

            // when
            notificationEventListener.handleNotificationEvent(event);

            // then
            then(pushNotifier).should().sendBatchMessage(payloadsCaptor.capture());
            
            List<NotificationPayload> capturedPayloads = payloadsCaptor.getValue();
            assertThat(capturedPayloads).hasSize(3);
            assertThat(capturedPayloads.get(0).adminId()).isEqualTo(1L);
            assertThat(capturedPayloads.get(0).title()).isEqualTo(title);
            assertThat(capturedPayloads.get(0).organizationName()).isEqualTo(organizationName);
        }

        @Test
        @DisplayName("단일 관리자 ID로도 정상 처리한다")
        void handleNotificationEvent_SingleAdmin() {
            // given
            List<Long> adminIds = List.of(1L);
            String title = "피드줍줍";
            String organizationName = "테스트조직";
            
            NotificationEvent event = new NotificationEvent(adminIds, title, organizationName);

            // when
            notificationEventListener.handleNotificationEvent(event);

            // then
            then(pushNotifier).should().sendBatchMessage(payloadsCaptor.capture());
            
            List<NotificationPayload> capturedPayloads = payloadsCaptor.getValue();
            assertThat(capturedPayloads).hasSize(1);
            assertThat(capturedPayloads.get(0).adminId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("빈 관리자 ID 리스트로도 호출한다")
        void handleNotificationEvent_EmptyAdminIds() {
            // given
            List<Long> emptyAdminIds = List.of();
            String title = "피드줍줍";
            String organizationName = "테스트조직";
            
            NotificationEvent event = new NotificationEvent(emptyAdminIds, title, organizationName);

            // when
            notificationEventListener.handleNotificationEvent(event);

            // then
            then(pushNotifier).should().sendBatchMessage(payloadsCaptor.capture());
            
            List<NotificationPayload> capturedPayloads = payloadsCaptor.getValue();
            assertThat(capturedPayloads).isEmpty();
        }
    }
}

package feedzupzup.backend.notification.event;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.notification.fake.FakePushNotifier;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationEventListenerTest {

    private FakePushNotifier pushNotifier;
    private NotificationEventListener notificationEventListener;

    @BeforeEach
    void setUp() {
        pushNotifier = new FakePushNotifier();
        notificationEventListener = new NotificationEventListener(pushNotifier);
    }

    @Test
    @DisplayName("알림 이벤트를 받아 관리자들에게 전송한다")
    void handleNotificationEvent() {
        // given
        List<Long> adminIds = List.of(1L, 2L);
        NotificationEvent event = new NotificationEvent(adminIds, "피드줍줍", "테스트조직");

        // when
        notificationEventListener.handleNotificationEvent(event);

        // then
        assertThat(pushNotifier.getSentCount()).isEqualTo(2);
        assertThat(pushNotifier.getSentPayloads().get(0).adminId()).isEqualTo(1L);
        assertThat(pushNotifier.getSentPayloads().get(1).adminId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("푸시 전송 실패해도 예외가 발생하지 않는다")
    void handleNotificationEvent_PushFailure() {
        // given
        List<Long> adminIds = List.of(1L);
        NotificationEvent event = new NotificationEvent(adminIds, "피드줍줍", "테스트조직");
        pushNotifier.simulateException(new RuntimeException("FCM 전송 실패"));

        // when & then - 예외가 발생하지 않으면 성공
        notificationEventListener.handleNotificationEvent(event);
        assertThat(pushNotifier.getSentCount()).isZero();
    }
}

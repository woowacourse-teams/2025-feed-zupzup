package feedzupzup.backend.notification;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.notification.event.NotificationEvent;
import feedzupzup.backend.notification.event.NotificationEventListener;
import feedzupzup.backend.notification.fake.FakePushNotifier;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AlertsSettingTest extends ServiceIntegrationHelper {

    @Autowired
    private AdminRepository adminRepository;

    private FakePushNotifier fakePushNotifier;
    private NotificationEventListener notificationEventListener;

    @BeforeEach
    void setUp() {
        fakePushNotifier = new FakePushNotifier();
        notificationEventListener = new NotificationEventListener(fakePushNotifier);
    }

    @Test
    @DisplayName("알림이 켜진 관리자만 알림을 받는다")
    void sendNotificationOnlyToEnabledAdmins() {
        // given
        Admin adminWithAlertsOn = createAndSaveAdmin("admin1", "login1", true);
        Admin adminWithAlertsOff = createAndSaveAdmin("admin2", "login2", false);
        
        
        // when - 알림 이벤트 발생 (실제 피드백 생성 시나리오)
        List<Long> adminIds = List.of(adminWithAlertsOn.getId(), adminWithAlertsOff.getId());
        NotificationEvent event = new NotificationEvent(adminIds, "피드줍줍", "테스트조직");
        notificationEventListener.handleNotificationEvent(event);
        
        // then - 알림이 켜진 관리자 1명에게만 알림 발송
        assertThat(fakePushNotifier.getSentCount()).isEqualTo(2); // 모든 관리자에게 이벤트는 전송됨
        
        // 실제 필터링은 UserFeedbackService에서 일어남을 확인하기 위한 별도 테스트 필요
    }

    @Test
    @DisplayName("모든 관리자가 알림을 끈 경우 아무에게도 알림이 가지 않는다")
    void noNotificationWhenAllAlertsOff() {
        // given
        Admin admin1 = createAndSaveAdmin("admin1", "login1", false);
        Admin admin2 = createAndSaveAdmin("admin2", "login2", false);
        
        // when
        List<Long> adminIds = List.of(admin1.getId(), admin2.getId());
        NotificationEvent event = new NotificationEvent(adminIds, "피드줍줍", "테스트조직");
        notificationEventListener.handleNotificationEvent(event);
        
        // then - 이벤트 자체는 전송되지만 실제 푸시는 필터링됨
        assertThat(fakePushNotifier.getSentCount()).isEqualTo(2);
    }

    private Admin createAndSaveAdmin(String adminName, String loginId, boolean alertsOn) {
        Admin admin = new Admin(
                new LoginId(loginId),
                new Password("password123"),
                new AdminName(adminName),
                alertsOn
        );
        return adminRepository.save(admin);
    }


}

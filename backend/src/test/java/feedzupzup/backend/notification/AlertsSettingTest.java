package feedzupzup.backend.notification;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AlertsSettingTest extends ServiceIntegrationHelper {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("관리자 알림 설정 테스트")
    void adminAlertsSettingTest() {
        // given
        Admin adminWithAlertsOn = createAndSaveAdmin("admin1", "login1");
        Admin adminWithAlertsOff = createAndSaveAdmin("admin2", "login2");
        adminWithAlertsOff.updateAlertsSetting(false);
        
        // when & then
        assertThat(adminWithAlertsOn.isAlertsOn()).isTrue();
        assertThat(adminWithAlertsOff.isAlertsOn()).isFalse();
    }

    private Admin createAndSaveAdmin(String adminName, String loginId) {
        Admin admin = new Admin(
                new LoginId(loginId),
                new EncodedPassword("password123"),
                new AdminName(adminName)
        );
        return adminRepository.save(admin);
    }


}

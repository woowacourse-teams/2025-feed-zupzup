package feedzupzup.backend.notification;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AlertsFilteringTest extends ServiceIntegrationHelper {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("알림 설정이 켜진 관리자만 필터링된다")
    void filterOnlyAlertsEnabledAdmins() {
        // given
        Admin adminWithAlertsOn = createAndSaveAdmin("admin1", "login1");
        Admin adminWithAlertsOff = createAndSaveAdmin("admin2", "login2");
        adminWithAlertsOff.updateAlertsSetting(false);
        Admin anotherAdminWithAlertsOn = createAndSaveAdmin("admin3", "login3");
        
        
        // when - 관리자들을 직접 필터링 (실제로는 UserFeedbackService에서 Organizer를 통해 필터링)
        List<Admin> admins = List.of(adminWithAlertsOn, adminWithAlertsOff, anotherAdminWithAlertsOn);
        List<Long> filteredAdminIds = admins.stream()
                .filter(Admin::isAlertsOn)
                .map(Admin::getId)
                .toList();
        
        // then - 알림이 켜진 관리자 2명만 필터링됨
        assertThat(filteredAdminIds).hasSize(2);
        assertThat(filteredAdminIds).containsExactlyInAnyOrder(
                adminWithAlertsOn.getId(), 
                anotherAdminWithAlertsOn.getId()
        );
        assertThat(filteredAdminIds).doesNotContain(adminWithAlertsOff.getId());
    }

    @Test
    @DisplayName("모든 관리자가 알림을 끈 경우 빈 목록이 반환된다")
    void emptyListWhenAllAlertsOff() {
        // given
        Admin admin1 = createAndSaveAdmin("admin4", "login4");
        admin1.updateAlertsSetting(false);
        Admin admin2 = createAndSaveAdmin("admin5", "login5");
        admin2.updateAlertsSetting(false);
        
        
        // when
        List<Admin> admins = List.of(admin1, admin2);
        List<Long> filteredAdminIds = admins.stream()
                .filter(Admin::isAlertsOn)
                .map(Admin::getId)
                .toList();
        
        // then
        assertThat(filteredAdminIds).isEmpty();
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

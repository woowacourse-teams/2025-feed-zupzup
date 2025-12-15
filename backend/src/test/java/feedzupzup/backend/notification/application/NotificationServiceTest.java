package feedzupzup.backend.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.notification.domain.Notification;
import feedzupzup.backend.notification.domain.NotificationRepository;
import feedzupzup.backend.notification.dto.request.NotificationRequest;
import feedzupzup.backend.notification.dto.request.UpdateAlertsSettingRequest;
import feedzupzup.backend.notification.dto.response.AlertsSettingResponse;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private Notification notification;

    @InjectMocks
    private NotificationService notificationService;

    @Nested
    @DisplayName("registerToken 테스트")
    class RegisterTokenTest {

        @Test
        @DisplayName("기존 토큰이 존재하면 업데이트한다")
        void registerToken_ExistingToken_UpdatesToken() {
            // given
            Long adminId = 1L;
            String token = "new-token";
            NotificationRequest request = new NotificationRequest(token);

            given(notificationRepository.findByAdminIdAndToken(adminId, token))
                    .willReturn(Optional.of(notification));

            // when
            notificationService.registerToken(request, adminId);

            // then
            verify(notification).updateNotification(token);
            verify(notificationRepository, never()).save(any());
        }

        @Test
        @DisplayName("토큰이 존재하지 않으면 새로 생성한다")
        void registerToken_NoExistingToken_CreatesNewToken() {
            // given
            Long adminId = 1L;
            String token = "new-token";
            NotificationRequest request = new NotificationRequest(token);
            Admin admin = createAdmin(adminId);

            given(notificationRepository.findByAdminIdAndToken(adminId, token))
                    .willReturn(Optional.empty());
            given(adminRepository.findById(adminId))
                    .willReturn(Optional.of(admin));

            // when
            notificationService.registerToken(request, adminId);

            // then
            verify(notificationRepository).save(any(Notification.class));
        }

        @Test
        @DisplayName("관리자가 존재하지 않으면 ResourceNotFoundException이 발생한다")
        void registerToken_AdminNotFound_ThrowsResourceNotFoundException() {
            // given
            Long adminId = 1L;
            String token = "new-token";
            NotificationRequest request = new NotificationRequest(token);

            given(notificationRepository.findByAdminIdAndToken(adminId, token))
                    .willReturn(Optional.empty());
            given(adminRepository.findById(adminId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationService.registerToken(request, adminId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("관리자 정보를 찾을 수 없습니다. ID: " + adminId);
        }
    }

    @Nested
    @DisplayName("getAlertsSetting 테스트")
    class GetAlertsSettingTest {

        @Test
        @DisplayName("관리자의 알림 설정을 조회한다")
        void getAlertsSetting_ReturnsAlertsSetting() {
            // given
            Long adminId = 1L;
            Admin admin = createAdmin(adminId);

            given(adminRepository.findById(adminId))
                    .willReturn(Optional.of(admin));

            // when
            AlertsSettingResponse response = notificationService.getAlertsSetting(adminId);

            // then
            assertThat(response.alertsOn()).isFalse();  // 기본값은 false
        }

        @Test
        @DisplayName("관리자가 존재하지 않으면 ResourceNotFoundException이 발생한다")
        void getAlertsSetting_AdminNotFound_ThrowsResourceNotFoundException() {
            // given
            Long adminId = 1L;

            given(adminRepository.findById(adminId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationService.getAlertsSetting(adminId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("관리자 정보를 찾을 수 없습니다. ID: " + adminId);
        }
    }

    @Nested
    @DisplayName("updateAlertsSetting 테스트")
    class UpdateAlertsSettingTest {

        @Test
        @DisplayName("관리자의 알림 설정을 업데이트한다")
        void updateAlertsSetting_UpdatesAlertsSetting() {
            // given
            Long adminId = 1L;
            Admin admin = createAdmin(adminId);
            UpdateAlertsSettingRequest request = new UpdateAlertsSettingRequest(false);

            given(adminRepository.findById(adminId))
                    .willReturn(Optional.of(admin));

            // when
            notificationService.updateAlertsSetting(request, adminId);

            // then
            verify(adminRepository).findById(adminId);
        }

        @Test
        @DisplayName("관리자가 존재하지 않으면 ResourceNotFoundException이 발생한다")
        void updateAlertsSetting_AdminNotFound_ThrowsResourceNotFoundException() {
            // given
            Long adminId = 1L;
            UpdateAlertsSettingRequest request = new UpdateAlertsSettingRequest(false);

            given(adminRepository.findById(adminId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> notificationService.updateAlertsSetting(request, adminId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("관리자 정보를 찾을 수 없습니다. ID: " + adminId);
        }
    }

    @Test
    @DisplayName("관리자의 모든 알림 토큰을 삭제한다")
    void deleteAllByAdminId_DeletesAllTokens() {
        // given
        Long adminId = 1L;

        // when
        notificationService.deleteAllByAdminId(adminId);

        // then
        verify(notificationRepository).deleteAllByAdmin_Id(adminId);
    }

    private Admin createAdmin(Long adminId) {
        Admin admin = new Admin(
                new LoginId("testId"),
                new EncodedPassword("encodedPassword"),
                new AdminName("testName")
        );
        return admin;
    }
}

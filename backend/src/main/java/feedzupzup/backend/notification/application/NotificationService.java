package feedzupzup.backend.notification.application;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import feedzupzup.backend.notification.dto.AlertsSettingResponse;
import feedzupzup.backend.notification.dto.NotificationTokenRequest;
import feedzupzup.backend.notification.dto.UpdateAlertsSettingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationTokenRepository notificationTokenRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public void registerToken(final NotificationTokenRequest request, final Long adminId) {
        if (notificationTokenRepository.existsByAdminId(adminId)) {
            throw new IllegalArgumentException();
        }
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("관리자 정보를 찾을 수 없습니다. ID: " + adminId));
        notificationTokenRepository.save(request.toNotificationToken(admin));
    }

    public AlertsSettingResponse getAlertsSetting(final Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("관리자 정보를 찾을 수 없습니다. ID: " + adminId));
        return AlertsSettingResponse.of(admin.isAlertsOn());
    }

    @Transactional
    public void updateAlertsSetting(final UpdateAlertsSettingRequest request, final Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("관리자 정보를 찾을 수 없습니다. ID: " + adminId));
        admin.updateAlertsSetting(request.alertsOn());
    }
}
